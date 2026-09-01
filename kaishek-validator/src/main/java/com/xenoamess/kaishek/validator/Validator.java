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
        walk(document.children(), domain, profile, out, sourcePath, 0,
                initialSide(domain));
        return List.copyOf(out);
    }
    /** Profile-api entry point; keeps validator independent of concrete profile modules. */
    public static List<Diagnostic> validate(ParseResult parsed, String sourcePath, GameProfile profile) {
        return validate(parsed, sourcePath, KaishekProfile.fromGameProfile(profile));
    }
    private static void walk(List<CstNode> nodes, ScriptDomain domain, KaishekProfile profile,
                             List<Diagnostic> out, String path, int depth,
                             ScriptSide side) {
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
            ScriptSide childSide = childSide(side, key, e);
            // A file-root block is a declaration map, where duplicate names
            // can hide an earlier definition.  Nested CK3 blocks are ordered
            // executable sequences (and may intentionally repeat an opcode),
            // so do not apply map-duplicate semantics to them.
            if (depth == 0 && seen.putIfAbsent(key, e) != null)
                out.add(diag("DUPLICATE_KEY", Diagnostic.Severity.ERROR, "duplicate key in the same block: " + key, e.span(), at));
            OpcodeSpec spec = profile.opcode(key);
            boolean opcodePosition = depth > 0;
            boolean scalarRootOpcode = depth == 0 && !(e.value() instanceof BlockNode) &&
                    (domain == ScriptDomain.SCRIPTED_EFFECTS || domain == ScriptDomain.SCRIPTED_TRIGGERS || domain == ScriptDomain.SCRIPTED_VALUES);
            if ((opcodePosition || scalarRootOpcode) && spec == null
                    && !profile.allowedStructuralKeys().contains(key)
                    // CK3 1.19.0.6 accepts calculated-value blocks for
                    // trigger ranges.  Treat the expression as one opaque
                    // value here so its `value`/`add` terms are not reported
                    // as generic opcodes; direct `=` still emits the
                    // dedicated schema-only diagnostic above.
                    && !calculatedValueExpression)
                out.add(diag("UNKNOWN_OPCODE", Diagnostic.Severity.ERROR, "unregistered opcode: " + key, e.key().span(), at));
            // A registered opcode is unambiguous even at file root; declarations
            // (event/scripted-effect IDs) are simply absent from the registry.
            if (spec != null) {
                validateDomain(spec, domain, e, out, at);
                validateParameters(spec, e, out, at);
                validateScope(spec, e, out, at);
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
                        && spec.kind() != OpcodeSpec.Kind.INTERFACE;
                if (!argumentBlock && !calculatedValueExpression) {
                    walk(b.children(), domain, profile, out, at, depth + 1,
                            childSide);
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
    private enum ScriptSide { OTHER, TRIGGER, EFFECT }
    private static void validateDomain(OpcodeSpec spec, ScriptDomain domain, EntryNode e, List<Diagnostic> out, String path) {
        boolean trigger = domain == ScriptDomain.SCRIPTED_TRIGGERS;
        boolean effect = domain == ScriptDomain.SCRIPTED_EFFECTS || domain == ScriptDomain.ON_ACTION;
        boolean value = domain == ScriptDomain.SCRIPTED_VALUES;
        if ((trigger && spec.kind() == OpcodeSpec.Kind.EFFECT) || (effect && spec.kind() == OpcodeSpec.Kind.TRIGGER) ||
            (value && spec.kind() != OpcodeSpec.Kind.VALUE && spec.kind() != OpcodeSpec.Kind.STRUCTURAL))
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
    private static Diagnostic diag(String c, Diagnostic.Severity s, String m, SourceSpan span, String p) { return new Diagnostic(c, s, m, p, span); }
}
