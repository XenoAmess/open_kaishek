package com.xenoamess.kaishek.validator;

import com.xenoamess.kaishek.profile.*;
import com.xenoamess.kaishek.syntax.*;
import java.util.*;

/** Strict, schema-aware validator. It only reports diagnostics; it never rewrites CST. */
public final class Validator {
    /**
     * CK3 1.19.0.6 exact-build evidence rejects calculated-value blocks on a
     * direct {@code var:x = { ... }} trigger equality.  Keep this distinct
     * from generic UNKNOWN_OPCODE diagnostics so an offline preflight can
     * point at the loader failure without claiming that the expression is
     * executable.  Range comparisons ({@code >=}/{@code <=}) are deliberately
     * outside this diagnostic until separate evidence says otherwise.
     */
    public static final String CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED =
            "CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED";
    public static final String INVALID_SCALAR_VALUE = "INVALID_SCALAR_VALUE";
    public static final String INVALID_CURRENT_SCOPE = "INVALID_CURRENT_SCOPE";

    private static final Set<String> CALCULATED_VALUE_TERMS = Set.of(
            "value", "add", "subtract", "multiply");

    private Validator() {}

    public static List<Diagnostic> validate(ParseResult parsed, String sourcePath, KaishekProfile profile) {
        Objects.requireNonNull(parsed);
        var result = new ArrayList<Diagnostic>();
        for (var d : parsed.diagnostics())
            result.add(new Diagnostic(d.code(), Diagnostic.Severity.valueOf(d.severity().name()), d.message(), sourcePath, d.span()));
        result.addAll(validate(parsed.document(), sourcePath, profile));
        return List.copyOf(result);
    }
    public static List<Diagnostic> validate(Document document, String sourcePath, KaishekProfile profile) {
        Objects.requireNonNull(document); Objects.requireNonNull(profile);
        List<Diagnostic> out = new ArrayList<>();
        ScriptDomain domain = profile.domainForPath(sourcePath);
        if (domain == ScriptDomain.UNKNOWN)
            out.add(diag("UNKNOWN_DIRECTORY", Diagnostic.Severity.ERROR, "no schema profile for directory", document.span(), sourcePath));
        if (isEu5EventSlice(domain, profile))
            validateEu5EventDeclarations(document, out, sourcePath);
        walk(document.children(), domain, profile, out, sourcePath, 0,
                initialSide(domain), ScriptScope.UNKNOWN);
        return List.copyOf(out);
    }

    private static boolean isEu5EventSlice(ScriptDomain domain, KaishekProfile profile) {
        return domain == ScriptDomain.EVENTS
                && "eu5-1.3.11-build-24187685".equals(profile.id());
    }

