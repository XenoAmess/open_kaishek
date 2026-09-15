package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static compatibility projection of the CK3 1.19.0.6 steward assignment
 * action contract.
 *
 * <p>The paired upstream package freezes the typed request, immediate ACK and
 * later paused-frame receipt. It has not registered a public bridge or MCP
 * command and has no live submission evidence. This profile therefore grants
 * no CK3 process authority and advertises no executable action.</p>
 */
public final class CouncilAssignCouncillorActionCapabilityProfile {
    public static final String UPSTREAM_PROVIDER_COMMIT =
            "c9ae3ceb3fe1cb8f37fd412e071e2cdd0c97f0f5";
    public static final String UPSTREAM_PAIRING_STATUS =
            "paired_to_upstream_static_action_pending_runtime_and_live";

    public static final String CAPABILITY_ID =
            "game.action.assign-councillor-v1";
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-assign-councillor-action-v1-candidate";
    public static final String CONTRACT_STAGE =
            "static_action_contract_runtime_and_live_pending";
    public static final String FIXED_POSITION_KEY = "councillor_steward";
    public static final int SCHEMA_VERSION = 1;

    public static final boolean NATIVE_CAPABILITY_ADVERTISED = false;
    public static final boolean MCP_REGISTERED = false;
    public static final boolean RUNTIME_CERTIFIED = false;
    public static final boolean NATIVE_LIVE_READY = false;
    public static final boolean FORMAL_STRATEGY_LIVE = false;
    public static final boolean FALLBACK_AVAILABLE = false;

    public static final List<String> REQUEST_FIELDS = List.of(
            "request_id",
            "position_key",
            "expected_snapshot_id",
            "expected_public_revision",
            "expected_native_revision",
            "expected_date_raw",
            "expected_owner_character_id",
            "candidate_character_id",
            "expected_has_incumbent",
            "expected_incumbent_character_id");

    public static final List<String> ACK_FIELDS = List.of(
            "status",
            "failure",
            "request_id",
            "pre_snapshot_id",
            "pre_public_revision",
            "pre_native_revision",
            "pre_date_raw",
            "owner_character_id",
            "position_key",
            "active_task_id",
            "candidate_character_id",
            "had_incumbent",
            "previous_incumbent_character_id",
            "route",
            "native_helper_invoked",
            "queue_acceptance_observed",
            "verification_pending",
            "native_reason_key");

    public static final List<String> ACK_STATUSES = List.of(
            "rejected_before_submit",
            "native_helper_invoked_verification_pending");

    public static final List<String> ROUTES = List.of(
            "none",
            "assign_vacant",
            "replace_incumbent");

    public static final List<String> FAILURE_REASONS = List.of(
            "none",
            "request_contract_invalid",
            "exact_build_mismatch",
            "private_candidate_not_admitted",
            "application_main_thread_required",
            "callbacks_unavailable",
            "observation_unavailable",
            "not_paused",
            "snapshot_binding_mismatch",
            "position_outside_coverage",
            "active_task_identity_unavailable",
            "incumbent_identity_unavailable",
            "candidate_equals_incumbent",
            "final_legality_unavailable",
            "candidate_not_in_exact_collection",
            "candidate_identity_mismatch",
            "candidate_already_councillor",
            "candidate_is_guest",
            "pending_character_interaction",
            "incumbent_cannot_be_replaced",
            "state_changed_before_submit",
            "native_helper_not_invoked");

    public static final List<String> RECEIPT_FIELDS = List.of(
            "status",
            "rejected_action_failure",
            "request_id",
            "post_snapshot_id",
            "post_public_revision",
            "post_native_revision",
            "post_date_raw",
            "owner_character_id",
            "position_key",
            "incumbent_character_id",
            "incumbent_identity_round_trip",
            "postcondition_verified",
            "reason");

    public static final List<String> RECEIPT_STATUSES = List.of(
            "rejected",
            "postcondition_failed",
            "applied");

    public static final List<String> RECEIPT_FAILURE_REASONS = List.of(
            "action_rejected",
            "invalid_ack",
            "post_observation_unavailable",
            "no_new_paused_frame",
            "owner_or_position_changed",
            "active_task_changed",
            "candidate_not_observed_as_incumbent");

    public static final CapabilityDescriptor ASSIGN_COUNCILLOR =
            new CapabilityDescriptor(
                    CAPABILITY_ID,
                    PROFILE_VERSION,
                    ACK_FIELDS,
                    List.of(
                            "request_contains_exactly_the_ten_frozen_fields",
                            "position_key_is_fixed_to_councillor_steward",
                            "request_binds_snapshot_revisions_date_owner_candidate_and_incumbent",
                            "vacant_and_replacement_routes_are_distinct",
                            "final_legality_is_rechecked_immediately_before_submit",
                            "replacement_requires_native_incumbent_fireability",
                            "ack_is_only_rejected_or_native_helper_invoked_verification_pending",
                            "native_helper_invocation_is_not_queue_acceptance",
                            "ack_never_proves_assignment_applied",
                            "applied_requires_a_distinct_newer_paused_frame",
                            "applied_requires_candidate_observed_as_the_steward_incumbent",
                            "public_bridge_and_mcp_registration_are_absent",
                            "production_capability_is_not_advertised",
                            "runtime_native_live_and_formal_strategy_readiness_are_false",
                            "no_fallback_is_available",
                            "contract_does_not_depend_on_machine_account_path_or_ck3_round"),
                    false,
                    false,
                    false,
                    false);

    private CouncilAssignCouncillorActionCapabilityProfile() { }
}
