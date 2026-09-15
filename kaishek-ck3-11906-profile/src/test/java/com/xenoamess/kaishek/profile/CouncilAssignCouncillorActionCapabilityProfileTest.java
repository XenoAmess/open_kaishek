package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CouncilAssignCouncillorActionCapabilityProfileTest {
    @Test
    void publicIdentityAndStrictRequestArePinnedExactly() {
        assertEquals(
                "game.action.assign-councillor-v1",
                CouncilAssignCouncillorActionCapabilityProfile.CAPABILITY_ID);
        assertEquals(
                CouncilAssignCouncillorActionCapabilityProfile.CAPABILITY_ID,
                CouncilAssignCouncillorActionCapabilityProfile.ASSIGN_COUNCILLOR.id());
        assertEquals(
                "councillor_steward",
                CouncilAssignCouncillorActionCapabilityProfile.FIXED_POSITION_KEY);
        assertEquals(
                List.of(
                        "request_id", "position_key", "expected_snapshot_id",
                        "expected_public_revision", "expected_native_revision",
                        "expected_date_raw", "expected_owner_character_id",
                        "candidate_character_id", "expected_has_incumbent",
                        "expected_incumbent_character_id"),
                CouncilAssignCouncillorActionCapabilityProfile.REQUEST_FIELDS);
    }

    @Test
    void acknowledgementShapeAndVocabulariesMatchTheNativeContract() {
        assertEquals(
                List.of(
                        "status", "failure", "request_id", "pre_snapshot_id",
                        "pre_public_revision", "pre_native_revision", "pre_date_raw",
                        "owner_character_id", "position_key", "active_task_id",
                        "candidate_character_id", "had_incumbent",
                        "previous_incumbent_character_id", "route",
                        "native_helper_invoked", "queue_acceptance_observed",
                        "verification_pending", "native_reason_key"),
                CouncilAssignCouncillorActionCapabilityProfile.ACK_FIELDS);
        assertEquals(
                CouncilAssignCouncillorActionCapabilityProfile.ACK_FIELDS,
                CouncilAssignCouncillorActionCapabilityProfile.ASSIGN_COUNCILLOR
                        .requiredFields());
        assertEquals(
                List.of(
                        "rejected_before_submit",
                        "native_helper_invoked_verification_pending"),
                CouncilAssignCouncillorActionCapabilityProfile.ACK_STATUSES);
        assertEquals(
                List.of("none", "assign_vacant", "replace_incumbent"),
                CouncilAssignCouncillorActionCapabilityProfile.ROUTES);
        assertEquals(22,
                CouncilAssignCouncillorActionCapabilityProfile.FAILURE_REASONS.size());
        assertTrue(CouncilAssignCouncillorActionCapabilityProfile.FAILURE_REASONS
                .contains("candidate_not_in_exact_collection"));
        assertTrue(CouncilAssignCouncillorActionCapabilityProfile.FAILURE_REASONS
                .contains("incumbent_cannot_be_replaced"));
        assertTrue(CouncilAssignCouncillorActionCapabilityProfile.FAILURE_REASONS
                .contains("native_helper_not_invoked"));
    }

    @Test
    void receiptIsSeparateAndRequiresANewerPausedObservation() {
        assertEquals(
                List.of(
                        "status", "rejected_action_failure", "request_id",
                        "post_snapshot_id", "post_public_revision",
                        "post_native_revision", "post_date_raw", "owner_character_id",
                        "position_key", "incumbent_character_id",
                        "incumbent_identity_round_trip", "postcondition_verified",
                        "reason"),
                CouncilAssignCouncillorActionCapabilityProfile.RECEIPT_FIELDS);
        assertEquals(
                List.of("rejected", "postcondition_failed", "applied"),
                CouncilAssignCouncillorActionCapabilityProfile.RECEIPT_STATUSES);
        assertEquals(
                List.of(
                        "action_rejected", "invalid_ack",
                        "post_observation_unavailable", "no_new_paused_frame",
                        "owner_or_position_changed", "active_task_changed",
                        "candidate_not_observed_as_incumbent"),
                CouncilAssignCouncillorActionCapabilityProfile.RECEIPT_FAILURE_REASONS);
    }

    @Test
    void compatibilityProfileDoesNotClaimRuntimeOrFallback() {
        assertFalse(CouncilAssignCouncillorActionCapabilityProfile
                .NATIVE_CAPABILITY_ADVERTISED);
        assertFalse(CouncilAssignCouncillorActionCapabilityProfile.MCP_REGISTERED);
        assertFalse(CouncilAssignCouncillorActionCapabilityProfile.RUNTIME_CERTIFIED);
        assertFalse(CouncilAssignCouncillorActionCapabilityProfile.NATIVE_LIVE_READY);
        assertFalse(CouncilAssignCouncillorActionCapabilityProfile.FORMAL_STRATEGY_LIVE);
        assertFalse(CouncilAssignCouncillorActionCapabilityProfile.FALLBACK_AVAILABLE);

        var action = CouncilAssignCouncillorActionCapabilityProfile.ASSIGN_COUNCILLOR;
        assertFalse(action.readOnly());
        assertFalse(action.deterministic());
        assertFalse(action.nativeCertified());
        assertFalse(action.runtimeCertified());
        assertFalse(action.certified());
        assertTrue(action.invariants().contains(
                "native_helper_invocation_is_not_queue_acceptance"));
        assertTrue(action.invariants().contains(
                "production_capability_is_not_advertised"));
        assertTrue(action.invariants().contains("no_fallback_is_available"));
    }

    @Test
    void exactBuildAndUpstreamPairingAreExplicit() {
        assertEquals(1, CouncilAssignCouncillorActionCapabilityProfile.SCHEMA_VERSION);
        assertEquals("1.19.0.6", Ck3Profile11906.GAME_VERSION);
        assertEquals(
                "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86",
                Ck3Profile11906.EXE_SHA256);
        assertEquals(
                "c9ae3ceb3fe1cb8f37fd412e071e2cdd0c97f0f5",
                CouncilAssignCouncillorActionCapabilityProfile.UPSTREAM_PROVIDER_COMMIT);
        assertEquals(
                "paired_to_upstream_static_action_pending_runtime_and_live",
                CouncilAssignCouncillorActionCapabilityProfile.UPSTREAM_PAIRING_STATUS);
        assertEquals(
                "static_action_contract_runtime_and_live_pending",
                CouncilAssignCouncillorActionCapabilityProfile.CONTRACT_STAGE);
    }
}