    /** The exact-build readme requires a namespace and numbered event declarations. */
    private static void validateEu5EventDeclarations(Document document,
                                                      List<Diagnostic> out, String path) {
        List<EntryNode> roots = document.children().stream()
                .filter(EntryNode.class::isInstance)
                .map(EntryNode.class::cast)
                .toList();
        EntryNode namespaceEntry = roots.stream()
                .filter(e -> "namespace".equals(e.key().text().trim()))
                .findFirst().orElse(null);
        if (namespaceEntry == null || namespaceEntry.value() == null) {
            out.add(diag("EU5_EVENT_NAMESPACE_REQUIRED", Diagnostic.Severity.ERROR,
                    "event file requires namespace = <id> before numbered declarations",
                    document.span(), path));
            return;
        }
        String namespace = namespaceEntry.value().text().trim();
        if (!namespace.matches("[A-Za-z][A-Za-z0-9_]*")) {
            out.add(diag("EU5_EVENT_NAMESPACE_INVALID", Diagnostic.Severity.ERROR,
                    "event namespace must be an identifier", namespaceEntry.value().span(), path));
            return;
        }
        if (!roots.isEmpty() && roots.get(0) != namespaceEntry)
            out.add(diag("EU5_EVENT_NAMESPACE_ORDER", Diagnostic.Severity.ERROR,
                    "event namespace must be the first declaration",
                    namespaceEntry.key().span(), path));
        for (EntryNode root : roots) {
            if (root == namespaceEntry) continue;
            String key = root.key().text().trim();
            String prefix = namespace + ".";
            String number = key.startsWith(prefix) ? key.substring(prefix.length()) : "";
            boolean numbered = number.matches("[0-9]{1,4}");
            int eventNumber = numbered ? Integer.parseInt(number) : 0;
            if (!numbered || eventNumber < 1 || eventNumber > 9999
                    || !(root.value() instanceof BlockNode block)) {
                out.add(diag("EU5_EVENT_ID_INVALID", Diagnostic.Severity.ERROR,
                        "event declaration must be " + namespace + ".<1..9999> = { ... }",
                        root.key().span(), path + "." + key));
                continue;
            }
            EntryNode type = block.entries().stream()
                    .filter(e -> "type".equals(e.key().text().trim()))
                    .findFirst().orElse(null);
            if (type == null || type.value() == null
                    || !"country_event".equals(type.value().text().trim()))
                out.add(diag("EU5_EVENT_TYPE_OUT_OF_SLICE", Diagnostic.Severity.ERROR,
                        "this exact-build event slice covers type = country_event only",
                        root.key().span(), path + "." + key));
        }
    }
    /** Profile-api entry point; keeps validator independent of concrete profile modules. */
    public static List<Diagnostic> validate(ParseResult parsed, String sourcePath, GameProfile profile) {
        return validate(parsed, sourcePath, KaishekProfile.fromGameProfile(profile));
    }
    private static void walk(List<CstNode> nodes, ScriptDomain domain, KaishekProfile profile,
                             List<Diagnostic> out, String path, int depth,
                             ScriptSide side, ScriptScope scope) {
        Map<String, EntryNode> seen = new HashMap<>();
        for (CstNode n : nodes) {
            if (!(n instanceof EntryNode e)) continue;
            // GUI declaration headers such as `types Foo {}` and
            // `blockoverride "name" {}` intentionally have no `=` operator.
            // Their nested widget vocabulary is outside the Phase 0 semantic
            // opcode table; parser diagnostics still cover malformed syntax,
            // so do not misclassify these declarations as unknown opcodes.
            if (domain == ScriptDomain.GUI_REGISTRATION && e.operator() == null) continue;
            String key = e.key().text().trim();
            String at = path + "." + key;
            boolean triggerSide = side == ScriptSide.TRIGGER;
            if (triggerSide && isCk3Profile11906(profile)
                    && isDirectCalculatedValueEquality(e)) {
                out.add(diag(CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED,
                        Diagnostic.Severity.ERROR,
                        "CK3 1.19.0.6 schema-only: calculated-value block on direct var equality is not certified; materialize the arithmetic before comparing",
                        e.key().span(), at));
            }
            // Track CK3's evaluation side at block boundaries.  A `limit` or
            // `trigger` block inside an effect is trigger-side (the observed
            // phase-two loader RED), while set/change/save/effect blocks stay
            // effect-side and must not acquire this trigger-only diagnostic.
            boolean calculatedValueExpression = triggerSide
                    && isCk3Profile11906(profile)
                    && isCalculatedValueExpression(e);
            boolean variableComparison = triggerSide
                    && isCk3Profile11906(profile)
                    && isScalarVariableComparison(e);
            ScriptSide childSide = childSide(side, key, e);
            ScriptScope childScope = childScope(scope, key, e, domain, profile, depth);
            // A file-root block is a declaration map, where duplicate names
            // can hide an earlier definition.  Nested CK3 blocks are ordered
            // executable sequences (and may intentionally repeat an opcode),
            // so do not apply map-duplicate semantics to them.
            if (depth == 0 && seen.putIfAbsent(key, e) != null)
                out.add(diag("DUPLICATE_KEY", Diagnostic.Severity.ERROR, "duplicate key in the same block: " + key, e.span(), at));
            OpcodeSpec spec = profile.opcode(key);
            if (profile.isScopeLinkKey(key) && !(e.value() instanceof BlockNode))
                out.add(diag("SCOPE_LINK_REQUIRES_BLOCK", Diagnostic.Severity.ERROR,
                        "left-hand scope link " + key + " must contain a script block",
                        e.key().span(), at));
            boolean opcodePosition = depth > 0;
            boolean scalarRootOpcode = depth == 0 && !(e.value() instanceof BlockNode) &&
                    (domain == ScriptDomain.SCRIPTED_EFFECTS || domain == ScriptDomain.SCRIPTED_TRIGGERS || domain == ScriptDomain.SCRIPTED_VALUES);
            if ((opcodePosition || scalarRootOpcode) && spec == null
                    && !profile.isStructuralKey(key)
                    // CK3 1.19.0.6 accepts calculated-value blocks for
                    // trigger ranges.  Treat the expression as one opaque
                    // value here so its `value`/`add` terms are not reported
                    // as generic opcodes; direct `=` still emits the
                    // dedicated schema-only diagnostic above.
                    && !calculatedValueExpression
                    // CK3 trigger predicates also allow a scoped variable
                    // reference on the left of a scalar comparison.  This is
                    // a source shape, not an opcode or executable runtime
                    // capability, so keep it out of the registry while still
                    // accepting the exact profile-bound syntax.
                    && !variableComparison)
                out.add(diag("UNKNOWN_OPCODE", Diagnostic.Severity.ERROR, "unregistered opcode: " + key, e.key().span(), at));
            // A registered opcode is unambiguous even at file root; declarations
            // (event/scripted-effect IDs) are simply absent from the registry.
            if (spec != null) {
                validateDomain(spec, domain, e, out, at, side, profile);
                validateParameters(spec, e, out, at);
                validateScope(spec, e, out, at);
                validateScalarValue(spec, e, out, at);
                validateCurrentScope(spec, scope, e, out, at);
            }
            if (e.value() instanceof BlockNode b) {
                // A registered non-structural opcode owns its RHS block as a
                // typed argument object (`name = { ... }`, `flag = { ... }`,
                // etc.).  Walking those fields as executable opcodes would
                // report every parameter name as UNKNOWN_OPCODE and would
                // make a valid CST→IR slice impossible.  Structural control
                // words such as `random` still recurse because their block is
                // an executable child sequence rather than a parameter map.
                boolean argumentBlock = spec != null
                        && spec.kind() != OpcodeSpec.Kind.STRUCTURAL
                        && spec.kind() != OpcodeSpec.Kind.INTERFACE
                        && !profile.walkOpcodeBlock(key);
                if (!argumentBlock && !calculatedValueExpression
                        && !profile.isOpaqueStructuralBlock(key)) {
                    walk(b.children(), domain, profile, out, at, depth + 1,
                            childSide, childScope);
                }
            }
        }
    }

