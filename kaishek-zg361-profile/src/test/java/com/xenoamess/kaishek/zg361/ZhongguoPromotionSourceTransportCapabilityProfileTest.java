package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

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
                "d077bcf0114f227d319d8f23f64385ba6950238b",
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ROOT_INTEGRATION_COMMIT);
        assertEquals(
                "a167bfe43cb1b0254e124abebef954a5fb8b2164afee31b16be8badc5e8fa786",
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ROOT_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "eb22c5339a483614e75cd5135b896742ac9e0040166ac9689fb8af3070c94068",
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ROOT_ABI_SHA256);
        assertEquals(
                "5cfa9fdea255b180612cace27687e9b3c89fa884f2a9fa92ac2c268c19876aea",
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .ROOT_PYTHON_CONTRACT_SHA256);
        assertEquals(5,
                ZhongguoPromotionSourceTransportCapabilityProfile
                        .FIXED_WIDGETS.size());
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
                "fixed_five_widget_allowlist_is_exact"));
        var action = ZhongguoPromotionSourceTransportCapabilityProfile
                .ACTION_TRANSPORT;
        assertTrue(action.requiredFields().contains("action_ack"));
        assertTrue(action.invariants().contains(
                "accepted_ack_is_verification_pending_not_state_evidence"));
        assertTrue(action.invariants().contains(
                "independent_later_progress_query_must_prove_b1_entry"));
    }
}
