package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/** Public compatibility projection for the companion's played-owner Workforce query. */
public final class ZhongguoWorkforceOwnerSnapshotCapabilityProfile {
    public static final String ID = "ck3-1.19.0.6-zg361-workforce-owner-snapshot-v1";
    public static final String CAPABILITY_ID =
            "game.command.query-zhongguo-workforce-owner-snapshot-v1";
    public static final String STEP_ID = "query-zhongguo-workforce-owner-snapshot-v1";
    public static final String MCP_TOOL_ID = "ck3_query_zhongguo_workforce_owner_snapshot_v1";
    public static final String GAME_VERSION = "1.19.0.6";
    public static final String EXECUTABLE_SHA256 =
            "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86";
    public static final String ROOT_SCHEMA_PATH = "ck3_autonomous_player/schemas/zhongguo-workforce-owner-snapshot-v1.schema.json";
    public static final String ROOT_SCHEMA_SHA256 = "647C5252DFDC64D58F9FCF448DE8816B6B66EF6591485D7B7702ABD51A00A10C";
    public static final String ROOT_FIXTURE_PATH = "ck3_autonomous_player/tests/fixtures/zhongguo_workforce_owner_snapshot_v1.json";
    public static final String ROOT_FIXTURE_SHA256 = "22774411C20DF0C0D65350183C54F4E1F5B9121CF720006DFE90F6EAB85AB66A";
    public static final String NATIVE_BACKEND_ID =
            "ck3-1.19.0.6-native-zhongguo-workforce-owner-snapshot-v1";
    public static final String PUBLIC_BACKEND_ID = "native-headless";
    public static final List<String> REQUEST_FIELDS =
            List.of("request_nonce", "expected_revision");
    public static final List<String> PUBLIC_ENVELOPE_FIELDS = List.of("build", "source", "binding");
    public static final List<String> TYPED_VALUE_FIELDS =
            List.of("status", "value", "unavailable_reason");
    public static final List<String> TERMINAL_KINDS =
            List.of("none", "success", "history_accruing", "not_applicable");

    public static final CapabilityDescriptor SNAPSHOT = new CapabilityDescriptor(
            CAPABILITY_ID,
            ID,
            List.of(
                    "schema_version",
                    "status",
                    "capability",
                    "source_backend_id",
                    "request_nonce",
                    "snapshot_revision",
                    "date_raw",
                    "paused",
                    "player_character_id",
                    "subject_character_id",
                    "unavailable_reason",
                    "readiness.case_identity_ready",
                    "readiness.player_owner_binding_ready",
                    "readiness.portfolio_subject_binding_ready",
                    "readiness.ready",
                    "readiness.same_frame_ready",
                    "terminal",
                    "workforce.central.subject_character_id",
                    "workforce.central.cycle_serial",
                    "workforce.central.case_serial",
                    "workforce.central.stage11_status",
                    "workforce.source.status",
                    "workforce.source.owner_character_id",
                    "workforce.source.subject_character_id",
                    "workforce.source.p2c_cycle_serial",
                    "workforce.source.p2c_case_serial",
                    "workforce.source.al_cycle_serial",
                    "workforce.source.al_case_serial",
                    "workforce.al_case.owner_character_id",
                    "workforce.al_case.subject_character_id",
                    "workforce.al_case.cycle_serial",
                    "workforce.al_case.case_serial",
                    "workforce.al_case.state",
                    "workforce.al_case.active",
                    "workforce.al_case.revision",
                    "workforce.m360_receipt.owner_character_id",
                    "workforce.m360_receipt.subject_character_id",
                    "workforce.m360_receipt.cycle_serial",
                    "workforce.m360_receipt.case_serial",
                    "workforce.m360_receipt.state",
                    "workforce.m360_receipt.choice",
                    "workforce.portfolio.closed",
                    "workforce.portfolio.status",
                    "workforce.portfolio.cycle_serial",
                    "workforce.portfolio.final_conservation_ok",
                    "workforce.portfolio.terminal_history_accruing",
                    "workforce.portfolio.history_cycle_count",
                    "workforce.portfolio.terminal_success",
                    "workforce.portfolio.terminal_na",
                    "workforce.portfolio.terminal_reason",
                    "workforce.portfolio.terminal_owned_operations",
                    "workforce.portfolio.terminal_skipped_manager_only",
                    "workforce.portfolio.terminal_skipped_charter",
                    "terminal_kind"),
            List.of(
                    "request_contains_only_nonce_and_expected_revision",
                    "owner_is_the_paused_played_character",
                    "subject_is_observed_from_the_owners_central_binding",
                    "m360_source_when_present_correlates_central_and_al_identities",
                    "central_case_and_al_case_keep_distinct_serials",
                    "readiness_describes_observability_and_does_not_imply_terminal",
                    "terminal_kind_separates_success_history_accruing_and_not_applicable",
                    "not_applicable_requires_explicit_product_terminal_flags",
                    "missing_m360_source_does_not_block_observing_a_legitimate_not_applicable_terminal",
                    "portfolio_closure_and_central_stage11_callback_status_are_separate",
                    "typed_unavailable_is_distinct_from_zero_false_and_not_applicable",
                    "action_ack_is_never_a_business_postcondition"),
            true, true, false, false);

    private ZhongguoWorkforceOwnerSnapshotCapabilityProfile() { }
}
