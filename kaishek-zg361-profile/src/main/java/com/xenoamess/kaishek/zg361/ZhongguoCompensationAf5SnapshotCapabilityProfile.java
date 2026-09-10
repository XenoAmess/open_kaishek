package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/** Public compatibility projection for the companion's standalone AF5 query. */
public final class ZhongguoCompensationAf5SnapshotCapabilityProfile {
    public static final String ID =
            "ck3-1.19.0.6-zg361-compensation-af5-snapshot-v1";
    public static final String CAPABILITY_ID =
            "game.command.query-zhongguo-compensation-af5-snapshot-v1";
    public static final String STEP_ID =
            "query-zhongguo-compensation-af5-snapshot-v1";
    public static final String MCP_TOOL_ID =
            "ck3_query_zhongguo_compensation_af5_snapshot_v1";
    public static final String GAME_VERSION = "1.19.0.6";
    public static final String EXECUTABLE_SHA256 =
            "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86";
    public static final String ROOT_SCHEMA_PATH =
            "ck3_autonomous_player/schemas/zhongguo-compensation-af5-snapshot-v1.schema.json";
    public static final String ROOT_SCHEMA_SHA256 =
            "B8839712AE999E49C7B3EE5E20DBDA14C9DBD0BB8C0511CEA6F83FE903933EBB";
    public static final String NATIVE_BACKEND_ID =
            "ck3-1.19.0.6-native-zhongguo-compensation-af5-snapshot-v1";
    public static final String PUBLIC_BACKEND_ID = "native-headless";
    public static final List<String> REQUEST_FIELDS =
            List.of("request_nonce", "expected_revision");
    public static final List<String> PUBLIC_ENVELOPE_FIELDS =
            List.of("build", "source", "binding");
    public static final List<String> TYPED_VALUE_FIELDS =
            List.of("status", "value", "unavailable_reason");

    public static final CapabilityDescriptor SNAPSHOT = new CapabilityDescriptor(
            CAPABILITY_ID,
            ID,
            List.of(
                    "schema_version", "status", "capability", "source_backend_id",
                    "request_nonce", "snapshot_revision", "date_raw", "paused",
                    "player_character_id", "subject_character_id", "unavailable_reason",
                    "af5.portfolio.domain", "af5.portfolio.stage",
                    "af5.portfolio.completed_cycle", "af5.portfolio.visible_pending",
                    "af5.portfolio.result_identity.owner_character_id",
                    "af5.portfolio.result_identity.subject_character_id",
                    "af5.portfolio.result_identity.cycle_serial",
                    "af5.portfolio.result_identity.case_serial",
                    "af5.case.identity.owner_character_id",
                    "af5.case.identity.subject_character_id",
                    "af5.case.identity.cycle_serial", "af5.case.identity.case_serial",
                    "af5.case.identity.revision", "af5.case.result_case_serial", "af5.case.state",
                    "af5.case.last_operation", "af5.case.last_route", "af5.case.active",
                    "af5.case.repurchase_resolved", "af5.case.unit_conserved",
                    "af5.m299.identity.owner_character_id",
                    "af5.m299.identity.subject_character_id",
                    "af5.m299.identity.cycle_serial", "af5.m299.identity.case_serial",
                    "af5.m299.state", "af5.m299.route", "af5.m299.active", "af5.m299.consumed",
                    "af5.m300.identity.owner_character_id",
                    "af5.m300.identity.subject_character_id",
                    "af5.m300.identity.cycle_serial", "af5.m300.identity.case_serial",
                    "af5.m300.state", "af5.m300.route", "af5.m300.active", "af5.m300.consumed",
                    "readiness.player_owner_binding_ready",
                    "readiness.portfolio_subject_binding_ready",
                    "readiness.same_case_identity_ready", "readiness.same_frame_ready",
                    "readiness.ready", "terminal"),
            List.of(
                    "request_contains_only_nonce_and_expected_revision",
                    "owner_is_the_paused_played_character",
                    "subject_is_observed_from_the_bound_portfolio",
                    "portfolio_result_case_and_af5_internal_case_keep_distinct_serials",
                    "typed_unavailable_is_distinct_from_zero_and_false",
                    "readiness_describes_observability_and_does_not_imply_terminal",
                    "terminal_requires_an_observable_same_case_same_frame",
                    "closed_portfolio_may_drop_domain_and_stage_while_receipts_persist",
                    "query_does_not_require_an_active_event_or_earlier_promotion_receipt",
                    "action_ack_is_never_a_business_postcondition"),
            true, true, false, false);

    private ZhongguoCompensationAf5SnapshotCapabilityProfile() { }
}
