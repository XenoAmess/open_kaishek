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

    private static final class FakeTransport implements OperatorMcpTransport {
        private final OperatorMcpTarget target;
        private final List<String> tools = new ArrayList<>();
        private int capabilitiesCalls;
        private int statusCalls;
        private int preflightCalls;
        private int handoffCalls;
        private boolean identityMatches = true;
        private boolean forceRed;
        private boolean loseFirstHandoffResponse;
        private boolean started;
        private String lastEndpoint;
        private String serverInstanceId = "server-instance-fake";

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
                default -> throw new AssertionError("unexpected tool " + toolId);
            };
        }

        private Map<String, Object> capabilities() {
            capabilitiesCalls++;
            return Map.ofEntries(
                    Map.entry("schema_version", 1),
                    Map.entry("server_version", "1.0.0"),
                    Map.entry("server_instance_id", serverInstanceId),
                    Map.entry("target_id", target.targetId()),
                    Map.entry("display_name", "Configured target"),
                    Map.entry("profile_sha256", PROFILE_SHA),
                    Map.entry("endpoint", Map.of("transport", "test")),
                    Map.entry("jobs", List.of(target.jobName())),
                    Map.entry("tools", OperatorMcpCapabilityProfile.TOOLS),
                    Map.entry("caller_supplied_commands", false),
                    Map.entry("operator_bootstrap_required", true));
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
            return Map.of(
                    "schema_version", 1,
                    "result", "ACCEPTED",
                    "idempotent_replay", replay,
                    "target_id", target.targetId(),
                    "profile_sha256", PROFILE_SHA,
                    "job", job);
        }
    }

    private static final class SimulatedLostResponse extends RuntimeException { }
}
