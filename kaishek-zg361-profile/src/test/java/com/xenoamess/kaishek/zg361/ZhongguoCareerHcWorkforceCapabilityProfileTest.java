package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZhongguoCareerHcWorkforceCapabilityProfileTest {
    @Test
    void postconditionIsReadOnlyExactBuildAndUncertified() {
        var capability = ZhongguoCareerHcWorkforceCapabilityProfile.POSTCONDITION;

        assertEquals(
                "game.command.query-zhongguo-career-hc-workforce-postcondition-v1",
                capability.id());
        assertEquals(
                "query-zhongguo-career-hc-workforce-postcondition-v1",
                ZhongguoCareerHcWorkforceCapabilityProfile.STEP_ID);
        assertEquals(
                "zhongguo.career-hc.workforce.route-b-no-hc-debit",
                ZhongguoCareerHcWorkforceCapabilityProfile.CASE_KIND);
        assertEquals(
                ZhongguoCareerHcWorkforceCapabilityProfile.ID,
                capability.profileVersion());
        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
    }

    @Test
    void postconditionRecordsProviderProjectionWithoutAdvertisingActions() {
        var capability = ZhongguoCareerHcWorkforceCapabilityProfile.POSTCONDITION;

        assertEquals(77, capability.requiredFields().size());
        assertEquals(12, capability.invariants().size());
        assertTrue(capability.requiredFields().contains(
                "m360_receipt.provider_observed"));
        assertTrue(capability.requiredFields().contains(
                "career_hc_partition.conserved"));
        assertTrue(capability.requiredFields().contains(
                "route_b_cost.manager_cost_total"));
        assertTrue(capability.requiredFields().contains(
                "binding.connection_generation"));
        assertTrue(capability.invariants().contains(
                "partition_sum_equals_authorized_and_conserved_is_true"));
        assertTrue(capability.invariants().contains(
                "action_ack_is_never_a_business_postcondition"));
        assertFalse(ZhongguoCareerHcWorkforceCapabilityProfile.DEFAULT_ADAPTER_ADVERTISED);
        assertFalse(
                ZhongguoCareerHcWorkforceCapabilityProfile.DOWNSTREAM_ACTION_CAPABILITY_ADVERTISED);
    }

    @Test
    void companionPinsMatchCanonicalProvider() {
        assertEquals(
                "8e91879901316bb165983f577887cdf7cb37c30ab7c98cf63d97f3fee77c0928",
                ZhongguoCareerHcWorkforceCapabilityProfile.ROOT_ABI_SHA256);
        assertEquals(
                "26b1f859cb6194d1d9295443be370862926dc81dabaa57d1f447f62c09a4062e",
                ZhongguoCareerHcWorkforceCapabilityProfile.ROOT_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "453b9a93fde9d472aa82c687bb2fd3db4fbef06d315ab91dcc4e167fa2f5b6c5",
                ZhongguoCareerHcWorkforceCapabilityProfile.ROOT_SCHEMA_SHA256);
        assertEquals(
                "398210553c74fdefa800b77f1287570598e4c2c4c73be35f243bec889bc14c76",
                ZhongguoCareerHcWorkforceCapabilityProfile.ROOT_PYTHON_CONTRACT_SHA256);
        assertEquals(
                "5e6fc9a0073ea7bbf9542bb3d95dfcd812c3a1f6",
                ZhongguoCareerHcWorkforceCapabilityProfile.ROOT_PROVIDER_COMMIT);
    }
}
