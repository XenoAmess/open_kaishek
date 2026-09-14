package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static exact-build projection of the public Develop County candidate query.
 *
 * <p>The upstream provider owns the native reader, paused-snapshot binding and
 * MCP server. This profile freezes the candidate v1 public request and response
 * contract while the production reader still returns typed unavailable. It
 * does not grant open_kaishek gameplay-process authority or certify the pending
 * exact-build reader.</p>
 */
public final class StewardDevelopCountyCandidatesCapabilityProfile {
    public static final String UPSTREAM_PROVIDER_COMMIT =
            "f0f10bf1863519881d9bd88c64a8ace4ec6abc41";
    public static final String UPSTREAM_PAIRING_STATUS =
            "paired_to_upstream_main";

    public static final String MCP_TOOL_ID =
            "ck3_query_steward_develop_county_candidates_v1";
    public static final String CAPABILITY_ID =
            "game.command.query-steward-develop-county-candidates-v1";
    public static final String STEP_ID =
            "query-steward-develop-county-candidates-v1";
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-steward-develop-county-candidates-v1-candidate";
    public static final String CONTRACT_STAGE =
            "exact_build_contract_fixture_pending_live_reader";
    public static final String BACKEND_ID =
            "ck3-1.19.0.6-native-steward-develop-county-candidates-v1";
    public static final String COMMAND_BACKEND_ID = "native-headless";
    public static final int SCHEMA_VERSION = 1;

    public static final List<String> REQUEST_FIELDS = List.of(
            "expected_revision");

    public static final List<String> COMMAND_ENVELOPE_FIELDS = List.of(
            "step",
            "accepted",
            "status",
            "query_sequence",
            "snapshot_revision",
            "steward_develop_county_candidates",
            "backend_id");

    public static final List<String> PAYLOAD_FIELDS = List.of(
            "schema_version",
            "contract_stage",
            "status",
            "unavailable_reason",
            "snapshot_revision",
            "observed_date_raw",
            "player_character_id",
            "steward_character_id",
            "task_key",
            "shown",
            "valid",
            "task_failure_reason",
            "steward_increase_development_value_raw",
            "current_gold_raw",
            "no_ai_increase_development",
            "has_active_improve_development_directive",
            "target_selection_mode",
            "candidates",
            "same_frame_stable",
            "readiness",
            "provenance");

    public static final List<String> CANDIDATE_FIELDS = List.of(
            "county_title_id",
            "capital_province_id",
            "holder_character_id",
            "is_player_capital",
            "directly_held_by_player",
            "native_legal",
            "development_level_raw",
            "development_progress_raw",
            "monthly_development_rate_raw",
            "max_development_level_raw",
            "terrain_key",
            "same_culture_as_player",
            "cultural_acceptance_threshold_passed");

    public static final List<String> PROVENANCE_FIELDS = List.of(
            "game_version",
            "executable_sha256",
            "backend_id",
            "reader_mode",
            "next_reverse_engineering_entry");

    public static final List<String> MCP_RESPONSE_FIELDS = List.of(
            "step",
            "accepted",
            "status",
            "query_sequence",
            "snapshot_revision",
            "steward_develop_county_candidates",
            "backend_id",
            "steward_develop_county_candidates_ready",
            "queried_snapshot_id",
            "queried_revision",
            "queried_native_revision",
            "schema_version",
            "scope",
            "build",
            "source",
            "binding");

    public static final List<String> UNAVAILABLE_REASONS = List.of(
            "reader_not_implemented",
            "unsupported_build",
            "requires_application_main",
            "requires_paused",
            "state_changed");

    public static final CapabilityDescriptor QUERY =
            new CapabilityDescriptor(
                    MCP_TOOL_ID,
                    PROFILE_VERSION,
                    MCP_RESPONSE_FIELDS,
                    List.of(
                            "request_accepts_only_non_negative_expected_revision",
                            "query_requires_paused_snapshot_and_exact_expected_revision",
                            "payload_is_bound_to_exact_build_and_native_snapshot_revision",
                            "available_payload_requires_complete_fields_and_two_sample_equality",
                            "production_reader_is_typed_unavailable_until_exact_build_abi_is_closed",
                            "unavailable_payload_exposes_no_partial_observations",
                            "unavailable_observed_date_is_null_or_same_frame_date",
                            "candidate_rows_include_only_native_legal_counties",
                            "target_selection_mode_preserves_engine_random_unscored_behavior",
                            "query_does_not_authorize_council_task_or_target_mutation",
                            "unknown_mcp_arguments_are_rejected",
                            "contract_does_not_depend_on_machine_account_path_or_ck3_round"),
                    true,
                    true,
                    false,
                    false);

    private StewardDevelopCountyCandidatesCapabilityProfile() { }
}
