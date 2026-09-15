package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static exact-build projection of the Council composition candidate result.
 *
 * <p>This class freezes the native result schema separately from the MCP
 * request/response capability descriptor. The companion project owns the
 * private reader and the still-pending public runtime advertisement, native
 * live proof and formal strategy consumption.</p>
 */
public final class CouncilCompositionCandidatesResultSchemaProfile {
    public static final String UPSTREAM_CANDIDATE_COMMIT =
            "e0d51a2dd8fc0637dd9a07ae599333ac252b949b";
    public static final String UPSTREAM_SCHEMA_CORRECTION_COMMIT =
            "91b2366344f96206fbac83b529617993b398a16c";
    public static final String UPSTREAM_PAIRING_STATUS =
            "paired_to_upstream_correction_pending_public_runtime_and_live";

    public static final String CAPABILITY_ID =
            "game.query.council-composition-candidates-v1";
    public static final String RESULT_SCHEMA_ID =
            "xar.ck3.council-composition-candidates/v1";
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-council-composition-candidates-v1-candidate";
    public static final String CONTRACT_STAGE =
            "static_result_schema_private_reader_live_public_runtime_and_live_pending";
    public static final int SCHEMA_VERSION = 1;

    public static final String POSITION_KEY = "councillor_steward";
    public static final String MAIN_SKILL_KEY = "stewardship";

    public static final boolean MCP_REGISTERED = false;
    public static final boolean RUNTIME_ADVERTISED = false;
    public static final boolean NATIVE_LIVE_READY = false;
    public static final boolean FORMAL_STRATEGY_LIVE = false;

    public static final List<String> COMMON_FIELDS = List.of(
            "schema",
            "schema_version",
            "capability",
            "exact_build",
            "status",
            "unavailable_reason",
            "source_unavailable_reason");

    public static final List<String> AVAILABLE_FIELDS = List.of(
            "snapshot",
            "owner_character_id",
            "position",
            "candidate_collection_complete",
            "candidates",
            "readiness");

    public static final List<String> EXACT_BUILD_FIELDS = List.of(
            "game_version",
            "executable_sha256");

    public static final List<String> SNAPSHOT_FIELDS = List.of(
            "snapshot_id",
            "public_revision",
            "native_revision",
            "date_raw",
            "paused");

    public static final List<String> POSITION_FIELDS = List.of(
            "position_key",
            "incumbent_character_id",
            "incumbent_main_skill",
            "vacant",
            "action_route");

    public static final List<String> CANDIDATE_FIELDS = List.of(
            "character_id",
            "native_collection_ordinal",
            "eligible",
            "eligibility_reason",
            "main_skill",
            "action_route");

    public static final List<String> MAIN_SKILL_FIELDS = List.of(
            "key",
            "value");

    public static final List<String> READINESS_FIELDS = List.of(
            "identity_ready",
            "candidate_collection_ready",
            "incumbent_ready",
            "incumbent_main_skill_ready",
            "candidate_legality_ready",
            "main_skill_ready",
            "action_route_ready",
            "same_frame_ready",
            "ready");

    public static final List<String> ACTION_ROUTES = List.of(
            "assign",
            "replace");

    public static final List<String> ELIGIBILITY_REASONS = List.of(
            "native_candidate_provider_accepted");

    public static final List<String> UNAVAILABLE_REASONS = List.of(
            "private_reader_unavailable",
            "enrichment_unavailable",
            "same_frame_binding_mismatch",
            "incumbent_invalid",
            "incumbent_main_skill_unready",
            "candidate_set_mismatch",
            "candidate_eligibility_unready",
            "candidate_main_skill_unready",
            "schema_invariant_failed");

    public static final List<String> SOURCE_UNAVAILABLE_REASONS = List.of(
            "invalid_request",
            "exact_build_not_admitted",
            "native_bindings_unavailable",
            "application_main_thread_required",
            "frame_capture_failed",
            "snapshot_identity_mismatch",
            "revision_drift",
            "date_drift",
            "not_paused",
            "player_unavailable",
            "active_steward_task_unavailable",
            "position_outside_coverage",
            "candidate_collection_unavailable",
            "candidate_span_invalid",
            "candidate_row_unreadable",
            "candidate_generation_mismatch",
            "duplicate_candidate_id",
            "temporary_vector_release_failed");

    private CouncilCompositionCandidatesResultSchemaProfile() { }
}
