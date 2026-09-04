package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZhongguoManagerGovernanceCapabilityProfileTest {
    @Test
    void snapshotContractIsReadOnlyHashBoundAndUncertified() {
        var capability = ZhongguoManagerGovernanceCapabilityProfile.SNAPSHOT;

        assertEquals(
                "game.command.query-zhongguo-manager-governance-snapshot-v1",
                capability.id());
        assertEquals("query-zhongguo-manager-governance-snapshot-v1",
                ZhongguoManagerGovernanceCapabilityProfile.STEP_ID);
        assertEquals("zhongguo.manager-governance",
                ZhongguoManagerGovernanceCapabilityProfile.CASE_KIND);
        assertEquals("zg361-bounded-ai-direct-manager-selection-v1",
                ZhongguoManagerGovernanceCapabilityProfile.BOUNDED_AI_MANAGER_DEPENDENCY);
        assertEquals(ZhongguoManagerGovernanceCapabilityProfile.ID,
                capability.profileVersion());
        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
        assertTrue(capability.requiredFields().contains(
                "subject_binding.bounded_ai_manager_dependency"));
        assertTrue(capability.requiredFields().contains(
                "f035.effective.actual_bottom_slots"));
        assertTrue(capability.requiredFields().contains(
                "f032.component8"));
        assertTrue(capability.invariants().contains(
                "two_complete_allowlist_reads_are_identical"));
        assertTrue(capability.invariants().contains(
                "arbitrary_character_variable_reads_are_forbidden"));
        assertEquals(64,
                ZhongguoManagerGovernanceCapabilityProfile.ROOT_SCHEMA_SHA256.length());
        assertEquals(64,
                ZhongguoManagerGovernanceCapabilityProfile.ROOT_CONTRACT_SHA256.length());
        assertEquals(64,
                ZhongguoManagerGovernanceCapabilityProfile.ROOT_ABI_SHA256.length());
        assertEquals(
                "6205dcab4947f67f296b9147a9ef4cdd292aa863283c49cea74a0370fa5a4684",
                ZhongguoManagerGovernanceCapabilityProfile.ROOT_ABI_SHA256);
        assertEquals(40,
                ZhongguoManagerGovernanceCapabilityProfile.ROOT_TRANSPORT_COMMIT.length());
        assertEquals(40,
                ZhongguoManagerGovernanceCapabilityProfile.ROOT_EFFECT_SPLIT_COMMIT.length());
        assertEquals(
                "fefb408e13c4ea2aa4c512d3e3900991f9c13f7b",
                ZhongguoManagerGovernanceCapabilityProfile.ROOT_SELECTOR_INTEGRATION_COMMIT);
    }

    @Test
    void capabilityDoesNotBroadenExistingRuntimeOrCertification() {
        var capability = ZhongguoManagerGovernanceCapabilityProfile.SNAPSHOT;

        assertEquals(45, capability.requiredFields().size());
        assertEquals(10, capability.invariants().size());
        assertFalse(capability.certified());
    }
}
