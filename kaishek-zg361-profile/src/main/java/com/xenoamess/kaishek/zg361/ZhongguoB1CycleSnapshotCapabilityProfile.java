package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/** Static compatibility contract for the exact-build B1 cycle snapshot query. */
public final class ZhongguoB1CycleSnapshotCapabilityProfile {
    public static final String ID = "ck3-1.19.0.6-zg361-b1-cycle-snapshot-v1";
    public static final String CAPABILITY_ID =
            "game.command.query-zhongguo-b1-cycle-snapshot-v1";
    public static final String STEP_ID = "query-zhongguo-b1-cycle-snapshot-v1";
    public static final String MCP_TOOL_ID = "ck3_query_zhongguo_b1_cycle_snapshot_v1";
    public static final String CASE_KIND = "zhongguo.b1.cycle";
    public static final String GAME_VERSION = "1.19.0.6";
    public static final String EXECUTABLE_SHA256 =
            "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86";
    public static final String ROOT_ABI_SHA256 =
            "15c9ea3943ec521b84c5ce693e90fea7a81a526deeeca9061eb66ff4d93145b0";
    public static final String ROOT_SOURCE_CONTRACT_SHA256 =
            "4093ab9db2a64c408a479f5dd96cee210253cc29f8aa0755e77e868c4447e1a1";
    public static final String ROOT_SCHEMA_SHA256 =
            "42e5692943123ac71c8629c2a8a6c0656f6aab20ea2a8546a193707ba932c23b";
    public static final String ROOT_PYTHON_CONTRACT_SHA256 =
            "470c596748669bff779fdbf2b18273250730f68ee1fbecebd2d7aa410eeeddff";
    public static final String ROOT_PROVIDER_COMMIT =
            "4c4891eb899e736ad4f5c6eaabdaaedc2f296f05";
    public static final int FIXED_ALLOWLIST_COUNT = 38;

    public static final CapabilityDescriptor SNAPSHOT =
            new CapabilityDescriptor(
                    CAPABILITY_ID,
                    ID,
                    List.of(
                            "schema_version", "status", "case_kind",
                            "request_nonce", "snapshot_revision", "date_raw",
                            "paused", "player_character_id", "manager_character_id",
                            "cycle.cycle_serial", "cycle.case_serial", "cycle.state",
                            "cycle.open_year", "cycle.runtime_schema", "cycle.active",
                            "roster.subject_count", "roster.before_prune_count",
                            "roster.pruned_count", "roster.amendment_count",
                            "roster.audit_version", "roster.reopen_required",
                            "processing.count", "processing.agenda_count",
                            "processing.local_candidate_count",
                            "processing.pre_calibration_valid_count",
                            "quota.rebuild_generation", "quota.built_case_serial",
                            "quota.book_version", "quota.target_top",
                            "quota.target_middle", "quota.target_bottom",
                            "quota.recount_top", "quota.recount_middle",
                            "quota.recount_bottom",
                            "quota.pre_calibration_expected_count",
                            "quota.pre_calibration_mismatch", "quota.pool_membership",
                            "closure.calibration_finalized", "closure.state",
                            "closure.rewards_issued", "closure.publication_blocked",
                            "pending.open_count", "pending.slot_used",
                            "pending.reward_expected_count", "pending.rewards_paid_count",
                            "pending.rewards_committed",
                            "pending.watchdog_cancelled_count",
                            "pending.watchdog_orphan_count",
                            "readiness.manager_binding_ready",
                            "readiness.cycle_identity_ready", "readiness.roster_ready",
                            "readiness.processing_ready", "readiness.quota_ready",
                            "readiness.closure_ready", "readiness.pending_ready",
                            "readiness.same_frame_ready", "readiness.ready",
                            "unavailable_reason", "provenance.game_version",
                            "provenance.executable_sha256", "provenance.backend_id",
                            "provenance.consumer_id", "provenance.allowlist_id",
                            "provenance.variable_context_for_scope_rva",
                            "provenance.variable_identifier_table_rva",
                            "provenance.variable_identifier_lookup_rva",
                            "provenance.variable_identifier_name_rva",
                            "provenance.character_storage_slot_rva"),
                    List.of(
                            "request_is_nonce_and_expected_revision_only",
                            "manager_is_the_paused_played_character",
                            "caller_cannot_select_character_or_variable_names",
                            "fixed_allowlist_contains_exactly_38_variables",
                            "two_complete_allowlist_reads_are_identical",
                            "frame_before_and_after_are_identical",
                            "active_matches_state", "counts_are_nonnegative",
                            "processing_count_does_not_exceed_roster",
                            "quota_targets_sum_to_processing_count",
                            "quota_recounts_sum_to_processing_count",
                            "quota_targets_match_recounts", "closed_state_is_coherent",
                            "typed_unavailable_never_produces_ready"),
                    true, true, false, false);

    private ZhongguoB1CycleSnapshotCapabilityProfile() { }
}
