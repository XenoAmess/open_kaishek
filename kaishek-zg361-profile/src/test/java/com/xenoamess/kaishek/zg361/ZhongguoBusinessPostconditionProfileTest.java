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
        assertTrue(projects.requiredFields().contains("checkpoint_state"));
        assertTrue(projects.requiredFields().contains(
                "metrics_result.source_contribution_receipt_revision"));
        assertTrue(projects.invariants().contains(
                "metrics_receipt_id_and_revision_equal_contribution"));
        assertTrue(projects.invariants().contains(
                "cp26_ready_p3_absent_exposes_no_p3_result"));
        assertEquals(
                "953634265ebf298cec3f2cf3065060e577dc8d17",
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_ROOT_COMMIT);
        assertEquals(
                "362e9e88ff0a2ac8a7ec5cd396959a7200ed9f4f6d8519c953fe1798b903f0f2",
                ZhongguoBusinessPostconditionProfile
                        .PROJECTS_METRICS_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "c0135b790089de3807ae2139431c3cd1df3867d61408e36cd64df17b5dc4fadd",
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_ABI_SHA256);
        assertEquals(
                "3763b17f937d4c36c5643a41d54ccd449cd23a8f5f94cddb4a4edbed7bbdbfd4",
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_SCHEMA_SHA256);
        assertEquals(
                "468d4ce43a28606148290b00b715f04dc33ba7f5bf95299949afe37b66b37195",
                ZhongguoBusinessPostconditionProfile
                        .PROJECTS_METRICS_PYTHON_CONTRACT_SHA256);
        assertEquals(
                "zg361-cp26-direct-p3m229-lineage-v2",
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_ALLOWLIST_ID);
        assertTrue(ZhongguoBusinessPostconditionProfile
                .PROJECTS_METRICS_CHECKPOINT_STATE_REQUIRED);
        assertFalse(ZhongguoBusinessPostconditionProfile
                .PROJECTS_METRICS_DEFAULT_CANDIDATE_ENABLED);
        assertFalse(ZhongguoBusinessPostconditionProfile
                .PROJECTS_METRICS_PRODUCTION_LIVE);

        var promotion = ZhongguoBusinessPostconditionProfile.PROMOTION_COMPENSATION;
        assertSame(promotion,
                ZhongguoBusinessPostconditionProfile.require(promotion.id()));
        assertFalse(promotion.certified());
        assertTrue(promotion.requiredFields().contains(
                "portfolio.delivered_result_case"));
        assertTrue(promotion.invariants().contains(
                "t_and_l_ae_af_kernel_case_identities_remain_independent"));
        assertEquals(
                "cac1e85b616827a9ae11d755dd71f119325e6f3f",
                ZhongguoBusinessPostconditionProfile
                        .PROMOTION_COMPENSATION_ROOT_COMMIT);
        assertEquals(
                "98ab5f09bb44d6d5cb1062fea64e6fdf9e41cf160f64ecd8d5a644b9086ef627",
                ZhongguoBusinessPostconditionProfile
                        .PROMOTION_COMPENSATION_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "XAR_CK3_ENABLE_ZHONGGUO_PROMOTION_COMPENSATION_CANDIDATE_V1",
                ZhongguoBusinessPostconditionProfile
                        .PROMOTION_COMPENSATION_PRIVATE_CANDIDATE_SWITCH);
        assertTrue(ZhongguoBusinessPostconditionProfile
                .PROMOTION_COMPENSATION_PRIVATE_CANDIDATE_ADVERTISES);
        assertFalse(ZhongguoBusinessPostconditionProfile
                .PROMOTION_COMPENSATION_DEFAULT_SWITCH_ENABLED);
        assertFalse(ZhongguoBusinessPostconditionProfile
                .PROMOTION_COMPENSATION_DEFAULT_ADAPTER_ADVERTISED);
        assertFalse(ZhongguoBusinessPostconditionProfile
                .PROMOTION_COMPENSATION_CANDIDATE_LIVE_TESTED);
        assertFalse(ZhongguoBusinessPostconditionProfile
                .PROMOTION_COMPENSATION_PUBLIC_API_CHANGED);
        assertFalse(ZhongguoBusinessPostconditionProfile
                .PROMOTION_COMPENSATION_PRODUCTION_LIVE);
    }

    @Test
    void unknownCapabilityFailsClosed() {
        assertTrue(ZhongguoBusinessPostconditionProfile.find(null).isEmpty());
        assertThrows(IllegalArgumentException.class,
                () -> ZhongguoBusinessPostconditionProfile.require("future-capability"));
    }
}
