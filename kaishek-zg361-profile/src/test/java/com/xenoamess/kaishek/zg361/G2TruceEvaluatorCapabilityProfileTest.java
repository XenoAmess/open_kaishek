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

    @Test
    void providerPinsSeparatePrivateProofFromProductionValidation() {
        assertEquals(
                "a3c13246ef32b35e117b08dbb86f61986c1dabe3",
                G2TruceEvaluatorCapabilityProfile.ROOT_PROVIDER_COMMIT);
        assertEquals(
                "0b0fbc047610a8ef25f47a59f7b42c83c176d69e",
                G2TruceEvaluatorCapabilityProfile.ROOT_PRODUCTION_CANDIDATE_COMMIT);
        assertEquals(
                "df720cd33d3606634378a5cff20d77227b82a35265269789bde4a51cff988e0d",
                G2TruceEvaluatorCapabilityProfile.ROOT_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "6b5783bca00a1b082aa5fec834ee73a95860535549b7525a616c82f178265c58",
                G2TruceEvaluatorCapabilityProfile.ROOT_PRODUCTION_MANIFEST_SHA256);
        assertEquals(
                "5299c88f4cd7b27959e4518d5a48061ae0ef39ae629a2590c269a8fe912f397a",
                G2TruceEvaluatorCapabilityProfile.ROOT_PROVIDER_SOURCE_SHA256);
        assertEquals(
                "e49d31f35fbb3f5bc713ea94cb9ff3e83ec9fa713772968a0dcffefd20200b2a",
                G2TruceEvaluatorCapabilityProfile.ROOT_PROVIDER_HEADER_SHA256);
        assertFalse(G2TruceEvaluatorCapabilityProfile.PUBLIC_SCHEMA_CHANGED);
        assertTrue(G2TruceEvaluatorCapabilityProfile.PRIVATE_LEAF_READER_LIVE_OBSERVED);
        assertTrue(G2TruceEvaluatorCapabilityProfile.DEFAULT_PRODUCTION_LEAF_READER_INSTALLED);
        assertFalse(G2TruceEvaluatorCapabilityProfile.DEFAULT_PRODUCTION_BINARY_LIVE_VALIDATED);
        assertFalse(G2TruceEvaluatorCapabilityProfile.EXPIRY_OBSERVABLE);
        assertFalse(G2TruceEvaluatorCapabilityProfile.EVALUATED_DAYS.certified());
    }
}
