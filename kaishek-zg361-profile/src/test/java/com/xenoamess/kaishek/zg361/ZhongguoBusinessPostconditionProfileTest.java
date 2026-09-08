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
                "requested_subject_character_id"));
        assertTrue(projects.requiredFields().contains(
                "credit_project_portfolio.final_subject_character_id"));
        assertTrue(projects.requiredFields().contains(
                "credit_project_portfolio.pending_player_event"));
        assertTrue(projects.requiredFields().contains(
                "readiness.portfolio_closed"));
        assertTrue(projects.requiredFields().contains(
                "metrics_result.source_contribution_receipt_revision"));
        assertTrue(projects.invariants().contains(
                "provider_reads_explicit_subject_portfolio_closure_while_paused_player_is_bound_owner_or_subject"));
        assertTrue(projects.invariants().contains(
                "portfolio_closure_requires_final_tuple_conservation_and_no_pending_player_event"));
        assertTrue(projects.invariants().contains(
                "r303_exact_build_private_candidate_proves_explicit_subject_portfolio_closure_and_cp26_to_p3m229_lineage"));
        assertTrue(projects.invariants().contains(
                "metrics_receipt_id_and_revision_equal_contribution"));
        assertTrue(projects.invariants().contains(
                "cp26_ready_p3_absent_exposes_no_p3_result"));
        assertEquals(
                "45024edf723a75502728f8f07b345f4271697cfe",
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_ROOT_COMMIT);
        assertEquals(
                "bea30b47cee6fdc66c04e48a42ebf5ac0115c62a8ea34698fcd0428530f4649a",
                ZhongguoBusinessPostconditionProfile
                        .PROJECTS_METRICS_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "1624d793b9461dbf6d08c64f60219f3567bc9fdf2805afe94c60f6aaf4deac6c",
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_ABI_SHA256);
        assertEquals(
                "44f0429e8b5ab46db38611a9779198cefd97b0fe435c0c847f3cb57674732380",
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_SCHEMA_SHA256);
        assertEquals(
                "b2dba9ee76457d0ec72bd87464eec145618e76482bb77d4b6687a03c88a60436",
                ZhongguoBusinessPostconditionProfile
                        .PROJECTS_METRICS_PYTHON_CONTRACT_SHA256);
        assertEquals(
                "zg361-cp-portfolio-cp26-direct-p3m229-lineage-v3",
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_ALLOWLIST_ID);
        assertEquals(49,
                ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS_ALLOWLIST_COUNT);
        assertTrue(ZhongguoBusinessPostconditionProfile
                .PROJECTS_METRICS_CHECKPOINT_STATE_REQUIRED);
        assertTrue(ZhongguoBusinessPostconditionProfile
                .PROJECTS_METRICS_EXPLICIT_SUBJECT_REQUIRED);
        assertTrue(ZhongguoBusinessPostconditionProfile
                .PROJECTS_METRICS_PORTFOLIO_CLOSURE_REQUIRED);
        assertEquals(
                "XAR_CK3_ENABLE_ZHONGGUO_PROJECTS_METRICS_CANDIDATE_V1",
                ZhongguoBusinessPostconditionProfile
                        .PROJECTS_METRICS_PRIVATE_CANDIDATE_SWITCH);
        assertTrue(ZhongguoBusinessPostconditionProfile
                .PROJECTS_METRICS_PRIVATE_CANDIDATE_LIVE_TESTED);
        assertEquals(
                "926BBD25076F69205B8AAA7CCC366AB470227BCBE174017B7E86C282862D7B01",
                ZhongguoBusinessPostconditionProfile
                        .PROJECTS_METRICS_PAUSED_LIVE_ARTIFACT_SHA256);
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