    private static boolean isCk3Profile11906(KaishekProfile profile) {
        return "ck3-1.19.0.6".equals(profile.id())
                && "1.19.0.6".equals(profile.gameVersion());
    }

    private static ScriptSide initialSide(ScriptDomain domain) {
        return switch (domain) {
            case SCRIPTED_TRIGGERS -> ScriptSide.TRIGGER;
            case SCRIPTED_EFFECTS, ON_ACTION -> ScriptSide.EFFECT;
            default -> ScriptSide.OTHER;
        };
    }

    private static ScriptSide childSide(ScriptSide parent, String key, EntryNode entry) {
        if (!(entry.value() instanceof BlockNode)) return parent;
        String normalized = key.toLowerCase(Locale.ROOT);
        // Condition containers are trigger-side even when the file itself is
        // an effect script (`limit` is the common phase-two loader case).
        if (normalized.equals("limit") || normalized.equals("trigger")
                || normalized.equals("potential") || normalized.equals("allow")
                || normalized.equals("check")) return ScriptSide.TRIGGER;
        // Explicit effect branches and effect-side variable/scope blocks are
        // never reinterpreted as trigger expressions.
        if (normalized.equals("effect") || normalized.equals("effects")
                || normalized.equals("option") || normalized.equals("immediate")
                || normalized.equals("after") || normalized.equals("hidden_effect")
                || normalized.equals("then") || normalized.equals("else")
                || normalized.equals("else_if") || normalized.equals("set_variable")
                || normalized.equals("change_variable") || normalized.equals("save_scope_as")
                || normalized.equals("save_scope_value_as")
                || normalized.equals("save_temporary_scope_as")
                || normalized.equals("save_temporary_scope_value_as")
                || normalized.equals("use_saved_scope_as")) return ScriptSide.EFFECT;
        return parent;
    }

