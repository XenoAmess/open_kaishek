package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static compatibility projection of the Council composition MCP query.
 *
 * <p>The MCP tool and formal planner consumer exist, but the native bridge does
 * not advertise the underlying capability until its live gate is satisfied.
 * The unsupported backend path therefore remains part of this contract. This
 * profile grants neither CK3 process authority nor councillor mutation.</p>
 */
public final class CouncilCompositionCandidatesMcpCapabilityProfile {
    public static final String UPSTREAM_MCP_CONSUMER_COMMIT =
            "a6857514dd909caeda68a07ec2baee053c53daac";
    public static final String UPSTREAM_PAIRING_STATUS =
            "paired_to_upstream_candidate_pending_native_advertisement_and_live";

    public static final String MCP_TOOL_ID =
            "ck3_query_council_composition_candidates_v1";
    public static final String CAPABILITY_ID =
            CouncilCompositionCandidatesResultSchemaProfile.CAPABILITY_ID;
    public static final String STEP_ID =
            "query-council-composition-candidates-v1";
    public static final String RESULT_SCHEMA_ID =
            CouncilCompositionCandidatesResultSchemaProfile.RESULT_SCHEMA_ID;
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-council-composition-candidates-v1-mcp-candidate";
    public static final String CONTRACT_STAGE =
            "formal_mcp_query_static_ready_native_advertisement_and_live_pending";
    public static final int SCHEMA_VERSION = 1;

    public static final boolean MCP_REGISTERED = true;
    public static final boolean FORMAL_PLANNER_CONSUMER_IMPLEMENTED = true;
    public static final boolean NATIVE_CAPABILITY_ADVERTISED = false;
    public static final boolean RUNTIME_CERTIFIED = false;
    public static final boolean NATIVE_LIVE_READY = false;
    public static final boolean ASSIGNMENT_ACTION_AVAILABLE = false;
    public static final boolean FORMAL_STRATEGY_LIVE = false;

    public static final List<String> REQUEST_FIELDS = List.of(
            "expected_snapshot_id",
            "public_revision",
            "native_revision",
            "date_raw",
            "owner_character_id",
            "position_key");

    public static final List<String> NATIVE_ENVELOPE_FIELDS = List.of(
            "step",
            "accepted",
            "status",
            "query_sequence",
            "snapshot_revision",
            "council_composition_candidates",
            "backend_id");

    public static final List<String> MCP_RESPONSE_FIELDS = List.of(
            "step",
            "accepted",
            "status",
            "query_sequence",
            "snapshot_revision",
            "council_composition_candidates",
            "backend_id",
            "council_composition_candidates_ready",
            "queried_snapshot_id",
            "queried_revision",
            "queried_native_revision",
            "schema_version",
            "schema",
            "scope",
            "request");

    public static final CapabilityDescriptor QUERY =
            new CapabilityDescriptor(
                    MCP_TOOL_ID,
                    PROFILE_VERSION,
                    MCP_RESPONSE_FIELDS,
                    List.of(
                            "request_contains_exactly_the_six_frozen_binding_fields",
                            "position_key_is_fixed_to_councillor_steward",
                            "request_revisions_are_positive_uint64_values",
                            "request_date_and_character_identity_use_signed_int32_ranges",
                            "query_requires_one_unchanged_paused_exact_build_snapshot",
                            "native_result_and_python_normalization_preserve_the_v1_schema",
                            "available_response_requires_complete_readiness_and_exact_binding",
                            "query_sequence_is_a_positive_uint64_value",
                            "bottom_native_capability_absence_remains_unsupported",
                            "hybrid_driver_does_not_fallback_for_this_pure_native_query",
                            "formal_planner_queries_before_consuming_a_council_observation",
                            "formal_planner_consumes_only_a_same_frame_available_result",
                            "assignment_remains_blocked_without_a_semantic_action",
                            "query_does_not_authorize_councillor_mutation",
                            "unknown_mcp_arguments_are_rejected",
                            "contract_does_not_depend_on_machine_account_path_or_ck3_round"),
                    true,
                    true,
                    false,
                    false);

    private CouncilCompositionCandidatesMcpCapabilityProfile() { }
}
