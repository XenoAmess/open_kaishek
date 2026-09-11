package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Exact-build compatibility projection for the companion strategic-power query.
 *
 * <p>The provider owns the native reader, paused-frame binding and MCP server.
 * This profile freezes the additive active-war target scope without granting
 * open_kaishek gameplay-process authority or an action surface.</p>
 */
public final class ActiveWarStrategicPowerCapabilityProfile {
    public static final String ROOT_PROVIDER_COMMIT =
            "283904d5438e07c18a49903333eafcaaabb75e80";
    public static final String ROOT_PYTHON_SCOPE_COMMIT =
            "81f76e04704305df214e34961a32b768ce14a485";
    public static final String ROOT_R470_EVIDENCE_COMMIT =
            "4ac06131ce04a4af02cbe9898b8eca780c8aa181";
    public static final String ROOT_R470_REPORT_SHA256 =
            "CCF29894130EB673C59FDAD03E39D15A6BEB649392C6A2D02E1DF8A92C7E022D";
    public static final String ROOT_R471_EVIDENCE_COMMIT =
            "050c94fbd2ba9ecd41f4419e1dc936bd7c083774";
    public static final String ROOT_R471_REPORT_SHA256 =
            "F467676201497A75C08ED5F6C72AFE64618337C73EFD2BA816B981470CE1E7CD";
    public static final String ROOT_R471_RECLASSIFICATION_SHA256 =
            "D8F43EABC2A38F451FCAB1FE8DAEEB157EF8C62439B904DF96E8AFA301E924C9";
    public static final String ROOT_R471_RUNNER_SHA256 =
            "A364FCFB89AAD6C25DABAB72AD708BB564793B484B79B983AB3DCE11ACC10679";
    public static final String ROOT_R471_RUNNER_TEST_SHA256 =
            "F14E420ECE5784BC6B3B2F7FBB9DF79E8D13416D8AE2955ECC069B684110FED3";
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
    public static final String ROOT_NATIVE_FRAME_SHA256 =
            "51F1830553A353C52A8AD757550791CFC556ECEB13BE7F07857361C901F9F2B9";
    public static final String ROOT_NATIVE_READER_SHA256 =
            "9448C95D787B88453085FA9AB76C895AC72B2911F320877396DF9962E4207D74";
    public static final String ROOT_BRIDGE_CPP_SHA256 =
            "25D62A80967B9AD41090C6310D4BE21A8B0A1F7F17FCEE02884CEC60425666AC";
    public static final String ROOT_NATIVE_UNIT_TEST_SHA256 =
            "AF8D6425C40C220FD866F2A49C4C4E5B8183EB0C329310B8EDC6455098B1B135";
    public static final String ROOT_NATIVE_SOURCE_CONTRACT_TEST_SHA256 =
            "927FFE8451423F7D09835B6FF280C4E83FFF4486C56DCF35A1D0B9ACE3B81058";
    public static final String ROOT_NATIVE_ABI_SHA256 =
            "4F786F156356F3B52EE1D1C408CF4A2912E154867793ECF2E75A3728D0F1385A";
    public static final String ROOT_NATIVE_CANDIDATE_DLL_SHA256 =
            "65C14FE284EA99036DBFBA950B3BE38C3656FA2D064017FD8A38FF21B32B61EF";

    public static final String TOOL_ID = "ck3_query_war_entry_assessments";
    public static final String CAPABILITY_ID =
            "game.command.query-war-entry-assessments-v1-N";
    public static final String STEP_TEMPLATE =
            "query-war-entry-assessments-v1-1-<target_character_id>";
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-active-war-strategic-power-v3";
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
                            "r470_native_reader_rejected_active_war_only_target_as_target_not_declarable",
                            "corrected_native_frame_freezes_declaration_and_active_war_sources_separately",
                            "corrected_native_reader_admits_either_source",
                            "r471_two_official_queries_are_identical_on_one_unchanged_paused_frame",
                            "r471_native_command_history_contains_only_two_successful_read_only_queries",
                            "r471_report_red_is_confined_to_the_corrected_history_field_audit",
                            "strategic_power_observation_does_not_certify_campaign_dominance",
                            "query_does_not_enable_declaration_surrender_or_white_peace",
                            "parameterized_query_never_authorizes_an_autonomous_action",
                            "unknown_mcp_arguments_are_rejected",
                            "contract_does_not_depend_on_machine_account_path_or_ck3_round"),
                    true,
                    true,
                    true,
                    true);

    private ActiveWarStrategicPowerCapabilityProfile() { }
}
