package com.xenoamess.kaishek.profile;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Version-pinned static schema slice for Stellaris 4.4.6.
 *
 * <p>This profile deliberately recognizes only the decision/deposit/scripted-trigger source
 * shapes needed by the initial Infinite Jobs acceptance corpus. Recognition
 * is not runtime certification: no opcode in this slice is executable by the
 * finite runtime and the real game remains the semantic authority.</p>
 */
public final class StellarisProfile446 implements KaishekProfile {
    public static final String ID = "stellaris-4.4.6";
    public static final String GAME_ID = "stellaris";
    public static final String GAME_VERSION = "4.4.6";
    public static final String EXE_SHA256 =
            "BC451C72D9654C8901F1BB0BEE1DD78D76F415465C2FBF746E9F98ADE333173A";

    private static final Set<String> STRUCTURAL = structuralKeys();
    private static final Map<String, OpcodeSpec> OPCODES = opcodesByName();

    @Override public String id() { return ID; }
    @Override public String gameVersion() { return GAME_VERSION; }
    @Override public String executableSha256() { return EXE_SHA256; }
    @Override public OpcodeSpec opcode(String name) { return name == null ? null : OPCODES.get(name); }
    @Override public Set<String> allowedStructuralKeys() { return STRUCTURAL; }
    @Override public Map<String, OpcodeSpec> opcodes() { return OPCODES; }

    @Override
    public ScriptDomain domainForPath(String sourcePath) {
        return ScriptDomain.fromPath(sourcePath);
    }

    private static Set<String> structuralKeys() {
        TreeSet<String> keys = new TreeSet<>(KaishekProfile.DEFAULT_STRUCTURAL_KEYS);
        Collections.addAll(keys,
                "owned_planets_only", "enactment_time", "resources", "category", "cost", "minerals",
                "ai_weight", "is_for_colonizable", "should_swap_deposit_on_terraforming", "owner");
        return Collections.unmodifiableSet(keys);
    }

    private static Map<String, OpcodeSpec> opcodesByName() {
        LinkedHashMap<String, OpcodeSpec> result = new LinkedHashMap<>();
        add(result, "add_deposit", OpcodeSpec.Kind.EFFECT, 0, 0,
                Set.of("PLANET", "planet", "SHIP", "ship"));
        add(result, "always", OpcodeSpec.Kind.TRIGGER, 0, 0, "THIS");
        add(result, "exists", OpcodeSpec.Kind.TRIGGER, 0, 0, "THIS");
        add(result, "is_gestalt", OpcodeSpec.Kind.TRIGGER, 0, 0, "COUNTRY");
        add(result, "has_valid_civic", OpcodeSpec.Kind.TRIGGER, 0, 0, "COUNTRY");
        add(result, "has_carrier_flag", OpcodeSpec.Kind.TRIGGER, 0, 0,
                Set.of("PLANET", "planet", "SHIP", "ship"));
        add(result, "is_nomadic", OpcodeSpec.Kind.TRIGGER, 0, 0, "COUNTRY");
        add(result, "is_planet_class", OpcodeSpec.Kind.TRIGGER, 0, 0,
                Set.of("PLANET", "planet", "SHIP", "ship"));
        add(result, "uses_district_set", OpcodeSpec.Kind.TRIGGER, 0, 0,
                Set.of("PLANET", "planet", "SHIP", "ship"));
        add(result, "set_carrier_flag", OpcodeSpec.Kind.EFFECT, 0, 0,
                Set.of("PLANET", "planet", "SHIP", "ship"));
        add(result, "remove_carrier_flag", OpcodeSpec.Kind.EFFECT, 0, 0,
                Set.of("PLANET", "planet", "SHIP", "ship"));
        // A scripted-trigger symbol is both a root declaration container and
        // a scalar call at use sites. Model the exact observed symbol as
        // structural so its declaration body is still checked fail-closed.
        add(result, "vivhite_workplace_supported_colony", OpcodeSpec.Kind.STRUCTURAL,
                0, Integer.MAX_VALUE, Set.of("PLANET", "planet", "SHIP", "ship"));

        add(result, "OR", OpcodeSpec.Kind.STRUCTURAL, 0, Integer.MAX_VALUE, "THIS");

        // These container shapes are intentionally opaque. Their child keys
        // are dynamic modifier identifiers, not executable script opcodes.
        add(result, "planet_modifier", OpcodeSpec.Kind.VALUE, 0, Integer.MAX_VALUE, "PLANET");
        add(result, "country_modifier", OpcodeSpec.Kind.VALUE, 0, Integer.MAX_VALUE, "COUNTRY");
        add(result, "modifier", OpcodeSpec.Kind.VALUE, 0, Integer.MAX_VALUE, "PLANET");
        add(result, "drop_weight", OpcodeSpec.Kind.VALUE, 0, Integer.MAX_VALUE, "THIS");

        // Unlike modifier maps, this is an executable structural container:
        // recurse so its potential triggers are still checked fail-closed.
        add(result, "triggered_planet_modifier", OpcodeSpec.Kind.STRUCTURAL,
                0, Integer.MAX_VALUE, "PLANET");
        return Collections.unmodifiableMap(result);
    }

    private static void add(Map<String, OpcodeSpec> target, String name, OpcodeSpec.Kind kind,
                            int minimum, int maximum, String scope) {
        target.put(name, new OpcodeSpec(name, kind, minimum, maximum,
                Set.of(scope, scope.toLowerCase(java.util.Locale.ROOT)), GAME_VERSION, Set.of()));
    }

    private static void add(Map<String, OpcodeSpec> target, String name, OpcodeSpec.Kind kind,
                            int minimum, int maximum, Set<String> scopes) {
        target.put(name, new OpcodeSpec(name, kind, minimum, maximum,
                scopes, GAME_VERSION, Set.of()));
    }
}
