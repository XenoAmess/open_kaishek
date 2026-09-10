package com.xenoamess.kaishek.operator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperatorMcpCapabilityProfileTest {
    @Test
    void providerVersionToolsAndRequestShapesArePinned() {
        assertEquals("1.1.0", OperatorMcpCapabilityProfile.CONTRACT_VERSION);
        assertEquals("1.0.0", OperatorMcpCapabilityProfile.LEGACY_CONTRACT_VERSION);
        assertEquals(1, OperatorMcpCapabilityProfile.PROFILE_SCHEMA_VERSION);
        assertEquals(
                "5610de6bae4f5ac7e04ba3bedb25084837608183",
                OperatorMcpCapabilityProfile.ROOT_PROVIDER_COMMIT);
        assertEquals(
                List.of(
                        "operator_get_capabilities",
                        "operator_get_status",
                        "operator_preflight_job",
                        "operator_handoff_job"),
                OperatorMcpCapabilityProfile.LEGACY_TOOLS);
        assertEquals(
                List.of(
                        "operator_get_capabilities",
                        "operator_get_status",
                        "operator_preflight_job",
                        "operator_handoff_job",
                        "operator_control_job"),
                OperatorMcpCapabilityProfile.TOOLS);
        assertEquals(List.of(), OperatorMcpCapabilityProfile.GET_CAPABILITIES_REQUEST_FIELDS);
        assertEquals(
                List.of("target_id"),
                OperatorMcpCapabilityProfile.GET_STATUS_REQUEST_FIELDS);
        assertEquals(
                List.of("target_id", "job_name"),
                OperatorMcpCapabilityProfile.PREFLIGHT_JOB_REQUEST_FIELDS);
        assertEquals(
                List.of("target_id", "job_name", "request_id"),
                OperatorMcpCapabilityProfile.HANDOFF_JOB_REQUEST_FIELDS);
        assertEquals(
                List.of(
                        "target_id", "job_name", "job_id", "control_name", "request_id"),
                OperatorMcpCapabilityProfile.CONTROL_JOB_REQUEST_FIELDS);
        assertTrue(OperatorMcpCapabilityProfile.isCompatibleUpgrade("1.0.0", "1.1.0"));
        assertFalse(OperatorMcpCapabilityProfile.isCompatibleUpgrade("1.1.0", "1.0.0"));
    }

    @Test
    void readToolsAndIdempotentHandoffRemainDistinct() {
        for (var capability : List.of(
                OperatorMcpCapabilityProfile.GET_CAPABILITIES,
                OperatorMcpCapabilityProfile.GET_STATUS,
                OperatorMcpCapabilityProfile.PREFLIGHT_JOB)) {
            assertTrue(capability.readOnly());
            assertFalse(capability.nativeCertified());
            assertFalse(capability.runtimeCertified());
        }
        assertFalse(OperatorMcpCapabilityProfile.HANDOFF_JOB.readOnly());
        assertFalse(OperatorMcpCapabilityProfile.HANDOFF_JOB.deterministic());
        assertTrue(OperatorMcpCapabilityProfile.HANDOFF_JOB.invariants().contains(
                "request_id_is_portable_and_idempotent_per_server_instance"));
        assertTrue(OperatorMcpCapabilityProfile.HANDOFF_JOB.invariants().contains(
                "caller_cannot_supply_a_command_or_working_directory"));
        assertFalse(OperatorMcpCapabilityProfile.CONTROL_JOB.readOnly());
        assertFalse(OperatorMcpCapabilityProfile.CONTROL_JOB.deterministic());
        assertTrue(OperatorMcpCapabilityProfile.CONTROL_JOB.invariants().contains(
                "caller_cannot_supply_stdin_payload"));
        assertTrue(OperatorMcpCapabilityProfile.CONTROL_JOB.invariants().contains(
                "red_write_results_are_frozen_and_not_silently_retried"));
        for (var capability : List.of(
                OperatorMcpCapabilityProfile.GET_CAPABILITIES,
                OperatorMcpCapabilityProfile.GET_STATUS,
                OperatorMcpCapabilityProfile.PREFLIGHT_JOB,
                OperatorMcpCapabilityProfile.HANDOFF_JOB,
                OperatorMcpCapabilityProfile.CONTROL_JOB)) {
            assertTrue(capability.profileVersion().endsWith("-1.1.0"));
        }
    }

    @Test
    void providerInputsAreExactHashPins() {
        for (String hash : List.of(
                OperatorMcpCapabilityProfile.ROOT_IMPLEMENTATION_SHA256,
                OperatorMcpCapabilityProfile.ROOT_SERVER_ENTRYPOINT_SHA256,
                OperatorMcpCapabilityProfile.ROOT_PROFILE_EXAMPLE_SHA256,
                OperatorMcpCapabilityProfile.ROOT_CONTRACT_TEST_SHA256)) {
            assertTrue(hash.matches("[0-9A-F]{64}"));
        }
    }
}
