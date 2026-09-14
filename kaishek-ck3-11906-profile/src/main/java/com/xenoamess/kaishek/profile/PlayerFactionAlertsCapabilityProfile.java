package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static exact-build projection of the public player-faction alert query.
 *
 * <p>The upstream provider owns the native reader, paused-snapshot binding and
 * MCP server. This profile freezes the public v1 request, envelope, payload and
 * strict partial-readiness boundary without granting open_kaishek process or
 * gameplay-action authority.</p>
 */
public final class PlayerFactionAlertsCapabilityProfile {
    public static final String UPSTREAM_PROVIDER_COMMIT =
            "1957b6d0ce133f76d56552b76a3ec96f7c740135";
    public static final String UPSTREAM_PAIRING_STATUS = "paired_to_upstream_main";

    public static final String MCP_TOOL_ID = "ck3_query_player_faction_alerts_v1";
    public static final String CAPABILITY_ID =
            "game.command.query-player-faction-alerts-v1";
    public static final String STEP_ID = "query-player-faction-alerts-v1";
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-player-faction-alerts-v1-candidate";
    public static final String CONTRACT_STAGE =
            "targeting_count_live_rows_and_county_fixture_pending_native_readers";
    public static final String BACKEND_ID =
            "ck3-1.19.0.6-native-player-faction-alerts-v1";
    public static final int SCHEMA_VERSION = 1;
    public static final int FIXED_POINT_SCALE = 100_000;
    public static final boolean TARGETING_COUNT_PRODUCTION_REUSED = true;
    public static final boolean TARGETING_ROWS_NATIVE_READY = false;
    public static final boolean COUNTY_EXPOSURE_NATIVE_READY = false;
    public static final boolean EXACT_ULTIMATUM_TIMING_READY = false;

    public static final List<String> REQUEST_FIELDS = List.of(
            "expected_revision");

    public static final List<String> COMMAND_ENVELOPE_FIELDS = List.of(
            "step",
            "accepted",
            "status",
            "query_sequence",
            "snapshot_revision",
            "player_faction_alerts",
            "backend_id");

    public static final List<String> MCP_RESPONSE_FIELDS = List.of(
            "step",
            "accepted",
            "status",
            "query_sequence",
            "snapshot_revision",
            "player_faction_alerts",
            "backend_id",
            "player_faction_alerts_ready",
            "queried_snapshot_id",
            "queried_revision",
            "queried_native_revision",
            "schema_version",
            "scope",
            "build",
            "source",
            "binding");

    public static final List<String> PAYLOAD_FIELDS = List.of(
            "schema_version",
            "status",
            "snapshot_revision",
            "date_raw",
            "player_character_id",
            "targeting_faction_count",
            "targeting_factions",
            "county_exposures",
            "planner_projection",
            "readiness",
            "component_unavailable_reasons",
            "unavailable_reason",
            "provenance");

    public static final List<String> TARGETING_FACTION_FIELDS = List.of(
            "faction_id",
            "faction_type_key",
            "target_character_id",
            "leader_character_id",
            "leader_is_human",
            "special_character_id",
            "special_title_id",
            "faction_at_war",
            "faction_war_id",
            "power",
            "power_threshold",
            "discontent",
            "discontent_per_month",
            "months_until_max_discontent",
            "character_member_ids",
            "county_member_title_ids",
            "dangerous_by_stock_rule",
            "danger_reason");

    public static final List<String> COUNTY_EXPOSURE_FIELDS = List.of(
            "county_title_id",
            "faction_id",
            "faction_type_key",
            "target_character_id",
            "power",
            "power_threshold",
            "dangerous_by_stock_rule",
            "danger_reason");

    public static final List<String> PLANNER_PROJECTION_FIELDS = List.of(
            "status",
            "present",
            "dangerous",
            "dangerous_faction_ids",
            "watch_faction_ids",
            "war_handoff_faction_ids",
            "exposed_county_title_ids",
            "exact_ultimatum_timing_ready");

    public static final List<String> READINESS_FIELDS = List.of(
            "identity_ready",
            "targeting_count_ready",
            "targeting_rows_ready",
            "county_exposure_ready",
            "stock_dangerous_predicate_ready",
            "same_frame_ready",
            "alert_ready",
            "exact_ultimatum_timing_ready");

    public static final List<String> COMPONENT_UNAVAILABLE_REASON_FIELDS = List.of(
            "targeting_rows",
            "county_exposure");

    public static final List<String> PROVENANCE_FIELDS = List.of(
            "game_version",
            "executable_sha256",
            "backend_id");

    public static final List<String> UNAVAILABLE_REASONS = List.of(
            "unsupported_build",
            "requires_application_main",
            "requires_paused",
            "state_changed",
            "reader_not_implemented");

    public static final List<String> PARTIAL_REASONS = List.of(
            "targeting_rows_native_reader_not_frozen",
            "county_exposure_native_reader_not_frozen");

    public static final CapabilityDescriptor QUERY =
            new CapabilityDescriptor(
                    MCP_TOOL_ID,
                    PROFILE_VERSION,
                    MCP_RESPONSE_FIELDS,
                    List.of(
                            "request_accepts_only_non_negative_expected_revision",
                            "query_requires_one_unchanged_paused_exact_build_snapshot",
                            "native_envelope_binds_query_sequence_and_snapshot_revision",
                            "available_frame_requires_same_frame_identity_and_targeting_count",
                            "targeting_count_reuses_the_production_campaign_root_reader",
                            "unready_targeting_rows_are_empty_and_use_the_frozen_partial_reason",
                            "unready_county_exposures_are_empty_and_use_the_frozen_partial_reason",
                            "alert_ready_requires_rows_counties_stock_predicate_and_same_frame",
                            "player_faction_alerts_ready_mirrors_readiness_alert_ready",
                            "planner_projection_is_unavailable_until_alert_ready",
                            "exact_ultimatum_timing_ready_remains_false_in_v1",
                            "available_rows_use_sorted_unique_stable_identities",
                            "targeting_row_count_matches_targeting_faction_count_when_ready",
                            "fixed_point_values_use_scale_100000",
                            "top_level_unavailable_exposes_no_partial_observations",
                            "query_does_not_authorize_faction_or_war_mutation",
                            "unknown_mcp_arguments_are_rejected",
                            "contract_does_not_depend_on_machine_account_path_or_ck3_round"),
                    true,
                    true,
                    false,
                    false);

    private PlayerFactionAlertsCapabilityProfile() { }
}
