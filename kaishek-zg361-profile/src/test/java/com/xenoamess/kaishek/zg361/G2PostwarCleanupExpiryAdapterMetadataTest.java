package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class G2PostwarCleanupExpiryAdapterMetadataTest {
    @Test
    void pinsTheIntegratedAdapterAndStaticReceipts() {
        assertEquals(
                "a01f8cb684d39e2ea8e95fbf0f20f170b6f1a396",
                G2PostwarCleanupExpiryAdapterMetadata
                        .ROOT_INTEGRATION_COMMIT);
        assertEquals(
                "beb17743a6440650eec2ca9c0bf270733bce2527",
                G2PostwarCleanupExpiryAdapterMetadata.ROOT_SOURCE_COMMIT);
        assertEquals(
                "7ce021720d0749288142040da5233f7bbdddd5a2f3b8ac187df1f72770b5a051",
                G2PostwarCleanupExpiryAdapterMetadata.ROOT_MANIFEST_SHA256);
        assertEquals(
                "7d8a0e3ce8560b7153ec5e4a89407aa019d0ea20671254d1286351880ad5d80d",
                G2PostwarCleanupExpiryAdapterMetadata.ROOT_RUNNER_SHA256);
        assertEquals(
                "GREEN_STATIC_ADAPTER_LIVE_BLOCKED_ON_CLEANUP_DISPATCH",
                G2PostwarCleanupExpiryAdapterMetadata.STATUS);
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
    void keepsCleanupAndProductReadinessClosed() {
        assertTrue(G2PostwarCleanupExpiryAdapterMetadata.METADATA_ONLY);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .ACTUAL_EXPIRY_QUERY_DISPATCH_PRESENT);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .CLEANUP_CANDIDATE_LIBRARY_PRESENT);
        assertTrue(
                G2PostwarCleanupExpiryAdapterMetadata
                        .SAME_LIFECYCLE_NATIVE_CLEANUP_REQUIRED);
        assertFalse(G2PostwarCleanupExpiryAdapterMetadata.DEFAULT_ENABLED);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .PUBLIC_CAPABILITY_ADDED);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .CLEANUP_QUERY_DISPATCH_PRESENT);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .OLD_WAR_ABSENCE_SUFFICIENT);
        assertFalse(
                G2PostwarCleanupExpiryAdapterMetadata
                        .PYTHON_ADAPTER_MAY_INFER_CLEANUP);
        assertFalse(G2PostwarCleanupExpiryAdapterMetadata.LIVE_AUTHORIZED);
        assertFalse(
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
}
