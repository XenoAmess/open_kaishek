package com.xenoamess.kaishek.operator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OperatorMcpCapabilityProfileTest {
    @Test
    void providerVersionToolsAndRequestShapesArePinned() {
        assertEquals("1.0.0", OperatorMcpCapabilityProfile.CONTRACT_VERSION);
        assertEquals(1, OperatorMcpCapabilityProfile.PROFILE_SCHEMA_VERSION);
        assertEquals(
                "e684fd05f94290d58e9fb7009046eb95d60468f4",
                OperatorMcpCapabilityProfile.ROOT_PROVIDER_COMMIT);
        assertEquals(
                List.of(
                        "operator_get_capabilities",
                        "operator_get_status",
                        "operator_preflight_job",
                        "operator_handoff_job"),
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
