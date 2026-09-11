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
                "r470_native_reader_rejected_active_war_only_target_as_target_not_declarable"));
        assertTrue(capability.invariants().contains(
                "corrected_native_frame_freezes_declaration_and_active_war_sources_separately"));
        assertTrue(capability.invariants().contains(
                "corrected_native_reader_admits_either_source"));
        assertTrue(capability.invariants().contains(
                "r471_two_official_queries_are_identical_on_one_unchanged_paused_frame"));
        assertTrue(capability.invariants().contains(
                "r471_native_command_history_contains_only_two_successful_read_only_queries"));
        assertTrue(capability.invariants().contains(
                "strategic_power_observation_does_not_certify_campaign_dominance"));
    }

    @Test
    void readOnlyNativeQueryIsNarrowlyRuntimeCertified() {
        var capability = ActiveWarStrategicPowerCapabilityProfile.QUERY;

        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertTrue(capability.nativeCertified());
        assertTrue(capability.runtimeCertified());
        assertTrue(capability.certified());
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
                "283904d5438e07c18a49903333eafcaaabb75e80",
                ActiveWarStrategicPowerCapabilityProfile.ROOT_PROVIDER_COMMIT);
        assertEquals(
                "81f76e04704305df214e34961a32b768ce14a485",
                ActiveWarStrategicPowerCapabilityProfile.ROOT_PYTHON_SCOPE_COMMIT);
        assertEquals(
                "4ac06131ce04a4af02cbe9898b8eca780c8aa181",
                ActiveWarStrategicPowerCapabilityProfile.ROOT_R470_EVIDENCE_COMMIT);
        assertEquals(
                "CCF29894130EB673C59FDAD03E39D15A6BEB649392C6A2D02E1DF8A92C7E022D",
                ActiveWarStrategicPowerCapabilityProfile.ROOT_R470_REPORT_SHA256);
        assertEquals(
                "050c94fbd2ba9ecd41f4419e1dc936bd7c083774",
                ActiveWarStrategicPowerCapabilityProfile.ROOT_R471_EVIDENCE_COMMIT);
        assertEquals(
                "F467676201497A75C08ED5F6C72AFE64618337C73EFD2BA816B981470CE1E7CD",
                ActiveWarStrategicPowerCapabilityProfile.ROOT_R471_REPORT_SHA256);
        assertEquals(
                "D8F43EABC2A38F451FCAB1FE8DAEEB157EF8C62439B904DF96E8AFA301E924C9",
                ActiveWarStrategicPowerCapabilityProfile.ROOT_R471_RECLASSIFICATION_SHA256);
        for (String hash : List.of(
                ActiveWarStrategicPowerCapabilityProfile.ROOT_R471_RUNNER_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_R471_RUNNER_TEST_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_MCP_SERVER_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_PYTHON_CONTRACT_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_SERVICE_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_NATIVE_DRIVER_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_BRIDGE_TEST_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_CONTRACT_TEST_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_NATIVE_FRAME_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_NATIVE_READER_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_BRIDGE_CPP_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_NATIVE_UNIT_TEST_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_NATIVE_SOURCE_CONTRACT_TEST_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_NATIVE_ABI_SHA256,
                ActiveWarStrategicPowerCapabilityProfile.ROOT_NATIVE_CANDIDATE_DLL_SHA256)) {
            assertTrue(hash.matches("[0-9A-F]{64}"));
        }
    }
}
