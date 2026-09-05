package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class G2PostwarCleanupExpiryAdapterMetadataTest {
    @Test
    void pinsTheIntegratedAdapterAndStaticReceipts() {
        assertEquals(
                "ff89dcdbefb9d8fc86ce4722df847946e96d0e81",
                G2PostwarCleanupExpiryAdapterMetadata
                        .ROOT_INTEGRATION_COMMIT);
        assertEquals(
                "7aae7e064b6e224dd3a5b95070b54d9205c32cf4",
                G2PostwarCleanupExpiryAdapterMetadata.ROOT_SOURCE_COMMIT);
        assertEquals(
                "7874094361e8de6b38f77441b1ff59f512afcd13c309e0ffd02147185e86375f",
                G2PostwarCleanupExpiryAdapterMetadata.ROOT_MANIFEST_SHA256);
        assertEquals(
                "40696417f3bbd841e79116612697654c48868c90534bdfc0fdf43e161fdb47c8",
                G2PostwarCleanupExpiryAdapterMetadata.ROOT_RUNNER_SHA256);
        assertEquals(
                "PRIVATE_DISPATCH_LIVE_PRIMITIVE_COMPARISON_PENDING",
                G2PostwarCleanupExpiryAdapterMetadata.STATUS);
        assertEquals(
                "game.command.query-raiktor-war-bound-loss-cleanup-v1-N",
                G2PostwarCleanupExpiryAdapterMetadata.CLEANUP_CAPABILITY_ID);
        assertEquals(
                "1863e7b53d852b83f8fc3432e66c90eee72e73ecfbcbdacc1f48e47c232ab4d9",
                G2PostwarCleanupExpiryAdapterMetadata
                        .ROOT_CLEANUP_DISPATCH_SOURCE_CONTRACT_SHA256);
    }

    @Test
    void pinsTheSyntheticJoinVectorWithoutCallingItLive() {
        assertEquals(50331699,
                G2PostwarCleanupExpiryAdapterMetadata.WAR_ID);
        assertEquals(29829,
                G2PostwarCleanupExpiryAdapterMetadata.PLAYER_CHARACTER_ID);
        assertEquals(36769,
                G2PostwarCleanupExpiryAdapterMetadata
                        .PRIMARY_DEFENDER_CHARACTER_ID);
        assertEquals(598,
                G2PostwarCleanupExpiryAdapterMetadata
                        .PRE_TERMINATION_SOLDIERS);
        assertEquals(0,
                G2PostwarCleanupExpiryAdapterMetadata
                        .POST_TERMINATION_SOLDIERS);
        assertEquals(598,
                G2PostwarCleanupExpiryAdapterMetadata
                        .PROVEN_BOUNDARY_SOLDIERS_LOST);
        assertTrue(G2PostwarCleanupExpiryAdapterMetadata.SYNTHETIC_FIXTURE);
        assertFalse(G2PostwarCleanupExpiryAdapterMetadata.FIXTURE_IS_LIVE);
    }

    @Test
    void recordsPrivateCleanupWithoutPromotingProductReadiness() {
        assertTrue(G2PostwarCleanupExpiryAdapterMetadata.METADATA_ONLY);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .ACTUAL_EXPIRY_QUERY_DISPATCH_PRESENT);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .CLEANUP_CANDIDATE_LIBRARY_PRESENT);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .CLEANUP_QUERY_DISPATCH_PRESENT);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata.CLEANUP_QUERY_PRIVATE);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .SAME_LIFECYCLE_NATIVE_CLEANUP_REQUIRED);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .WAR_ID_ABSENCE_ADMISSION_ONLY);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .DESTROYED_RESULT_FROM_EXACT_STORES);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .ADAPTER_ISSUES_CLEANUP_QUERY);
        assertFalse(G2PostwarCleanupExpiryAdapterMetadata.DEFAULT_ENABLED);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .PUBLIC_CAPABILITY_ADDED);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .CLEANUP_DISPATCH_LIVE_TESTED);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .OLD_WAR_ABSENCE_SUFFICIENT);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .PYTHON_ADAPTER_MAY_INFER_CLEANUP);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .EXTERNAL_CLEANUP_INJECTION_ALLOWED);
        assertFalse(G2PostwarCleanupExpiryAdapterMetadata.LIVE_AUTHORIZED);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata.RUNTIME_CLEANUP_READY);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .PUBLIC_READINESS_PROMOTED);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .ACTION_READINESS_PROMOTED);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .SOURCE_SPECIFIC_ATTRIBUTION_READY);
        assertFalse(G2PostwarCleanupExpiryAdapterMetadata.DECISION_READY);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .AUTOMATIC_SURRENDER_READY);
        assertFalse(G2PostwarCleanupExpiryAdapterMetadata.GEN_034_RESOLVED);
    }

    @Test
    void bindsTheImmutableR3LiveAttemptSeparatelyFromSyntheticEvidence() {
        assertEquals("e72f9fa302811a823479635648eb008a6f5d8418",
                G2PostwarCleanupExpiryAdapterMetadata.LIVE_ROOT_SOURCE_COMMIT);
        assertEquals("GREEN_ACTION_BOUND_POSTWAR_RETENTION_EXPIRY",
                G2PostwarCleanupExpiryAdapterMetadata.LIVE_RESULT);
        assertEquals("2113032784cc3acc5da14557c14315b0aec9af03cdc15654739a3c54704f96da",
                G2PostwarCleanupExpiryAdapterMetadata.LIVE_MANIFEST_SHA256);
        assertEquals("44e1f7c0b470b2cf7b6549192865402f21f88c7cf073e896de1b93632311d5d0",
                G2PostwarCleanupExpiryAdapterMetadata.LIVE_REPORT_SHA256);
        assertEquals("4d839524098891bd997009663e189929722746ab0404d88c1e91f7546efe238b",
                G2PostwarCleanupExpiryAdapterMetadata.LIVE_CANDIDATE_DLL_SHA256);
        assertEquals(53267736,
                G2PostwarCleanupExpiryAdapterMetadata.LIVE_ACTUAL_EXPIRY_DATE_RAW);
        assertTrue(G2PostwarCleanupExpiryAdapterMetadata.SYNTHETIC_FIXTURE);
        assertFalse(G2PostwarCleanupExpiryAdapterMetadata.FIXTURE_IS_LIVE);
    }
}
