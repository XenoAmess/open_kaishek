package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActiveWarStrategicPowerCapabilityProfileTest {
    @Test
    void publicIdentityRequestAndAdditiveScopeArePinned() {
        assertEquals(
                "ck3_query_war_entry_assessments",
                ActiveWarStrategicPowerCapabilityProfile.QUERY.id());
        assertEquals(
                "game.command.query-war-entry-assessments-v1-N",
                ActiveWarStrategicPowerCapabilityProfile.CAPABILITY_ID);
        assertEquals(
                "query-war-entry-assessments-v1-1-<target_character_id>",
                ActiveWarStrategicPowerCapabilityProfile.STEP_TEMPLATE);
        assertEquals(
                List.of("target_character_ids", "expected_revision"),
                ActiveWarStrategicPowerCapabilityProfile.REQUEST_FIELDS);
        assertEquals(
                List.of("declarable_war", "active_war_primary_opponent"),
                ActiveWarStrategicPowerCapabilityProfile.TARGET_SOURCES);
        assertEquals(1, ActiveWarStrategicPowerCapabilityProfile.SCHEMA_VERSION);
        assertEquals(1, ActiveWarStrategicPowerCapabilityProfile.MAXIMUM_TARGETS);
    }

    @Test
    void responseAndScopeInvariantsAreExplicit() {
        var capability = ActiveWarStrategicPowerCapabilityProfile.QUERY;

        assertTrue(capability.requiredFields().contains("target_scopes"));
        assertTrue(capability.requiredFields().contains("war_entry_assessments"));
        assertTrue(capability.invariants().contains(
                "target_must_be_a_current_declaration_or_active_war_primary_opponent"));
        assertTrue(capability.invariants().contains(
                "target_scope_is_rechecked_after_the_native_result"));
        assertTrue(capability.invariants().contains(
                "native_targets_declarable_ready_is_a_legacy_approved_scope_spelling"));
    }

    @Test
    void readOnlyNativeQueryAwaitsActiveWarRuntimeCertification() {
        var capability = ActiveWarStrategicPowerCapabilityProfile.QUERY;

        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertTrue(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
        assertTrue(capability.invariants().contains(
                "query_does_not_enable_declaration_surrender_or_white_peace"));
        assertTrue(capability.invariants().contains(
                "contract_does_not_depend_on_machine_account_path_or_ck3_round"));
    }

    @Test
    void exactBuildAndProviderFilesAreHashBound() {
        assertEquals("1.19.0.6", Ck3Profile11906.GAME_VERSION);
        assertEquals(
                "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86",
                Ck3Profile11906.EXE_SHA256);
        assertEquals(
                "81f76e04704305df214e34961a32b768ce14a485",
                ActiveWarStrategicPowerCapabilityProfile.ROOT_PROVIDER_COMMIT);
        for (String hash : List.of(
                ActiveWarStrategicPowerCapabilityProfile.ROOT_MCP_SERVER_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_PYTHON_CONTRACT_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_SERVICE_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_NATIVE_DRIVER_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_BRIDGE_TEST_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_CONTRACT_TEST_SHA256)) {
            assertTrue(hash.matches("[0-9A-F]{64}"));
        }
    }
}
