package com.xenoamess.kaishek.operator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Small transport-neutral client for one configured target/job pair.
 *
 * <p>Readiness performs the documented read-only capabilities/status/preflight sequence.
 * First handoff calls readiness and then submits only target, job, and caller-owned request
 * id. If a response is lost, retrying the same request id bypasses a now-RED preflight and
 * lets the target-side server return its idempotent replay. A completed response is cached
 * per adapter instance and returned without another transport call.</p>
 */
public final class OperatorMcpClientAdapter {
    private final OperatorMcpTarget target;
    private final OperatorMcpTransport transport;
    private final Map<String, Map<String, Object>> accepted = new LinkedHashMap<>();
    private final Map<String, String> attemptedServerInstances = new LinkedHashMap<>();

    public OperatorMcpClientAdapter(
            OperatorMcpTarget target,
            OperatorMcpTransport transport) {
        this.target = Objects.requireNonNull(target, "target");
        this.transport = Objects.requireNonNull(transport, "transport");
    }

    public OperatorMcpTarget target() {
        return target;
    }

    public Map<String, Object> getCapabilities() {
        Map<String, Object> response = call(
                OperatorMcpCapabilityProfile.GET_CAPABILITIES_TOOL,
                Map.of());
        requireSchema(response);
        requireEquals(response, "server_version", target.expectedServerVersion());
        requireEquals(response, "target_id", target.targetId());
        requireEquals(response, "caller_supplied_commands", false);
        requireEquals(response, "operator_bootstrap_required", true);
        Object tools = response.get("tools");
        if (!(tools instanceof Collection<?> collection)
                || !collection.containsAll(OperatorMcpCapabilityProfile.TOOLS)) {
            throw contract("capabilities.tools does not contain the four contract tools");
        }
        Object jobs = response.get("jobs");
        if (!(jobs instanceof Collection<?> jobsCollection)
                || !jobsCollection.contains(target.jobName())) {
            throw contract("capabilities.jobs does not contain the selected job");
        }
        requireString(response, "server_instance_id");
        requireSha256(response, "profile_sha256");
        return response;
    }

    public Map<String, Object> getStatus() {
        Map<String, Object> response = call(
                OperatorMcpCapabilityProfile.GET_STATUS_TOOL,
                Map.of("target_id", target.targetId()));
        requireSchema(response);
        requireEquals(response, "target_id", target.targetId());
        requireString(response, "server_instance_id");
        requireBoolean(response, "identity_matches_profile");
        return response;
    }

    public Map<String, Object> preflightJob() {
        Map<String, Object> response = call(
                OperatorMcpCapabilityProfile.PREFLIGHT_JOB_TOOL,
                Map.of("target_id", target.targetId(), "job_name", target.jobName()));
        requireSchema(response);
        requireEquals(response, "target_id", target.targetId());
        requireEquals(response, "job_name", target.jobName());
        requireOneOf(response, "result", "GREEN", "RED");
        requireSha256(response, "profile_sha256");
        requireSha256(response, "job_spec_sha256");
        requireSha256(response, "evidence_sha256");
        return response;
    }

    public OperatorMcpReadiness inspectReadiness() {
        Map<String, Object> capabilities = getCapabilities();
        Map<String, Object> status = getStatus();
        Map<String, Object> preflight = preflightJob();
        requireEquals(
                status,
                "server_instance_id",
                requireString(capabilities, "server_instance_id"));
        requireEquals(
                preflight,
                "profile_sha256",
                requireString(capabilities, "profile_sha256"));
        if (!Boolean.TRUE.equals(status.get("identity_matches_profile"))) {
            throw contract("status identity does not match the target-side profile");
        }
        return new OperatorMcpReadiness(capabilities, status, preflight);
    }

    /**
     * Run the read-only readiness sequence for a first request, or retry an already attempted
     * request directly so the server's per-instance idempotency remains reachable.
     */
    public synchronized Map<String, Object> handoffJob(String requestId) {
        String checkedRequestId = OperatorMcpTarget.requireIdentifier(
                requestId, "requestId");
        Map<String, Object> prior = accepted.get(checkedRequestId);
        if (prior != null) {
            return prior;
        }
        String attemptedServerInstance = attemptedServerInstances.get(checkedRequestId);
        if (attemptedServerInstance != null) {
            return retryHandoffJob(checkedRequestId, attemptedServerInstance);
        }
        OperatorMcpReadiness readiness = inspectReadiness();
        if (!readiness.green()) {
            throw contract("operator preflight is RED");
        }
        attemptedServerInstances.put(checkedRequestId, readiness.serverInstanceId());
        return invokeHandoff(checkedRequestId, readiness.capabilities());
    }

