package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/**
 * Static contract for the exact-build B3 manager/subordinate selector query.
 *
 * <p>The companion project owns the native selector, driver, service, and MCP
 * transport. This profile records only the public read-only query and its
 * observable response boundary. It neither registers a Paradox opcode nor
 * advertises the downstream manager-governance action cell.</p>
 */
public final class ZhongguoManagerSubordinateSelectorCapabilityProfile {
    public static final String ID =
            "ck3-1.19.0.6-zg361-manager-subordinate-selector-v1";
    public static final String CAPABILITY_ID =
            "game.command.query-zhongguo-manager-subordinate-selector-v1";
    public static final String STEP_ID =
            "query-zhongguo-manager-subordinate-selector-v1";
    public static final String SELECTOR_KIND =
            "zg361-bounded-ai-direct-manager-selection-v1";
    public static final String GAME_VERSION = "1.19.0.6";
    public static final String EXECUTABLE_SHA256 =
            "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86";
    public static final String ROOT_ABI_SHA256 =
            "b10d596bdc18842c4a582a932affd12fd035382c879477625a18f6a4417bf55a";
    public static final String ROOT_SOURCE_CONTRACT_SHA256 =
            "b175ea8231e22614c144abad1b108b9f43ed60a9f9e36406ca00017700851057";
    public static final String ROOT_PYTHON_CONTRACT_SHA256 =
            "26e5e2ce6ddc13c496ac497c476de3ebf935788b386ae6fdf5a58c049332a7b7";
    public static final String ROOT_INTEGRATION_COMMIT =
            "fefb408e13c4ea2aa4c512d3e3900991f9c13f7b";
    public static final boolean DOWNSTREAM_ACTION_CAPABILITY_ADVERTISED = false;

    public static final CapabilityDescriptor SELECTOR =
            new CapabilityDescriptor(
                    CAPABILITY_ID,
                    ID,
                    List.of(
                            "schema_version",
                            "status",
                            "selector_kind",
                            "request_nonce",
                            "snapshot_revision",
                            "date_raw",
                            "paused",
                            "player_character_id",
                            "provider_observed",
                            "selection.manager_character_id",
                            "selection.subordinate_character_id",
                            "selection.manager_contract_id",
                            "selection.subordinate_contract_id",
                            "selection.manager_primary_title_id",
                            "selection.manager_primary_title_tier_raw",
                            "selection.manager_primary_title_tier_key",
                            "selection.manager_government_key",
                            "readiness.exact_build_ready",
                            "readiness.player_binding_ready",
                            "readiness.relationship_enumeration_ready",
                            "readiness.manager_eligibility_ready",
                            "readiness.direct_subordinate_ready",
                            "readiness.same_frame_ready",
                            "readiness.ready",
                            "unavailable_reason",
                            "provenance.game_version",
                            "provenance.executable_sha256",
                            "provenance.subject_contract_storage_slot_rva",
                            "provenance.subject_contract_fallback_slot_rva",
                            "provenance.immediate_liege_rva",
                            "provenance.primary_title_rva",
                            "provenance.effective_government_rva",
                            "provenance.is_human_player_rva",
                            "manager_character_id",
                            "subordinate_character_id",
                            "binding.request_nonce",
                            "binding.snapshot_id",
                            "binding.revision",
                            "binding.native_revision",
                            "binding.date_raw",
                            "binding.paused",
                            "binding.player_character_id",
                            "query_sequence",
                            "backend_id"),
                    List.of(
                            "query_is_read_only_on_one_paused_application_main_frame",
                            "public_request_is_nonce_plus_expected_revision_only",
                            "caller_cannot_supply_character_or_eligibility_assertions",
                            "manager_is_living_ai_celestial_landed_duke_or_higher_direct_vassal",
                            "subordinate_immediate_liege_is_selected_manager",
                            "first_complete_pair_preserves_nested_native_contract_order",
                            "two_complete_selection_reads_are_identical",
                            "frame_before_and_after_are_identical",
                            "unavailable_result_never_leaks_a_candidate",
                            "bad_relationship_storage_is_not_reclassified_as_no_candidate",
                            "exact_build_provenance_is_fixed"),
                    true,
                    true,
                    false,
                    false);

    private ZhongguoManagerSubordinateSelectorCapabilityProfile() { }
}
