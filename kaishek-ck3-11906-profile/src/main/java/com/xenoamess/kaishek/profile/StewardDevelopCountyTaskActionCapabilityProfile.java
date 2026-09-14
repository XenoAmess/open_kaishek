package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static candidate profile for the typed steward Develop County task action.
 *
 * <p>The upstream provider owns request admission, native dispatch and the
 * independent post-snapshot verifier. This profile freezes the public
 * candidate contract without advertising the production action or treating a
 * submission acknowledgement as an applied result.</p>
 */
public final class StewardDevelopCountyTaskActionCapabilityProfile {
    public static final String UPSTREAM_PROVIDER_COMMIT =
            "bf5a4032440eae5651dc4c5c481c24b4be27cff1";
    public static final String UPSTREAM_PAIRING_STATUS =
            "paired_to_upstream_main";

    public static final String MCP_TOOL_ID =
            "ck3_change_steward_develop_county_task_v1";
    public static final String CAPABILITY_ID =
            "game.command.change-steward-develop-county-task-v1";
    public static final String FAIL_CLOSED_TRANSPORT_CAPABILITY_ID =
            "game.contract.change-steward-develop-county-task-v1-fail-closed";
    public static final String STEP_ID =
            "change-steward-develop-county-task-v1";
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-steward-develop-county-task-action-v1-candidate";
    public static final String FIXED_TASK_KEY = "task_develop_county";
    public static final int SCHEMA_VERSION = 1;

    public static final List<String> REQUEST_FIELDS = List.of(
            "councillor_character_id",
            "target_county_title_id",
            "expected_revision",
            "replace_existing_task");

    public static final List<String> ACK_FIELDS = List.of(
            "schema_version",
            "status",
            "verification_pending",
            "request_id",
            "pre_snapshot_revision",
            "councillor_character_id",
            "task_key",
            "target_county_title_id",
            "replaced_existing_task",
            "failure_class",
            "rejection_reason",
            "native_reason_key",
            "exact_build");

    public static final List<String> ACK_STATUSES = List.of(
            "submitted_verification_pending",
            "rejected_before_submit");

    public static final List<String> FAILURE_CLASSES = List.of(
            "request_contract",
            "snapshot_binding",
            "councillor_binding",
            "task_or_target_legality",
            "native_command_dispatch");

    public static final List<String> RECEIPT_FIELDS = List.of(
            "schema_version",
            "request_id",
            "status",
            "reason",
            "post_snapshot_revision",
            "post_native_snapshot_revision",
            "post_observed_date_raw",
            "councillor_character_id",
            "active_task_key",
            "target_county_title_id",
            "target_province_id",
            "progress_kind",
            "progress_current_raw",
            "progress_max_raw",
            "progress_frozen",
            "postcondition_verified");

    public static final List<String> RECEIPT_STATUSES = List.of(
            "applied",
            "rejected",
            "postcondition_failed");

    public static final CapabilityDescriptor CHANGE_TASK =
            new CapabilityDescriptor(
                    MCP_TOOL_ID,
                    PROFILE_VERSION,
                    ACK_FIELDS,
                    List.of(
                            "request_task_key_is_fixed_to_task_develop_county",
                            "request_is_bound_to_councillor_target_and_expected_revision",
                            "replace_existing_task_requires_explicit_authorization",
                            "ack_status_is_submitted_verification_pending_or_rejected_before_submit",
                            "ack_field_set_excludes_applied_and_success",
                            "submitted_ack_sets_verification_pending_true",
                            "rejected_before_submit_ack_sets_verification_pending_false",
                            "ack_never_proves_native_command_application",
                            "receipt_is_derived_from_an_independent_new_paused_snapshot",
                            "receipt_applied_requires_postcondition_verified_true",
                            "receipt_status_is_applied_rejected_or_postcondition_failed",
                            "failure_class_uses_the_closed_five_value_vocabulary",
                            "fail_closed_transport_can_return_only_rejected_before_submit",
                            "submitted_ack_from_fail_closed_transport_is_a_red_contract_violation",
                            "production_capability_is_not_advertised",
                            "fixture_does_not_certify_native_abi_or_runtime",
                            "unknown_mcp_arguments_are_rejected",
                            "contract_does_not_depend_on_machine_account_path_or_ck3_round"),
                    false,
                    false,
                    false,
                    false);

    private StewardDevelopCountyTaskActionCapabilityProfile() { }
}
