package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZhongguoPromotionSourceTransportCapabilityProfileTest {
    @Test
    void recordsOnlyTheTwoAdvertisedFailClosedTransports() {
        var query = ZhongguoPromotionSourceTransportCapabilityProfile
                .QUERY_TRANSPORT;
        var action = ZhongguoPromotionSourceTransportCapabilityProfile
                .ACTION_TRANSPORT;
        assertEquals(
                "game.contract.zhongguo-promotion-source-progress-v1-fail-closed",
                query.id());
        assertTrue(query.readOnly());
        assertTrue(query.deterministic());
        assertFalse(query.certified());
        assertEquals(
                "game.contract.zhongguo-review-now-action-v1-fail-closed",
                action.id());
        assertFalse(action.readOnly());
        assertFalse(action.deterministic());
        assertFalse(action.certified());
        assertNotEquals(
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .QUERY_CAPABILITY_ID,
                query.id());
        assertNotEquals(
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ACTION_CAPABILITY_ID,
                action.id());
    }

    @Test
    void pinsExactSourcesAndKeepsProductReadinessClosed() {
        assertEquals(
                "4974324fece6ba152bada6069f8a12718e4da8f6",
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ROOT_INTEGRATION_COMMIT);
        assertEquals(
                "4ebeb1463d421d2278d69b68ab9a070de776a3d38a7e03e54719b0401d6817b5",
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ROOT_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "f489cae78e8a15da7b284b93eb33a6ad3fe6fd5735899f797a4a0e690e75f400",
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ROOT_ABI_SHA256);
        assertEquals(
                "9f8acf825ed2df8410484f48a5da979f0fb290056cf5080936e1c52409d21094",
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ROOT_PYTHON_CONTRACT_SHA256);
        assertEquals(List.of(
                        "zg361_promotion_source_bridge_window",
                        "zg361_promotion_source_review_now_action",
                        "zg361_promotion_source_b1_active",
                        "zg361_promotion_source_central_active",
                        "zg361_promotion_source_pp_active",
                        "zg361_promotion_source_central_stage_1",
                        "zg361_promotion_source_central_stage_2",
                        "zg361_promotion_source_central_stage_3",
                        "zg361_promotion_source_central_stage_4",
                        "zg361_promotion_source_central_stage_5",
                        "zg361_promotion_source_central_stage_6",
                        "zg361_promotion_source_central_stage_7",
                        "zg361_promotion_source_central_stage_8",
                        "zg361_promotion_source_central_stage_9",
                        "zg361_promotion_source_central_stage_10",
                        "zg361_promotion_source_central_stage_11",
                        "zg361_promotion_source_central_status_0",
                        "zg361_promotion_source_central_status_1",
                        "zg361_promotion_source_central_status_2",
                        "zg361_promotion_source_central_status_3",
                        "zg361_promotion_source_central_status_4",
                        "zg361_promotion_source_central_status_5",
                        "zg361_promotion_source_cl_partial_open",
                        "zg361_promotion_source_cl_digest_pending",
                        "zg361_promotion_source_cl_cycle_matches",
                        "zg361_promotion_source_cl_frozen_positive",
                        "zg361_promotion_source_cl_expectations_match",
                        "zg361_promotion_source_cl_ah_complete",
                        "zg361_promotion_source_cl_ai_complete"),
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .FIXED_WIDGETS);
        assertFalse(
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .QUERY_PRODUCTION_CAPABILITY_ADVERTISED);
        assertFalse(
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ACTION_PRODUCTION_CAPABILITY_ADVERTISED);
        assertFalse(
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .PRODUCTION_LIVE_READY);
        assertFalse(
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ACTION_ACK_IS_STATE_EVIDENCE);
    }

    @Test
    void freezesQueryAndActionEvidenceBoundaries() {
        var query = ZhongguoPromotionSourceTransportCapabilityProfile
                .QUERY_TRANSPORT;
        assertTrue(query.requiredFields().contains(
                "readiness.production_live_ready"));
        assertTrue(query.invariants().contains(
                "fixed_twenty_nine_widget_allowlist_is_exact"));
        var action = ZhongguoPromotionSourceTransportCapabilityProfile
                .ACTION_TRANSPORT;
        assertTrue(action.requiredFields().contains("action_ack"));
        assertTrue(action.invariants().contains(
                "accepted_ack_is_verification_pending_not_state_evidence"));
        assertTrue(action.invariants().contains(
                "independent_later_progress_query_must_prove_b1_entry"));
    }
}
