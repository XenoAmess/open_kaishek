package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZhongguoManagerSubordinateSelectorCapabilityProfileTest {
    @Test
    void selectorIsReadOnlyExactBuildAndHashBound() {
        var capability = ZhongguoManagerSubordinateSelectorCapabilityProfile.SELECTOR;

        assertEquals(
                "game.command.query-zhongguo-manager-subordinate-selector-v1",
                capability.id());
        assertEquals(
                "query-zhongguo-manager-subordinate-selector-v1",
                ZhongguoManagerSubordinateSelectorCapabilityProfile.STEP_ID);
        assertEquals(
                "zg361-bounded-ai-direct-manager-selection-v1",
                ZhongguoManagerSubordinateSelectorCapabilityProfile.SELECTOR_KIND);
        assertEquals(
                ZhongguoManagerSubordinateSelectorCapabilityProfile.ID,
                capability.profileVersion());
        assertEquals("1.19.0.6",
                ZhongguoManagerSubordinateSelectorCapabilityProfile.GAME_VERSION);
        assertEquals(64,
                ZhongguoManagerSubordinateSelectorCapabilityProfile.EXECUTABLE_SHA256.length());
        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
    }

    @Test
    void selectorRecordsPublicProjectionWithoutAdvertisingAnAction() {
        var capability = ZhongguoManagerSubordinateSelectorCapabilityProfile.SELECTOR;

        assertEquals(44, capability.requiredFields().size());
        assertEquals(11, capability.invariants().size());
        assertTrue(capability.requiredFields().contains(
                "selection.manager_character_id"));
        assertTrue(capability.requiredFields().contains(
                "selection.subordinate_character_id"));
        assertTrue(capability.requiredFields().contains(
                "readiness.relationship_enumeration_ready"));
        assertTrue(capability.invariants().contains(
                "caller_cannot_supply_character_or_eligibility_assertions"));
        assertTrue(capability.invariants().contains(
                "bad_relationship_storage_is_not_reclassified_as_no_candidate"));
        assertFalse(
                ZhongguoManagerSubordinateSelectorCapabilityProfile.DOWNSTREAM_ACTION_CAPABILITY_ADVERTISED);
        assertTrue(capability.id().startsWith("game.command.query-"));
    }

    @Test
    void companionPinsMatchCanonicalIntegration() {
        assertEquals(
                "b10d596bdc18842c4a582a932affd12fd035382c879477625a18f6a4417bf55a",
                ZhongguoManagerSubordinateSelectorCapabilityProfile.ROOT_ABI_SHA256);
        assertEquals(
                "b175ea8231e22614c144abad1b108b9f43ed60a9f9e36406ca00017700851057",
                ZhongguoManagerSubordinateSelectorCapabilityProfile.ROOT_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "26e5e2ce6ddc13c496ac497c476de3ebf935788b386ae6fdf5a58c049332a7b7",
                ZhongguoManagerSubordinateSelectorCapabilityProfile.ROOT_PYTHON_CONTRACT_SHA256);
        assertEquals(
                "fefb408e13c4ea2aa4c512d3e3900991f9c13f7b",
                ZhongguoManagerSubordinateSelectorCapabilityProfile.ROOT_INTEGRATION_COMMIT);
    }
}