    /** Match a trigger-side calculated-value comparison block. */
    private static boolean isCalculatedValueExpression(EntryNode entry) {
        if (entry.operator() == null) return false;
        String operator = entry.operator().text().trim();
        if (!operator.equals("=") && !operator.equals(">=") && !operator.equals("<=")) return false;
        String key = entry.key().text().trim().toLowerCase(Locale.ROOT);
        if (!key.startsWith("var:")) return false;
        if (!(entry.value() instanceof BlockNode block)) return false;
        return block.entries().stream().map(child -> child.key().text().trim().toLowerCase(Locale.ROOT))
                .anyMatch(CALCULATED_VALUE_TERMS::contains);
    }

    /** Match only the loader-proven direct {@code =} form, never a range. */
    private static boolean isDirectCalculatedValueEquality(EntryNode entry) {
        return entry.operator() != null
                && "=".equals(entry.operator().text().trim())
                && isCalculatedValueExpression(entry);
    }

    /** Match a trigger-side scalar comparison whose left operand is a CK3 variable reference. */
    private static boolean isScalarVariableComparison(EntryNode entry) {
        if (entry.operator() == null || entry.value() instanceof BlockNode) return false;
        String key = entry.key().text().trim().toLowerCase(Locale.ROOT);
        if (!key.startsWith("var:") || key.length() == "var:".length()) return false;
        return switch (entry.operator().text().trim()) {
            case "=", "!=", ">", "<", ">=", "<=" -> true;
            default -> false;
        };
    }
    private enum ScriptSide { OTHER, TRIGGER, EFFECT }
    private enum ScriptScope { UNKNOWN, COUNTRY, LOCATION, INTERNATIONAL_ORGANIZATION }

