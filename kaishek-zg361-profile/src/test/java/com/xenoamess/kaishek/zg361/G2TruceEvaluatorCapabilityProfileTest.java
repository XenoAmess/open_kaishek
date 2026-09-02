package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class G2TruceEvaluatorCapabilityProfileTest {
    @Test
    void evaluatedDaysContractIsReadOnlyAndExplicitlyUncertified() {
        var capability = G2TruceEvaluatorCapabilityProfile.EVALUATED_DAYS;

        assertEquals(
                "game.command.query-g2-truce-evaluated-days-v1",
                capability.id());
        assertEquals(
                G2TruceEvaluatorCapabilityProfile.ID,
                capability.profileVersion());
        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
        assertTrue(capability.requiredFields().contains("evaluated_days"));
        assertTrue(capability.invariants().contains(
                "two_evaluator_reads_match_on_one_paused_frame"));
        assertTrue(capability.invariants().contains(
                "expiry_is_not_inferred_from_evaluated_days"));
    }
}
