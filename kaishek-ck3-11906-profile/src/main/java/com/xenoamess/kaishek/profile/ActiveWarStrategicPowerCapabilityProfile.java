package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static compatibility projection for the companion strategic-power query.
 *
 * <p>The provider owns the native reader, paused-frame binding and MCP server.
 * This profile freezes the additive active-war target scope without granting
 * open_kaishek gameplay-process authority or an action surface.</p>
 */
public final class ActiveWarStrategicPowerCapabilityProfile {
    public static final String ROOT_PROVIDER_COMMIT =
            "81f76e04704305df214e34961a32b768ce14a485";
    public static final String ROOT_MCP_SERVER_SHA256 =
            "3723D21778A5DEAD2D7539BEC9E35E95BB040CE7478334446F375879B2918DBA";
    public static final String ROOT_PYTHON_CONTRACT_SHA256 =
            "0FCA88E320BD2EFBA75371AC54568F984923BABFE06953A0A30D244E4CF50BDA";
    public static final String ROOT_SERVICE_SHA256 =
            "CD0855232359148329426D26846F3CC77FA3E0C923BF17BC90383283AE26D813";
    public static final String ROOT_NATIVE_DRIVER_SHA256 =
            "5265BC19A3A8003D8A8551BAD50025CE8722979F45C8C99D30EC0CF8AF125ED5";
    public static final String ROOT_BRIDGE_TEST_SHA256 =
            "13F794BE2B79F30C37ED99AF2064C53D424101F6E7E0130E3AF407AC5A584440";
    public static final String ROOT_CONTRACT_TEST_SHA256 =
            "67A601A0D4867B3224EDDED69B2769B7054944ADAB6467D0FBFDE721A4C6F533";

    public static final String TOOL_ID = "ck3_query_war_entry_assessments";
    public static final String CAPABILITY_ID =
            "game.command.query-war-entry-assessments-v1-N";
    public static final String STEP_TEMPLATE =
            "query-war-entry-assessments-v1-1-<target_character_id>";
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-active-war-strategic-power-v1";
    public static final int SCHEMA_VERSION = 1;
    public static final int MAXIMUM_TARGETS = 1;

    public static final List<String> REQUEST_FIELDS = List.of(
            "target_character_ids", "expected_revision");
    public static final List<String> TARGET_SOURCES = List.of(
            "declarable_war", "active_war_primary_opponent");

    public static final CapabilityDescriptor QUERY =
            new CapabilityDescriptor(
                    TOOL_ID,
                    PROFILE_VERSION,
                    List.of(
                            "schema_version",
                            "status",
                            "step",
                            "accepted",
                            "query_sequence",
                            "target_character_ids",
                            "target_scopes",
                            "war_entry_assessments",
                            "queried_snapshot_id",
                            "queried_revision",
                            "queried_native_revision",
                            "backend_id"),
                    List.of(
                            "request_contains_exactly_one_positive_signed_int32_character_id",
                            "target_must_be_a_current_declaration_or_active_war_primary_opponent",
                            "target_scope_sources_are_reported_in_stable_order",
                            "target_scope_is_rechecked_after_the_native_result",
                            "query_is_bound_to_one_unchanged_paused_snapshot",
                            "native_schema_v1_payload_and_exact_build_binding_are_preserved",
                            "native_targets_declarable_ready_is_a_legacy_approved_scope_spelling",
                            "query_does_not_enable_declaration_surrender_or_white_peace",
                            "parameterized_query_never_authorizes_an_autonomous_action",
                            "unknown_mcp_arguments_are_rejected",
                            "contract_does_not_depend_on_machine_account_path_or_ck3_round"),
                    true,
                    true,
                    true,
                    false);

    private ActiveWarStrategicPowerCapabilityProfile() { }
}
