package com.xenoamess.kaishek.profile;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Exact-build static schema slice for EU5 1.3.11, Steam Build 24187685.
 *
 * <p>The profile intentionally covers only the country-interaction and
 * scripted-trigger shapes used by For Vivhite: Subject Territory
 * Consolidation. It provides fail-closed source validation, not executable
 * EU5 runtime semantics.</p>
 */
public final class Eu5Profile1311 implements KaishekProfile {
    public static final String ID = "eu5-1.3.11-build-24187685";
    public static final String GAME_VERSION = "1.3.11";
    public static final String STEAM_BUILD_ID = "24187685";
    public static final String EXE_SHA256 =
            "C0DB888DA5E132CD6AB50C2C531C7CAE419488BEA0054CF8AC348616332683EF";

    public static final Map<String, String> VANILLA_EVIDENCE_SHA256 = Map.of(
            "country_interactions/give_location_to_subject.txt",
            "82F5BDF0C0C85637E7E1D81C9722D09083E7672FA08CFC8D9F91C6F9F6A489F3",
            "country_interactions/move_subject_capital.txt",
            "7C103EE37FF67C8FC6118EA44144D3E7C42BA8C6FD1A4E266F043CB67808E6F0",
            "subject_types/readme.txt",
            "06A72F97064D4F88BD1582DD50890A337DDD95B602F275DC57407F33BF842837",
            "subject_types/colonial_nation.txt",
            "F39406149EEC6A7DBC84BAC977C9831B081D32667DAEB56B2C7B92806483AF58",
            "subject_types/state_bank.txt",
            "C90271BCA606E9F3129E2EE57D169112C50F4D2E066F312F49D621863A302BAE",
            "subject_types/trade_company.txt",
            "5BCDC8852AF7DD5633D04BB4C2C01113CA85F3B06A2A466B23301109BC641D55");

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
        if (sourcePath != null) {
            String normalized = sourcePath.replace('\\', '/').toLowerCase(Locale.ROOT);
            if (normalized.contains("/country_interactions/")
                    || normalized.startsWith("country_interactions/")) {
                return ScriptDomain.INTERACTIONS;
            }
        }
        return ScriptDomain.fromPath(sourcePath);
    }

    private static Set<String> structuralKeys() {
        TreeSet<String> keys = new TreeSet<>(KaishekProfile.DEFAULT_STRUCTURAL_KEYS);
        Collections.addAll(keys,
                "AND", "OR", "NOR", "MAX",
                "category", "ai_tick", "use_enroute", "block_when_at_war",
                "show_message", "show_message_to_target",
                "scope:actor", "scope:recipient",
                "any_subject", "every_subject", "any_subject_or_below",
                "any_owned_location", "any_location_in_region", "every_location_in_region",
                "every_in_list", "capital", "capital.region", "scope:recipient.capital.region", "owner",
                "select_trigger", "looking_for_a", "interaction_source_list",
                "target_flag", "none_available_msg_key", "show_why_not_enabled",
                "column", "data", "visible", "enabled", "custom_tooltip",
                "trigger_if", "count", "list",
                "xcrt_is_eligible_direct_subject_target",
                "xcrt_is_transferable_location",
                "xcrt_has_at_most_transferable_locations",
                "xcrt_recipient_respects_tusi_cap",
                "xcrt_frozen_transfer_list_respects_tusi_cap");
        return Collections.unmodifiableSet(keys);
    }

    private static Map<String, OpcodeSpec> opcodesByName() {
        LinkedHashMap<String, OpcodeSpec> result = new LinkedHashMap<>();
        add(result, "exists", OpcodeSpec.Kind.TRIGGER);
        add(result, "at_war", OpcodeSpec.Kind.TRIGGER);
        add(result, "country_type", OpcodeSpec.Kind.TRIGGER);
        add(result, "is_subject_of", OpcodeSpec.Kind.TRIGGER);
        add(result, "is_subject_type", OpcodeSpec.Kind.TRIGGER);
        add(result, "num_locations", OpcodeSpec.Kind.TRIGGER);
        add(result, "region", OpcodeSpec.Kind.TRIGGER);
        add(result, "is_ownable", OpcodeSpec.Kind.TRIGGER);
        add(result, "this", OpcodeSpec.Kind.TRIGGER);
        add(result, "is_subject_or_below_of", OpcodeSpec.Kind.TRIGGER);
        result.put("list_size", new OpcodeSpec(
                "list_size", OpcodeSpec.Kind.TRIGGER, 2, 2,
                Set.of("THIS", "this"), GAME_VERSION, Set.of("name", "value")));
        add(result, "add_to_list", OpcodeSpec.Kind.EFFECT);
        add(result, "change_location_owner", OpcodeSpec.Kind.EFFECT);
        return Collections.unmodifiableMap(result);
    }

    private static void add(Map<String, OpcodeSpec> target, String name, OpcodeSpec.Kind kind) {
        target.put(name, new OpcodeSpec(
                name, kind, 0, Integer.MAX_VALUE,
                Set.of("THIS", "this"), GAME_VERSION, Set.of()));
    }
}
