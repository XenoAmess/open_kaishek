package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoatOfArmsSourceProbeCapabilityProfileTest {
    @Test
    void publicIdentityRequestAndSchemasArePinned() {
        assertEquals(
                "ck3_probe_coat_of_arms_source_v1",
                CoatOfArmsSourceProbeCapabilityProfile.PROBE.id());
        assertEquals(
                "game.command.probe-coat-of-arms-source-v1",
                CoatOfArmsSourceProbeCapabilityProfile.CAPABILITY_ID);
        assertEquals(
                "probe-coat-of-arms-source-v1",
                CoatOfArmsSourceProbeCapabilityProfile.STEP_ID);
        assertEquals(
                List.of("source", "expected_revision", "apply"),
                CoatOfArmsSourceProbeCapabilityProfile.REQUEST_FIELDS);
        assertEquals("coat-of-arms-source-probe-v1",
                CoatOfArmsSourceProbeCapabilityProfile.RESULT_SCHEMA);
        assertEquals("xar.ck3.coat-of-arms-designer-probe.v1",
                CoatOfArmsSourceProbeCapabilityProfile.NATIVE_RESULT_SCHEMA);
        assertEquals(1, CoatOfArmsSourceProbeCapabilityProfile.SCHEMA_VERSION);
        assertEquals(131_072,
                CoatOfArmsSourceProbeCapabilityProfile.MAXIMUM_SOURCE_BYTES);
    }

    @Test
    void probeIsStatefulNativeInputAndRemainsUncertified() {
        var capability = CoatOfArmsSourceProbeCapabilityProfile.PROBE;
        assertFalse(capability.readOnly());
        assertFalse(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
        assertTrue(capability.invariants().contains(
                "detect_only_still_mutates_clipboard_and_paste_preview"));
        assertTrue(capability.invariants().contains(
                "applied_does_not_claim_upper_window_finish_or_saved_persistence"));
        assertTrue(capability.invariants().contains(
                "parameterized_probe_never_enters_autonomous_planner_actions"));
    }

    @Test
    void responseCarriesSourceAndSessionBindingEvidence() {
        var fields = CoatOfArmsSourceProbeCapabilityProfile.PROBE.requiredFields();
        for (String field : List.of(
                "status",
                "source_sha256",
                "source_bytes",
                "binding.mode",
                "binding.revision",
                "binding.connection_generation",
                "binding.bridge_pid")) {
            assertTrue(fields.contains(field), field);
        }
    }

    @Test
    void providerInputsAreExactHashPins() {
        assertEquals(
                "16755189c172772669a0d30456da1f977d6b6de0",
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_CAPABILITY_INTRODUCTION_COMMIT);
        assertEquals(
                "718a60d538249fb6eecd47284e622fd33d71d260",
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_PROVIDER_COMMIT);
        for (String hash : List.of(
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_MCP_SERVER_SHA256,
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_PYTHON_CONTRACT_SHA256,
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_NATIVE_ABI_SHA256,
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_NATIVE_IMPLEMENTATION_SHA256,
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_BRIDGE_BOOTSTRAP_SHA256,
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_SERVICE_SHA256,
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_NATIVE_DRIVER_SHA256,
                CoatOfArmsSourceProbeCapabilityProfile.ROOT_PROVIDER_TEST_SHA256)) {
            assertTrue(hash.matches("[0-9A-F]{64}"));
        }
        assertTrue(CoatOfArmsSourceProbeCapabilityProfile.PROBE.invariants().contains(
                "native_hook_bootstraps_before_gameplay_snapshot_availability"));
    }
}
