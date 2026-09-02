package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZhongguoBusinessPostconditionProfileTest {
    @Test
    void registersBothReadOnlyUncertifiedCapabilities() {
        assertEquals(2, ZhongguoBusinessPostconditionProfile.all().size());
        var projects = ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS;
        assertSame(projects,
                ZhongguoBusinessPostconditionProfile.require(projects.id()));
        assertEquals(ZhongguoBusinessPostconditionProfile.ID,
                projects.profileVersion());
        assertTrue(projects.readOnly());
        assertTrue(projects.deterministic());
        assertFalse(projects.nativeCertified());
        assertFalse(projects.runtimeCertified());
        assertFalse(projects.certified());
        assertTrue(projects.requiredFields().contains(
                "metrics_result.source_contribution_receipt_revision"));
        assertTrue(projects.invariants().contains(
                "metrics_receipt_id_and_revision_equal_contribution"));

        var promotion = ZhongguoBusinessPostconditionProfile.PROMOTION_COMPENSATION;
        assertSame(promotion,
                ZhongguoBusinessPostconditionProfile.require(promotion.id()));
        assertFalse(promotion.certified());
        assertTrue(promotion.requiredFields().contains(
                "portfolio.delivered_result_case"));
        assertTrue(promotion.invariants().contains(
                "t_and_l_ae_af_kernel_case_identities_remain_independent"));
    }

    @Test
    void unknownCapabilityFailsClosed() {
        assertTrue(ZhongguoBusinessPostconditionProfile.find(null).isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> ZhongguoBusinessPostconditionProfile.require("future-capability"));
    }
}
