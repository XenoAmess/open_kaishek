package com.xenoamess.kaishek.operator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OperatorMcpClientAdapterTest {
    private static final String PROFILE_SHA = "A".repeat(64);
    private static final String JOB_SHA = "B".repeat(64);
    private static final String EVIDENCE_SHA = "C".repeat(64);

    @Test
    void configuredTargetRunsReadOnlySequenceThenOneHandoff() {
        var target = OperatorMcpTarget.contractV1(
                "https://operator-host.invalid:9443/mcp",
                "operator-target-22",
                "managed-replay-cycle-88");
        var transport = new FakeTransport(target);
        var adapter = new OperatorMcpClientAdapter(target, transport);

        Map<String, Object> first = adapter.handoffJob("turn-991-attempt-1");
        Map<String, Object> cached = adapter.handoffJob("turn-991-attempt-1");

        assertEquals("ACCEPTED", first.get("result"));
        assertEquals(false, first.get("idempotent_replay"));
        assertSame(first, cached);
        assertEquals(1, transport.capabilitiesCalls);
        assertEquals(1, transport.statusCalls);
        assertEquals(1, transport.preflightCalls);
        assertEquals(1, transport.handoffCalls);
        assertEquals(
                List.of(
                        "operator_get_capabilities",
                        "operator_get_status",
                        "operator_preflight_job",
                        "operator_handoff_job"),
                transport.tools);
        assertEquals("https://operator-host.invalid:9443/mcp", transport.lastEndpoint);
        assertThrows(UnsupportedOperationException.class, () -> first.put("x", "y"));
    }

    @Test
    void lostFirstResponseRetriesSameRequestWithoutBlockedPreflight() {
        var target = OperatorMcpTarget.contractV1(
                "stdio://operator-endpoint-c",
                "target-c",
                "job-c");
        var transport = new FakeTransport(target);
        transport.loseFirstHandoffResponse = true;
        var adapter = new OperatorMcpClientAdapter(target, transport);

        assertThrows(
                SimulatedLostResponse.class,
                () -> adapter.handoffJob("portable-request-7"));
        Map<String, Object> replay = adapter.handoffJob("portable-request-7");

        assertEquals(true, replay.get("idempotent_replay"));
        assertEquals(2, transport.capabilitiesCalls);
        assertEquals(1, transport.statusCalls);
        assertEquals(1, transport.preflightCalls);
        assertEquals(2, transport.handoffCalls);
    }

    @Test
    void newClientCanExplicitlyResumeAnAmbiguousRequest() {
        var target = OperatorMcpTarget.contractV1(
                "mcp+test://host-d/session",
                "target-d",
                "job-d");
        var transport = new FakeTransport(target);
        transport.started = true;
        var recreated = new OperatorMcpClientAdapter(target, transport);

        Map<String, Object> replay = recreated.retryHandoffJob(
                "prior-request-44", "server-instance-fake");

        assertEquals(true, replay.get("idempotent_replay"));
        assertEquals(1, transport.capabilitiesCalls);
        assertEquals(0, transport.statusCalls);
        assertEquals(0, transport.preflightCalls);
        assertEquals(1, transport.handoffCalls);
    }

    @Test
    void recreatedClientRejectsRetryAgainstAnotherServerInstance() {
        var target = OperatorMcpTarget.contractV1(
                "mcp+test://host-f/session",
                "target-f",
                "job-f");
        var transport = new FakeTransport(target);
        transport.serverInstanceId = "server-instance-restarted";
        var recreated = new OperatorMcpClientAdapter(target, transport);

        assertThrows(
                OperatorMcpContractException.class,
                () -> recreated.retryHandoffJob(
                        "prior-request-45", "server-instance-original"));
        assertEquals(0, transport.handoffCalls);
    }

    @Test
    void redOrWrongIdentityNeverReachesHandoff() {
        var target = OperatorMcpTarget.contractV1("endpoint-e", "target-e", "job-e");
        var redTransport = new FakeTransport(target);
        redTransport.forceRed = true;
        var redAdapter = new OperatorMcpClientAdapter(target, redTransport);
        assertThrows(
                OperatorMcpContractException.class,
                () -> redAdapter.handoffJob("request-red"));
        assertEquals(0, redTransport.handoffCalls);

        var identityTransport = new FakeTransport(target);
        identityTransport.identityMatches = false;
        var identityAdapter = new OperatorMcpClientAdapter(target, identityTransport);
        assertThrows(
                OperatorMcpContractException.class,
                () -> identityAdapter.handoffJob("request-wrong-identity"));
        assertEquals(0, identityTransport.handoffCalls);
    }

    @Test
    void targetAndRequestValuesRemainPortableAndCallerSelected() {
        var first = OperatorMcpTarget.contractV1("endpoint-one", "target-one", "job-one");
        var second = OperatorMcpTarget.contractV1("endpoint-two", "target-two", "job-two");
        assertNotEquals(first, second);
        assertThrows(
                IllegalArgumentException.class,
                () -> OperatorMcpTarget.contractV1("endpoint", "bad target", "job"));
        var adapter = new OperatorMcpClientAdapter(first, new FakeTransport(first));
        assertThrows(IllegalArgumentException.class, () -> adapter.handoffJob("bad request"));
    }

    @Test
    void legacyContractDiscoversNoControlsAndRejectsControlLocally() {
        var target = OperatorMcpTarget.contractV1("endpoint-legacy", "target-old", "job-old");
        var transport = new FakeTransport(target);
        var adapter = new OperatorMcpClientAdapter(target, transport);

        assertEquals(List.of(), adapter.discoverControls());
        assertThrows(
                OperatorMcpContractException.class,
                () -> adapter.controlJob("job-id-old", "resume", "control-old"));
        assertEquals(0, transport.controlCalls);
    }

    @Test
    void legacyTargetAcceptsCompatibleUpgradeAndInvokesAdvertisedControl() {
        var target = OperatorMcpTarget.contractV1("endpoint-upgraded", "target-up", "job-up");
        var transport = new FakeTransport(target);
        transport.serverVersion = "1.1.0";
        transport.controls = List.of("resume", "stop");
        var adapter = new OperatorMcpClientAdapter(target, transport);

        assertEquals(List.of("resume", "stop"), adapter.discoverControls());
        Map<String, Object> response = adapter.controlJob(
                "job-id-fake", "resume", "control-request-1");
        Map<String, Object> cached = adapter.controlJob(
                "job-id-fake", "resume", "control-request-1");

        assertEquals("ACCEPTED", response.get("result"));
        assertEquals(false, response.get("idempotent_replay"));
        assertEquals(7, response.get("payload_bytes"));
        assertSame(response, cached);
        assertEquals(1, transport.controlCalls);
        assertEquals(
                Map.of(
                        "target_id", target.targetId(),
                        "job_name", target.jobName(),
                        "job_id", "job-id-fake",
                        "control_name", "resume",
                        "request_id", "control-request-1"),
                transport.lastControlArguments);
        assertThrows(UnsupportedOperationException.class, () -> response.put("x", "y"));
    }

    @Test
    void readinessDomainCarriesVersionAndSelectedJobControls() {
        var target = OperatorMcpTarget.contractV11(
                "endpoint-domain", "target-domain", "job-domain");
        var transport = new FakeTransport(target);
        transport.serverVersion = "1.1.0";
        transport.controls = List.of("advance", "finish");
        var adapter = new OperatorMcpClientAdapter(target, transport);

        OperatorMcpReadiness readiness = adapter.inspectReadiness();

        assertTrue(readiness.green());
        assertEquals("1.1.0", readiness.serverVersion());
        assertEquals(List.of("advance", "finish"), readiness.controlsFor(target.jobName()));
        assertThrows(
                OperatorMcpContractException.class,
                () -> readiness.controlsFor("another-job"));
    }

    @Test
    void stage10PlayerSubjectControlsRemainTargetProfileData() {
        var target = OperatorMcpTarget.contractV11(
                "endpoint-stage10", "target-stage10", "stage10-player-subject");
        var transport = new FakeTransport(target);
        transport.serverVersion = "1.1.0";
        transport.controls = List.of("status", "run-stage10", "cleanup");
        var adapter = new OperatorMcpClientAdapter(target, transport);

        assertEquals(transport.controls, adapter.discoverControls());
        Map<String, Object> response = adapter.controlJob(
                "job-id-fake", "run-stage10", "stage10-request-1");

        assertEquals("ACCEPTED", response.get("result"));
        assertEquals(
                Map.of(
                        "target_id", "target-stage10",
                        "job_name", "stage10-player-subject",
                        "job_id", "job-id-fake",
                        "control_name", "run-stage10",
                        "request_id", "stage10-request-1"),
                transport.lastControlArguments);
        assertThrows(
                OperatorMcpContractException.class,
                () -> adapter.controlJob(
                        "job-id-fake", "retry-stage10", "stage10-request-2"));
    }

    @Test
    void lostControlResponseRetriesSameBindingAndServerInstance() {
        var target = OperatorMcpTarget.contractV11(
                "endpoint-control-retry", "target-retry", "job-retry");
        var transport = new FakeTransport(target);
        transport.serverVersion = "1.1.0";
        transport.controls = List.of("continue");
        transport.loseFirstControlResponse = true;
        var adapter = new OperatorMcpClientAdapter(target, transport);

        assertThrows(
                SimulatedLostResponse.class,
                () -> adapter.controlJob(
                        "job-id-fake", "continue", "control-request-retry"));
        Map<String, Object> replay = adapter.controlJob(
                "job-id-fake", "continue", "control-request-retry");

        assertEquals(true, replay.get("idempotent_replay"));
        assertEquals(2, transport.controlCalls);
        assertThrows(
                OperatorMcpContractException.class,
                () -> adapter.controlJob(
                        "different-job", "continue", "control-request-retry"));
    }

    @Test
    void redControlResponseIsReturnedAndNeverRetried() {
        var target = OperatorMcpTarget.contractV11(
                "endpoint-control-red", "target-red", "job-red");
        var transport = new FakeTransport(target);
        transport.serverVersion = "1.1.0";
        transport.controls = List.of("stop");
        transport.controlRed = true;
        var adapter = new OperatorMcpClientAdapter(target, transport);

        Map<String, Object> red = adapter.controlJob(
                "job-id-fake", "stop", "control-red-request");
        Map<String, Object> cached = adapter.controlJob(
                "job-id-fake", "stop", "control-red-request");

        assertEquals("RED", red.get("result"));
        assertEquals("BrokenPipeError: configured failure", red.get("error"));
        assertSame(red, cached);
        assertEquals(1, transport.controlCalls);
    }

    @Test
    void newContractRejectsMissingControlMetadataOrOldServer() {
        var target = OperatorMcpTarget.contractV11("endpoint-new", "target-new", "job-new");
        var oldTransport = new FakeTransport(target);
        var oldAdapter = new OperatorMcpClientAdapter(target, oldTransport);
        assertThrows(OperatorMcpContractException.class, oldAdapter::getCapabilities);

        var missingMetadata = new FakeTransport(target);
        missingMetadata.serverVersion = "1.1.0";
        missingMetadata.omitJobControls = true;
        var missingAdapter = new OperatorMcpClientAdapter(target, missingMetadata);
        assertThrows(OperatorMcpContractException.class, missingAdapter::getCapabilities);
    }

    private static final class FakeTransport implements OperatorMcpTransport {
        private final OperatorMcpTarget target;
        private final List<String> tools = new ArrayList<>();
        private int capabilitiesCalls;
        private int statusCalls;
        private int preflightCalls;
        private int handoffCalls;
        private int controlCalls;
        private boolean identityMatches = true;
        private boolean forceRed;
        private boolean loseFirstHandoffResponse;
        private boolean loseFirstControlResponse;
        private boolean controlRed;
        private boolean omitJobControls;
        private boolean started;
        private List<String> controls = List.of();
        private String lastEndpoint;
        private String serverInstanceId = "server-instance-fake";
        private String serverVersion = "1.0.0";
        private Map<String, Object> lastControlArguments;

        private FakeTransport(OperatorMcpTarget target) {
            this.target = target;
        }

        @Override
        public Map<String, Object> call(
                String endpoint,
                String toolId,
                Map<String, Object> arguments) {
            lastEndpoint = endpoint;
            tools.add(toolId);
            return switch (toolId) {
                case "operator_get_capabilities" -> capabilities();
                case "operator_get_status" -> status(arguments);
                case "operator_preflight_job" -> preflight(arguments);
                case "operator_handoff_job" -> handoff(arguments);
                case "operator_control_job" -> control(arguments);
                default -> throw new AssertionError("unexpected tool " + toolId);
            };
        }

        private Map<String, Object> capabilities() {
            capabilitiesCalls++;
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("schema_version", 1);
            result.put("server_version", serverVersion);
            result.put("server_instance_id", serverInstanceId);
            result.put("target_id", target.targetId());
            result.put("display_name", "Configured target");
            result.put("profile_sha256", PROFILE_SHA);
            result.put("endpoint", Map.of("transport", "test"));
            result.put("jobs", List.of(target.jobName()));
            if ("1.1.0".equals(serverVersion) && !omitJobControls) {
                result.put("job_controls", Map.of(target.jobName(), controls));
            }
            result.put(
                    "tools",
                    "1.1.0".equals(serverVersion)
                            ? OperatorMcpCapabilityProfile.TOOLS
                            : OperatorMcpCapabilityProfile.LEGACY_TOOLS);
            result.put("caller_supplied_commands", false);
            result.put("operator_bootstrap_required", true);
            return result;
        }

        private Map<String, Object> status(Map<String, Object> arguments) {
            statusCalls++;
            assertEquals(target.targetId(), arguments.get("target_id"));
            return Map.of(
                    "schema_version", 1,
                    "server_instance_id", serverInstanceId,
                    "target_id", target.targetId(),
                    "identity", Map.of(
                            "token_user", "configured-user",
                            "desktop", "configured-desktop",
                            "machine", "configured-machine",
                            "process_id", 101),
                    "identity_matches_profile", identityMatches,
                    "process_gates", Map.of(),
                    "process_gate_errors", Map.of(),
                    "jobs", List.of());
        }

        private Map<String, Object> preflight(Map<String, Object> arguments) {
            preflightCalls++;
            assertEquals(target.targetId(), arguments.get("target_id"));
            assertEquals(target.jobName(), arguments.get("job_name"));
            boolean green = !forceRed && !started;
            return Map.ofEntries(
                    Map.entry("schema_version", 1),
                    Map.entry("result", green ? "GREEN" : "RED"),
                    Map.entry("target_id", target.targetId()),
                    Map.entry("job_name", target.jobName()),
                    Map.entry("profile_sha256", PROFILE_SHA),
                    Map.entry("job_spec_sha256", JOB_SHA),
                    Map.entry("checks", Map.of("ready", green)),
                    Map.entry("observations", Map.of()),
                    Map.entry("process_gate_errors", Map.of()),
                    Map.entry("running_job_ids", started ? List.of("job-id-fake") : List.of()),
                    Map.entry("failed_checks", green ? List.of() : List.of("not_ready")),
                    Map.entry("evidence_sha256", EVIDENCE_SHA));
        }

        private Map<String, Object> handoff(Map<String, Object> arguments) {
            handoffCalls++;
            assertEquals(target.targetId(), arguments.get("target_id"));
            assertEquals(target.jobName(), arguments.get("job_name"));
            String requestId = (String) arguments.get("request_id");
            boolean replay = started;
            started = true;
            if (loseFirstHandoffResponse && handoffCalls == 1) {
                throw new SimulatedLostResponse();
            }
            Map<String, Object> job = new LinkedHashMap<>();
            job.put("job_id", "job-id-fake");
            job.put("request_id", requestId);
            job.put("job_name", target.jobName());
            job.put("pid", 202);
            job.put("state", "running");
            job.put("exit_code", null);
            job.put("started_unix", 123.5);
            job.put("stdout_path", "configured/stdout.log");
            job.put("stderr_path", "configured/stderr.log");
            if ("1.1.0".equals(serverVersion)) {
                job.put("available_controls", controls);
            }
            return Map.of(
                    "schema_version", 1,
                    "result", "ACCEPTED",
                    "idempotent_replay", replay,
                    "target_id", target.targetId(),
                    "profile_sha256", PROFILE_SHA,
                    "job", job);
        }

        private Map<String, Object> control(Map<String, Object> arguments) {
            controlCalls++;
            lastControlArguments = Map.copyOf(arguments);
            assertEquals(target.targetId(), arguments.get("target_id"));
            assertEquals(target.jobName(), arguments.get("job_name"));
            assertEquals("job-id-fake", arguments.get("job_id"));
            assertTrue(controls.contains(arguments.get("control_name")));
            boolean replay = controlCalls > 1;
            if (loseFirstControlResponse && controlCalls == 1) {
                throw new SimulatedLostResponse();
            }
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("schema_version", 1);
            response.put("result", controlRed ? "RED" : "ACCEPTED");
            response.put("idempotent_replay", replay);
            response.put("target_id", target.targetId());
            response.put("profile_sha256", PROFILE_SHA);
            response.put("job_id", arguments.get("job_id"));
            response.put("job_name", target.jobName());
            response.put("control_name", arguments.get("control_name"));
            response.put("payload_bytes", 7);
            if (controlRed) {
                response.put("error", "BrokenPipeError: configured failure");
            }
            return response;
        }
    }

    private static final class SimulatedLostResponse extends RuntimeException { }
}
