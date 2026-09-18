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
 * <p>The profile covers the country-interaction, scripted-trigger and
 * country-event fixture shapes used by For Vivhite: Subject Territory
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
            "5BCDC8852AF7DD5633D04BB4C2C01113CA85F3B06A2A466B23301109BC641D55",
            "country_interactions/samanta_upgrades.txt",
            "382F27F0672DDC89263B34374D0C509679C55D5A121F110702F5179037358020");

    public static final Map<String, String> VANILLA_EVENT_EVIDENCE_SHA256 = Map.of(
            "events/readme.txt",
            "2794AF93B213B6A03E27CA72D4B038B3AB398DEB2912B14E98141CF1A6ED668B",
            "events/debug/qa_debug.txt",
            "26664F3B8013C860DCF96690D5BD6E7544A3544198128FB9B56E13BEDF30976E",
            "events/economy/banking.txt",
            "2CA6AC2A36370FCFBDC4FBD172AFCEE8B63BB9D5DF489CC576794CE8DD06141A",
            "events/rebels.txt",
            "3E7360307A62DAEAB9D2153E0BC9F2FB8DBDDE89F5C17BB0E69755B01B3EF61B",
            "events/DHE/flavor_chi.txt",
            "105519F6B69970C866EAFB23FCD1B135F41649AE46F7C08B02F0E03A7DE837DC",
            "events/DHE/flavor_RUS.txt",
            "91282A9A36CFFA9276989CE77D33368758E494D5837E459CE4D7468B02EB2BDB",
            "events/institution_events.txt",
            "8787B67BAE64518CFF6C013C2F4A90CEB2E903913099F6A2E99A68295C9BEE3A",
            "events/situations/hussite_wars.txt",
            "CD1259E4143605932FA8C5004FF9C632BB93A65E0441799B510C10A7AA28EF0B");

    public static final Map<String, String> VANILLA_WAR_FIXTURE_EVIDENCE_SHA256 = Map.of(
            "events/situations/treaty_of_tordesillas.txt",
            "31717CCF1D12D20F6EA6D7294D59AE2A32F3F89E59B11024561337D87978760A",
            "events/debug/000_johan_debug.txt",
            "DD0A4CE6DF80ADDD95EFDE7381DD9B600EAFBDD276AC5CA2F7FC6BC74497DC7A",
            "common/parliament_issues/10_hre_issues.txt",
            "A550B3CC15D43978D6FE8D48B778F7AF4A4C1124E6A04666141764DEF5D53BA7");

    public static final Map<String, String> VANILLA_CREATED_COUNTRY_IO_EVIDENCE_SHA256 = Map.of(
            "events/situations/fall_of_delhi.txt",
            "FF0D1D1D45EC8A859C2218F0F38D00471B5C3ED1FAF65CCFD7D39E86981A98B3",
            "common/scripted_effects/international_organization_effects.txt",
            "66DEFD13B110B76DF72772DE58E9010E0030BF0A326DD0D90E4C46D5F5523DCE");

    private static final Set<String> STRUCTURAL = structuralKeys();
    private static final Map<String, OpcodeSpec> OPCODES = opcodesByName();

    @Override public String id() { return ID; }
    @Override public String gameVersion() { return GAME_VERSION; }
    @Override public String executableSha256() { return EXE_SHA256; }
    @Override public OpcodeSpec opcode(String name) { return name == null ? null : OPCODES.get(name); }
    @Override public Set<String> allowedStructuralKeys() { return STRUCTURAL; }
    @Override public Map<String, OpcodeSpec> opcodes() { return OPCODES; }

    @Override
    public boolean isStructuralKey(String name) {
        return STRUCTURAL.contains(name)
                || isScopeLinkKey(name);
    }

    @Override
    public boolean isScopeLinkKey(String name) {
        return name != null && (name.matches("location:[a-z0-9_]+")
                || name.matches("c:[A-Za-z0-9_]+(?:\\.capital(?:\\.region)?)?")
                || name.matches("international_organization:[A-Za-z0-9_]+"));
    }

    @Override
    public boolean walkOpcodeBlock(String name) {
        return "create_country_from_location".equals(name)
                || "create_building_country_in_location".equals(name);
    }

    @Override
    public boolean isOpaqueStructuralBlock(String name) {
        return "reforms".equals(name);
    }

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
                "AND", "OR", "NOR", "NAND", "MAX",
                "category", "ai_tick", "use_enroute", "block_when_at_war",
                "show_message", "show_message_to_target",
                "scope:actor", "scope:recipient",
                "any_subject", "every_subject", "any_subject_or_below",
                "any_owned_location", "any_location_in_region", "every_location_in_region",
                "every_in_list", "every_current_war",
                "capital", "capital.region", "scope:recipient.capital.region", "owner",
                "select_trigger", "looking_for_a", "interaction_source_list",
                "target_flag", "none_available_msg_key", "show_why_not_enabled",
                "column", "data", "visible", "enabled", "custom_tooltip",
                "trigger_if", "count", "list",
                "xcrt_is_eligible_direct_subject_target",
                "xcrt_is_transferable_location",
                "xcrt_has_at_most_transferable_locations",
                "xcrt_recipient_respects_tusi_cap",
                "xcrt_frozen_transfer_list_respects_tusi_cap",
                "namespace", "option", "outcome", "orphan", "historical_option",
                "hidden_effect", "overlord", "subject_type", "reforms");
        return Collections.unmodifiableSet(keys);
    }

    private static Map<String, OpcodeSpec> opcodesByName() {
        LinkedHashMap<String, OpcodeSpec> result = new LinkedHashMap<>();
        add(result, "exists", OpcodeSpec.Kind.TRIGGER);
        add(result, "at_war", OpcodeSpec.Kind.TRIGGER);
        add(result, "country_type", OpcodeSpec.Kind.TRIGGER);
        add(result, "country_rank", OpcodeSpec.Kind.TRIGGER);
        add(result, "is_subject_of", OpcodeSpec.Kind.TRIGGER);
        add(result, "is_subject_type", OpcodeSpec.Kind.TRIGGER);
        add(result, "subject_type_is_not_locked", OpcodeSpec.Kind.TRIGGER);
        add(result, "num_locations", OpcodeSpec.Kind.TRIGGER);
        add(result, "region", OpcodeSpec.Kind.TRIGGER);
        add(result, "is_ownable", OpcodeSpec.Kind.TRIGGER);
        add(result, "this", OpcodeSpec.Kind.TRIGGER);
        add(result, "is_subject_or_below_of", OpcodeSpec.Kind.TRIGGER);
        add(result, "always", OpcodeSpec.Kind.TRIGGER);
        add(result, "tag", OpcodeSpec.Kind.TRIGGER);
        add(result, "has_global_variable", OpcodeSpec.Kind.TRIGGER);
        add(result, "country_exists", OpcodeSpec.Kind.TRIGGER);
        add(result, "any_war_participant", OpcodeSpec.Kind.TRIGGER);
        result.put("list_size", new OpcodeSpec(
                "list_size", OpcodeSpec.Kind.TRIGGER, 2, 2,
                Set.of("THIS", "this"), GAME_VERSION, Set.of("name", "value")));
        add(result, "add_to_list", OpcodeSpec.Kind.EFFECT);
        add(result, "change_location_owner", OpcodeSpec.Kind.EFFECT);
        add(result, "discover_location", OpcodeSpec.Kind.EFFECT);
        add(result, "create_country_from_location", OpcodeSpec.Kind.EFFECT);
        add(result, "create_building_country_in_location", OpcodeSpec.Kind.EFFECT);
        add(result, "define_unique_country_tag", OpcodeSpec.Kind.EFFECT);
        add(result, "change_country_name", OpcodeSpec.Kind.EFFECT);
        add(result, "change_country_adjective", OpcodeSpec.Kind.EFFECT);
        add(result, "set_global_variable", OpcodeSpec.Kind.EFFECT);
        add(result, "set_capital", OpcodeSpec.Kind.EFFECT);
        add(result, "make_subject_of", OpcodeSpec.Kind.EFFECT);
        add(result, "declare_war", OpcodeSpec.Kind.EFFECT);
        add(result, "white_peace", OpcodeSpec.Kind.EFFECT);
        add(result, "set_country_rank", OpcodeSpec.Kind.EFFECT);
        add(result, "set_variable", OpcodeSpec.Kind.EFFECT);
        add(result, "lock_current_subject_type", OpcodeSpec.Kind.EFFECT);
        return Collections.unmodifiableMap(result);
    }

    private static void add(Map<String, OpcodeSpec> target, String name, OpcodeSpec.Kind kind) {
        target.put(name, new OpcodeSpec(
                name, kind, 0, Integer.MAX_VALUE,
                Set.of("THIS", "this"), GAME_VERSION, Set.of()));
    }
}
