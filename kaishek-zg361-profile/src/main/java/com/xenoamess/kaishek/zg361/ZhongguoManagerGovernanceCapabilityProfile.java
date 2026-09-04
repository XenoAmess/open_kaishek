package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/**
 * Static contract for the Phase-2 B3 manager-governance snapshot query.
 *
 * <p>The companion project owns the native mailbox, JSON schema, driver,
 * service, and MCP transport. This profile records only the public read-only
 * capability and the minimum fields/invariants needed by a downstream
 * compatibility audit. It does not register a Paradox opcode, execute the
 * query, or certify native/runtime behavior.</p>
 */
public final class ZhongguoManagerGovernanceCapabilityProfile {
    public static final String ID =
            "ck3-1.19.0.6-zg361-manager-governance-v1";
    public static final String CAPABILITY_ID =
            "game.command.query-zhongguo-manager-governance-snapshot-v1";
    public static final String STEP_ID =
            "query-zhongguo-manager-governance-snapshot-v1";
    public static final String CASE_KIND = "zhongguo.manager-governance";
    public static final String BOUNDED_AI_MANAGER_DEPENDENCY =
            "zg361-bounded-ai-direct-manager-selection-v1";
    public static final String ROOT_SCHEMA_SHA256 =
            "1487d9dc129baa31a9f2990466fbbb6e935caafdcf1fb2f16358a6b85c68f072";
    public static final String ROOT_CONTRACT_SHA256 =
            "c7f07a53c4752cc11e859e47176124e092a791b8d87880ee3882ac88f31e767a";
    public static final String ROOT_ABI_SHA256 =
            "697994d3c3d798aaeebabd63e891cf7007e67f5cef0c4b0f14405ac7ec646328";
    public static final String ROOT_TRANSPORT_COMMIT =
            "fc8be4aa4a06c5234747b01fcc188f2f7239961e";
    public static final String ROOT_EFFECT_SPLIT_COMMIT =
            "4890b17998df1c5586beb36011d283c1a111f388";

    public static final CapabilityDescriptor SNAPSHOT =
            new CapabilityDescriptor(
                    CAPABILITY_ID,
                    ID,
                    List.of(
                            "schema_version",
                            "status",
                            "case_kind",
                            "request_nonce",
                            "snapshot_revision",
                            "date_raw",
                            "paused",
                            "player_character_id",
                            "subject_character_id",
                            "requested_owner_character_id",
                            "subject_binding.kind",
                            "subject_binding.owner_character_id",
                            "subject_binding.bounded_ai_manager_dependency",
                            "f_case.owner_character_id",
                            "f_case.subject_character_id",
                            "f_case.cycle_serial",
                            "f_case.case_serial",
                            "f_case.state",
                            "f_case.active",
                            "f_case.revision",
                            "team_snapshot.revision",
                            "team_snapshot.cohort_n",
                            "team_snapshot.aggregates",
                            "f035.receipt",
                            "f035.snapshot.mode",
                            "f035.snapshot.rule_source",
                            "f035.snapshot.top_slots",
                            "f035.snapshot.middle_slots",
                            "f035.snapshot.bottom_slots",
                            "f035.snapshot.conserved_slots",
                            "f035.next_cycle_policy",
                            "f035.effective.actual_cohort_n",
                            "f035.effective.actual_bottom_slots",
                            "f032.receipt",
                            "f032.manager_score.sum",
                            "f032.manager_score.mode",
                            "f032.component8",
                            "readiness.ready",
                            "unavailable_reason",
                            "build.version",
                            "build.exe_sha256",
                            "source.backend_id",
                            "source.connection_generation",
                            "source.native_revision",
                            "binding.expected_revision"),
                    List.of(
                            "query_is_read_only_on_one_paused_application_main_frame",
                            "ai_subject_requires_typed_bounded_direct_manager_dependency",
                            "caller_cannot_assert_ai_manager_eligibility",
                            "two_complete_allowlist_reads_are_identical",
                            "owner_subject_cycle_case_identity_is_consistent",
                            "f035_distribution_conserves_current_cohort",
                            "f035_effective_bottom_slots_match_mode_and_current_cohort",
                            "f032_manager_score_and_component8_share_current_receipt",
                            "route_c_is_typed_not_applicable_and_wipes_stale_values",
                            "arbitrary_character_variable_reads_are_forbidden"),
                    true,
                    true,
                    false,
                    false);

    private ZhongguoManagerGovernanceCapabilityProfile() { }
}
