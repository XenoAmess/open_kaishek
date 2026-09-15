package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CouncilCompositionCandidatesMcpCapabilityProfileTest {
    @Test
    void mcpIdentityAndRequestArePinnedExactly() {
        assertEquals(
                "ck3_query_council_composition_candidates_v1",
                CouncilCompositionCandidatesMcpCapabilityProfile.QUERY.id());
        assertEquals(
                "game.query.council-composition-candidates-v1",
                CouncilCompositionCandidatesMcpCapabilityProfile.CAPABILITY_ID);
        assertEquals(
                "query-council-composition-candidates-v1",
                CouncilCompositionCandidatesMcpCapabilityProfile.STEP_ID);
        assertEquals(
                "xar.ck3.council-composition-candidates/v1",
                CouncilCompositionCandidatesMcpCapabilityProfile.RESULT_SCHEMA_ID);
        assertEquals(
                List.of(
                        "expected_snapshot_id", "public_revision", "native_revision",
                        "date_raw", "owner_character_id", "position_key"),
                CouncilCompositionCandidatesMcpCapabilityProfile.REQUEST_FIELDS);
    }

    @Test
    void nativeAndMcpResponseShapesArePinnedExactly() {
        assertEquals(
                List.of(
                        "step", "accepted", "status", "query_sequence",
                        "snapshot_revision", "council_composition_candidates",
                        "backend_id"),
                CouncilCompositionCandidatesMcpCapabilityProfile.NATIVE_ENVELOPE_FIELDS);
        assertEquals(
                List.of(
                        "step", "accepted", "status", "query_sequence",
                        "snapshot_revision", "council_composition_candidates",
                        "backend_id", "council_composition_candidates_ready",
                        "queried_snapshot_id", "queried_revision",
                        "queried_native_revision", "schema_version", "schema",
                        "scope", "request"),
                CouncilCompositionCandidatesMcpCapabilityProfile.MCP_RESPONSE_FIELDS);
        assertEquals(
                CouncilCompositionCandidatesMcpCapabilityProfile.MCP_RESPONSE_FIELDS,
                CouncilCompositionCandidatesMcpCapabilityProfile.QUERY.requiredFields());
    }

    @Test
    void registrationAndExecutionReadinessRemainSeparate() {
        assertTrue(CouncilCompositionCandidatesMcpCapabilityProfile.MCP_REGISTERED);
        assertTrue(
                CouncilCompositionCandidatesMcpCapabilityProfile
                        .FORMAL_PLANNER_CONSUMER_IMPLEMENTED);
        assertFalse(
                CouncilCompositionCandidatesMcpCapabilityProfile
                        .NATIVE_CAPABILITY_ADVERTISED);
        assertFalse(CouncilCompositionCandidatesMcpCapabilityProfile.RUNTIME_CERTIFIED);
        assertFalse(CouncilCompositionCandidatesMcpCapabilityProfile.NATIVE_LIVE_READY);
        assertFalse(
                CouncilCompositionCandidatesMcpCapabilityProfile
                        .ASSIGNMENT_ACTION_AVAILABLE);
        assertFalse(CouncilCompositionCandidatesMcpCapabilityProfile.FORMAL_STRATEGY_LIVE);

        var query = CouncilCompositionCandidatesMcpCapabilityProfile.QUERY;
        assertTrue(query.readOnly());
        assertTrue(query.deterministic());
        assertFalse(query.nativeCertified());
        assertFalse(query.runtimeCertified());
        assertFalse(query.certified());
        assertTrue(query.invariants().contains(
                "bottom_native_capability_absence_remains_unsupported"));
        assertTrue(query.invariants().contains(
                "assignment_remains_blocked_without_a_semantic_action"));
    }

    @Test
    void exactBuildAndUpstreamCandidateArePinned() {
        assertEquals(1, CouncilCompositionCandidatesMcpCapabilityProfile.SCHEMA_VERSION);
        assertEquals("1.19.0.6", Ck3Profile11906.GAME_VERSION);
        assertEquals(
                "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86",
                Ck3Profile11906.EXE_SHA256);
        assertEquals(
                "a6857514dd909caeda68a07ec2baee053c53daac",
                CouncilCompositionCandidatesMcpCapabilityProfile
                        .UPSTREAM_MCP_CONSUMER_COMMIT);
        assertEquals(
                "paired_to_upstream_candidate_pending_native_advertisement_and_live",
                CouncilCompositionCandidatesMcpCapabilityProfile.UPSTREAM_PAIRING_STATUS);
    }
}
