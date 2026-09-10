package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZhongguoB1CycleSnapshotCapabilityProfileTest {
    @Test
    void snapshotIsReadOnlyExactBuildAndUncertified() {
        var capability = ZhongguoB1CycleSnapshotCapabilityProfile.SNAPSHOT;
        assertEquals("game.command.query-zhongguo-b1-cycle-snapshot-v1", capability.id());
        assertEquals("query-zhongguo-b1-cycle-snapshot-v1",
                ZhongguoB1CycleSnapshotCapabilityProfile.STEP_ID);
        assertEquals("ck3_query_zhongguo_b1_cycle_snapshot_v1",
                ZhongguoB1CycleSnapshotCapabilityProfile.MCP_TOOL_ID);
        assertEquals("zhongguo.b1.cycle", ZhongguoB1CycleSnapshotCapabilityProfile.CASE_KIND);
        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
    }

    @Test
    void projectionKeepsB1LivenessAndSafetySignals() {
        var capability = ZhongguoB1CycleSnapshotCapabilityProfile.SNAPSHOT;
        assertEquals(68, capability.requiredFields().size());
        assertEquals(14, capability.invariants().size());
        assertEquals(38, ZhongguoB1CycleSnapshotCapabilityProfile.FIXED_ALLOWLIST_COUNT);
        assertTrue(capability.requiredFields().contains("quota.rebuild_generation"));
        assertTrue(capability.requiredFields().contains("quota.target_top"));
        assertTrue(capability.requiredFields().contains("quota.recount_top"));
        assertTrue(capability.requiredFields().contains("closure.publication_blocked"));
        assertTrue(capability.requiredFields().contains("pending.watchdog_orphan_count"));
        assertTrue(capability.invariants().contains("quota_targets_match_recounts"));
        assertTrue(capability.invariants().contains("closed_state_is_coherent"));
    }

    @Test
    void companionPinsMatchCanonicalProviderCommit() {
        assertEquals(64, ZhongguoB1CycleSnapshotCapabilityProfile.ROOT_ABI_SHA256.length());
        assertEquals(64, ZhongguoB1CycleSnapshotCapabilityProfile.ROOT_SOURCE_CONTRACT_SHA256.length());
        assertEquals(64, ZhongguoB1CycleSnapshotCapabilityProfile.ROOT_SCHEMA_SHA256.length());
        assertEquals(64, ZhongguoB1CycleSnapshotCapabilityProfile.ROOT_PYTHON_CONTRACT_SHA256.length());
        assertEquals("4c4891eb899e736ad4f5c6eaabdaaedc2f296f05",
                ZhongguoB1CycleSnapshotCapabilityProfile.ROOT_PROVIDER_COMMIT);
    }
}
