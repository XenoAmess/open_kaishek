package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CouncilCompositionCandidatesResultSchemaProfileTest {
    @Test
    void publicResultIdentityAndExactBuildArePinned() {
        assertEquals(
                "game.query.council-composition-candidates-v1",
                CouncilCompositionCandidatesResultSchemaProfile.CAPABILITY_ID);
        assertEquals(
                "xar.ck3.council-composition-candidates/v1",
                CouncilCompositionCandidatesResultSchemaProfile.RESULT_SCHEMA_ID);
        assertEquals(1, CouncilCompositionCandidatesResultSchemaProfile.SCHEMA_VERSION);
        assertEquals("1.19.0.6", Ck3Profile11906.GAME_VERSION);
        assertEquals(
                "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86",
                Ck3Profile11906.EXE_SHA256);
    }

    @Test
    void availableShapeIsPinnedExactly() {
        assertEquals(
                List.of(
                        "schema", "schema_version", "capability", "exact_build",
                        "status", "unavailable_reason", "source_unavailable_reason"),
                CouncilCompositionCandidatesResultSchemaProfile.COMMON_FIELDS);
        assertEquals(
                List.of(
                        "snapshot", "owner_character_id", "position",
                        "candidate_collection_complete", "candidates", "readiness"),
                CouncilCompositionCandidatesResultSchemaProfile.AVAILABLE_FIELDS);
        assertEquals(5,
                CouncilCompositionCandidatesResultSchemaProfile.SNAPSHOT_FIELDS.size());
        assertEquals(
                List.of(
                        "position_key", "incumbent_character_id", "vacant",
                        "action_route"),
                CouncilCompositionCandidatesResultSchemaProfile.POSITION_FIELDS);
        assertEquals(
                List.of(
                        "character_id", "native_collection_ordinal", "eligible",
                        "eligibility_reason", "main_skill", "action_route"),
                CouncilCompositionCandidatesResultSchemaProfile.CANDIDATE_FIELDS);
        assertEquals(
                List.of("key", "value"),
                CouncilCompositionCandidatesResultSchemaProfile.MAIN_SKILL_FIELDS);
        assertEquals(8,
                CouncilCompositionCandidatesResultSchemaProfile.READINESS_FIELDS.size());
    }

    @Test
    void stewardSemanticsAndClosedVocabulariesArePinned() {
        assertEquals(
                "councillor_steward",
                CouncilCompositionCandidatesResultSchemaProfile.POSITION_KEY);
        assertEquals(
                "stewardship",
                CouncilCompositionCandidatesResultSchemaProfile.MAIN_SKILL_KEY);
        assertEquals(
                List.of("assign", "replace"),
                CouncilCompositionCandidatesResultSchemaProfile.ACTION_ROUTES);
        assertEquals(
                List.of("native_candidate_provider_accepted"),
                CouncilCompositionCandidatesResultSchemaProfile.ELIGIBILITY_REASONS);
        assertEquals(8,
                CouncilCompositionCandidatesResultSchemaProfile.UNAVAILABLE_REASONS.size());
        assertEquals(18,
                CouncilCompositionCandidatesResultSchemaProfile.SOURCE_UNAVAILABLE_REASONS.size());
    }

    @Test
    void candidateProfileDoesNotAdvertiseTransportOrLiveReadiness() {
        assertFalse(CouncilCompositionCandidatesResultSchemaProfile.MCP_REGISTERED);
        assertFalse(CouncilCompositionCandidatesResultSchemaProfile.RUNTIME_ADVERTISED);
        assertFalse(CouncilCompositionCandidatesResultSchemaProfile.NATIVE_LIVE_READY);
        assertFalse(CouncilCompositionCandidatesResultSchemaProfile.FORMAL_STRATEGY_LIVE);
        assertEquals(
                "static_result_schema_pending_private_live_and_runtime_registration",
                CouncilCompositionCandidatesResultSchemaProfile.CONTRACT_STAGE);
        assertEquals(
                "paired_to_upstream_candidate_pending_r693_and_transport",
                CouncilCompositionCandidatesResultSchemaProfile.UPSTREAM_PAIRING_STATUS);
        assertTrue(
                CouncilCompositionCandidatesResultSchemaProfile.UPSTREAM_CANDIDATE_COMMIT
                        .matches("[0-9a-f]{40}"));
    }
}