    /**
     * Retry a request after an ambiguous/lost response, including from a new client instance.
     * Only capabilities are reread; the server resolves the request id before its live gate.
     */
    public synchronized Map<String, Object> retryHandoffJob(
            String requestId,
            String expectedServerInstanceId) {
        String checkedRequestId = OperatorMcpTarget.requireIdentifier(
                requestId, "requestId");
        String checkedServerInstanceId = OperatorMcpTarget.requireIdentifier(
                expectedServerInstanceId, "expectedServerInstanceId");
        Map<String, Object> prior = accepted.get(checkedRequestId);
        if (prior != null) {
            return prior;
        }
        Map<String, Object> capabilities = getCapabilities();
        requireEquals(
                capabilities,
                "server_instance_id",
                checkedServerInstanceId);
        attemptedServerInstances.put(checkedRequestId, checkedServerInstanceId);
        return invokeHandoff(checkedRequestId, capabilities);
    }

    private Map<String, Object> invokeHandoff(
            String requestId,
            Map<String, Object> capabilities) {
        Map<String, Object> response = call(
                OperatorMcpCapabilityProfile.HANDOFF_JOB_TOOL,
                Map.of(
                        "target_id", target.targetId(),
                        "job_name", target.jobName(),
                        "request_id", requestId));
        requireSchema(response);
        requireEquals(response, "result", "ACCEPTED");
        requireEquals(response, "target_id", target.targetId());
        requireEquals(
                response,
                "profile_sha256",
                requireString(capabilities, "profile_sha256"));
        requireBoolean(response, "idempotent_replay");
        Object rawJob = response.get("job");
        if (!(rawJob instanceof Map<?, ?> job)) {
            throw contract("handoff.job is not an object");
        }
        requireEquals(job, "request_id", requestId);
        requireEquals(job, "job_name", target.jobName());
        requireString(job, "job_id");
        Map<String, Object> frozen = OperatorMcpReadiness.freezeMap(response);
        accepted.put(requestId, frozen);
        return frozen;
    }

    private Map<String, Object> call(String tool, Map<String, Object> arguments) {
        Map<String, Object> response = transport.call(target.endpoint(), tool, arguments);
        return OperatorMcpReadiness.freezeMap(response);
    }

    private static void requireSchema(Map<?, ?> response) {
        Object version = response.get("schema_version");
        if (!(version instanceof Number number)
                || number.intValue() != OperatorMcpCapabilityProfile.PROFILE_SCHEMA_VERSION) {
            throw contract("response schema_version is not 1");
        }
    }

    private static String requireString(Map<?, ?> response, String field) {
        Object value = response.get(field);
        if (!(value instanceof String text) || text.isBlank()) {
            throw contract(field + " is not a non-empty string");
        }
        return text;
    }

    private static void requireSha256(Map<?, ?> response, String field) {
        String value = requireString(response, field);
        if (!value.matches("[0-9A-F]{64}")) {
            throw contract(field + " is not uppercase SHA-256");
        }
    }

    private static void requireBoolean(Map<?, ?> response, String field) {
        if (!(response.get(field) instanceof Boolean)) {
            throw contract(field + " is not boolean");
        }
    }

    private static void requireEquals(Map<?, ?> response, String field, Object expected) {
        if (!Objects.equals(response.get(field), expected)) {
            throw contract(field + " does not match the selected target contract");
        }
    }

    private static void requireOneOf(
            Map<?, ?> response,
            String field,
            Object first,
            Object second) {
        Object value = response.get(field);
        if (!Objects.equals(value, first) && !Objects.equals(value, second)) {
            throw contract(field + " is outside the supported contract");
        }
    }

    private static OperatorMcpContractException contract(String message) {
        return new OperatorMcpContractException(message);
    }
}