    private static ScriptScope childScope(ScriptScope parent, String key, EntryNode entry,
                                          ScriptDomain domain, KaishekProfile profile,
                                          int depth) {
        if (!(entry.value() instanceof BlockNode block)) return parent;
        if (isEu5EventSlice(domain, profile) && depth == 0) {
            boolean countryEvent = block.entries().stream().anyMatch(child ->
                    "type".equals(child.key().text().trim())
                            && child.value() != null
                            && "country_event".equals(child.value().text().trim()));
            if (countryEvent) return ScriptScope.COUNTRY;
        }
        if (isEu5EventSlice(domain, profile)
                && ("create_country_from_location".equals(key)
                || "create_building_country_in_location".equals(key)))
            return ScriptScope.COUNTRY;
        if (!profile.isScopeLinkKey(key)) return parent;
        String normalized = key.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("c:")) return ScriptScope.COUNTRY;
        if (normalized.startsWith("location:")) return ScriptScope.LOCATION;
        if (normalized.startsWith("international_organization:"))
            return ScriptScope.INTERNATIONAL_ORGANIZATION;
        return ScriptScope.UNKNOWN;
    }
    private static void validateDomain(OpcodeSpec spec, ScriptDomain domain, EntryNode e,
                                       List<Diagnostic> out, String path, ScriptSide side,
                                       KaishekProfile profile) {
        boolean trigger = domain == ScriptDomain.SCRIPTED_TRIGGERS;
        boolean effect = domain == ScriptDomain.SCRIPTED_EFFECTS || domain == ScriptDomain.ON_ACTION;
        boolean value = domain == ScriptDomain.SCRIPTED_VALUES;
        boolean eu5Event = isEu5EventSlice(domain, profile);
        // Effect/on_action files legitimately embed registered trigger
        // predicates inside condition containers (`limit`, `trigger`,
        // `potential`, `allow`, or `check`).  `childSide` marks only those
        // containers as TRIGGER; direct effect-side predicates stay RED and
        // all other domains retain their existing checks.
        boolean triggerInEffectCondition = effect
                && side == ScriptSide.TRIGGER
                && spec.kind() == OpcodeSpec.Kind.TRIGGER;
        if ((trigger && spec.kind() == OpcodeSpec.Kind.EFFECT)
                || (effect && spec.kind() == OpcodeSpec.Kind.TRIGGER && !triggerInEffectCondition) ||
            (value && spec.kind() != OpcodeSpec.Kind.VALUE && spec.kind() != OpcodeSpec.Kind.STRUCTURAL) ||
            (eu5Event && side == ScriptSide.TRIGGER && spec.kind() == OpcodeSpec.Kind.EFFECT) ||
            (eu5Event && side == ScriptSide.EFFECT && spec.kind() == OpcodeSpec.Kind.TRIGGER))
            out.add(diag("WRONG_DOMAIN", Diagnostic.Severity.ERROR, "opcode " + spec.name() + " is " + spec.kind() + " but file domain is " + domain, e.key().span(), path));
    }
    private static void validateParameters(OpcodeSpec spec, EntryNode e, List<Diagnostic> out, String path) {
        if (!(e.value() instanceof BlockNode b)) return;
        List<EntryNode> parameters = new ArrayList<>();
        for (EntryNode parameter : b.entries()) {
            String name = parameter.key().text().trim();
            // CK3 parameter blocks are ordered and may legally repeat a
            // named field (for example, repeated value/add terms).  Keep
            // every occurrence for arity/declared-name checks; duplicate
            // diagnostics belong only to executable/structural sibling
            // sequences in walk(), where a repeated key is ambiguous.
            if (!name.equals("scope")) parameters.add(parameter);
        }
        Set<String> declared = spec.parameterNames();
        if (!declared.isEmpty()) {
            for (EntryNode parameter : parameters) {
                String name = parameter.key().text().trim();
                if (!declared.contains(name)) {
                    out.add(diag("INVALID_PARAMETERS", Diagnostic.Severity.ERROR,
                            "parameter " + name + " is not declared by opcode " + spec.name(),
                            parameter.key().span(), path + "." + name));
                }
            }
        }
        int count = parameters.size();
        if (count < spec.minParameters() || count > spec.maxParameters())
            out.add(diag("INVALID_PARAMETERS", Diagnostic.Severity.ERROR, "opcode " + spec.name() + " expects " + spec.minParameters() + ".." + (spec.maxParameters() == Integer.MAX_VALUE ? "*" : spec.maxParameters()) + " parameters, got " + count, e.value().span(), path));
    }
    private static void validateScope(OpcodeSpec spec, EntryNode e, List<Diagnostic> out, String path) {
        if (spec.allowedScopes().isEmpty() || !(e.value() instanceof BlockNode b)) return;
        for (EntryNode child : b.entries()) if (child.key().text().trim().equals("scope") && child.value() != null) {
            String scope = child.value().text().trim();
            if (!spec.allowedScopes().contains(scope)) out.add(diag("INVALID_SCOPE", Diagnostic.Severity.ERROR, "scope " + scope + " is not valid for " + spec.name(), child.value().span(), path + ".scope"));
        }
    }
    private static void validateScalarValue(OpcodeSpec spec, EntryNode e,
                                            List<Diagnostic> out, String path) {
        if (spec.scalarValuePattern().isBlank()) return;
        String value = e.value() instanceof BlockNode || e.value() == null
                ? null : e.value().text().trim();
        if (!spec.acceptsScalarValue(value))
            out.add(diag(INVALID_SCALAR_VALUE, Diagnostic.Severity.ERROR,
                    "opcode " + spec.name() + " requires scalar value matching "
                            + spec.scalarValuePattern(),
                    e.value() == null ? e.key().span() : e.value().span(), path));
    }
    private static void validateCurrentScope(OpcodeSpec spec, ScriptScope scope, EntryNode e,
                                             List<Diagnostic> out, String path) {
        if (scope == ScriptScope.UNKNOWN || spec.allowedScopes().isEmpty()
                || spec.allowedScopes().contains("THIS")
                || spec.allowedScopes().contains("this")) return;
        if (!spec.allowedScopes().contains(scope.name()))
            out.add(diag(INVALID_CURRENT_SCOPE, Diagnostic.Severity.ERROR,
                    "opcode " + spec.name() + " requires one of " + spec.allowedScopes()
                            + " but current scope is " + scope,
                    e.key().span(), path));
    }
    private static Diagnostic diag(String c, Diagnostic.Severity s, String m, SourceSpan span, String p) { return new Diagnostic(c, s, m, p, span); }
}
