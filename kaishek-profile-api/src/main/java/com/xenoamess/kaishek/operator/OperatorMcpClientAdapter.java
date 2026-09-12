package com.xenoamess.kaishek.operator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
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
    private final Map<String, Map<String, Object>> controlResponses = new LinkedHashMap<>();
    private final Map<String, ControlAttempt> controlAttempts = new LinkedHashMap<>();

    private record ControlAttempt(
            String jobId,
            String controlName,
            String serverInstanceId) { }

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
        String serverVersion = requireString(response, "server_version");
        if (!OperatorMcpCapabilityProfile.isSupportedServerVersion(serverVersion)
                || !OperatorMcpCapabilityProfile.isCompatibleUpgrade(
                target.expectedServerVersion(), serverVersion)) {
            throw contract("server_version does not match a compatible target contract");
        }
        requireEquals(response, "target_id", target.targetId());
        requireEquals(response, "caller_supplied_commands", false);
        requireEquals(response, "operator_bootstrap_required", true);
        Object tools = response.get("tools");
        if (!(tools instanceof Collection<?> collection)
                || !collection.containsAll(OperatorMcpCapabilityProfile.LEGACY_TOOLS)) {
            throw contract("capabilities.tools does not contain the four baseline tools");
        }
        if (OperatorMcpCapabilityProfile.CONTRACT_VERSION.equals(serverVersion)) {
            if (!collection.contains(OperatorMcpCapabilityProfile.CONTROL_JOB_TOOL)) {
                throw contract("1.1 capabilities.tools does not contain operator_control_job");
            }
            if (!response.containsKey("job_controls")) {
                throw contract("1.1 capabilities does not contain job_controls");
            }
            OperatorMcpReadiness.controlsFor(response, target.jobName());
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

    /** Discover the selected job's profile-frozen control names, or an empty list on 1.0. */
    public List<String> discoverControls() {
        return OperatorMcpReadiness.controlsFor(getCapabilities(), target.jobName());
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

    /**
     * Send a profile-frozen control. The caller supplies only identifiers, never stdin bytes.
     * An ambiguous retry with the same request id remains bound to the original job/control.
     */
    public synchronized Map<String, Object> controlJob(
            String jobId,
            String controlName,
            String requestId) {
        String checkedJobId = OperatorMcpTarget.requireIdentifier(jobId, "jobId");
        String checkedControlName = OperatorMcpTarget.requireIdentifier(
                controlName, "controlName");
        String checkedRequestId = OperatorMcpTarget.requireIdentifier(
                requestId, "requestId");
        Map<String, Object> prior = controlResponses.get(checkedRequestId);
        if (prior != null) {
            requireControlBinding(checkedRequestId, checkedJobId, checkedControlName);
            return prior;
        }
        ControlAttempt attempt = controlAttempts.get(checkedRequestId);
        if (attempt != null) {
            requireControlBinding(checkedRequestId, checkedJobId, checkedControlName);
            return retryControlJob(
                    checkedJobId,
                    checkedControlName,
                    checkedRequestId,
                    attempt.serverInstanceId());
        }
        Map<String, Object> capabilities = getCapabilities();
        requireAdvertisedControl(capabilities, checkedControlName);
        controlAttempts.put(
                checkedRequestId,
                new ControlAttempt(
                        checkedJobId,
                        checkedControlName,
                        requireString(capabilities, "server_instance_id")));
        return invokeControl(
                checkedJobId, checkedControlName, checkedRequestId, capabilities);
    }

    public synchronized Map<String, Object> retryControlJob(
            String jobId,
            String controlName,
            String requestId,
            String expectedServerInstanceId) {
        String checkedJobId = OperatorMcpTarget.requireIdentifier(jobId, "jobId");
        String checkedControlName = OperatorMcpTarget.requireIdentifier(
                controlName, "controlName");
        String checkedRequestId = OperatorMcpTarget.requireIdentifier(
                requestId, "requestId");
        String checkedServerInstanceId = OperatorMcpTarget.requireIdentifier(
                expectedServerInstanceId, "expectedServerInstanceId");
        Map<String, Object> prior = controlResponses.get(checkedRequestId);
        if (prior != null) {
            requireControlBinding(checkedRequestId, checkedJobId, checkedControlName);
            return prior;
        }
        ControlAttempt attempt = controlAttempts.get(checkedRequestId);
        if (attempt != null) {
            requireControlBinding(checkedRequestId, checkedJobId, checkedControlName);
            if (!attempt.serverInstanceId().equals(checkedServerInstanceId)) {
                throw contract("control request is bound to a different server instance");
            }
        }
        Map<String, Object> capabilities = getCapabilities();
        requireEquals(capabilities, "server_instance_id", checkedServerInstanceId);
        requireAdvertisedControl(capabilities, checkedControlName);
        controlAttempts.put(
                checkedRequestId,
                new ControlAttempt(
                        checkedJobId, checkedControlName, checkedServerInstanceId));
        return invokeControl(
                checkedJobId, checkedControlName, checkedRequestId, capabilities);
    }

    private Map<String, Object> invokeControl(
            String jobId,
            String controlName,
            String requestId,
            Map<String, Object> capabilities) {
        Map<String, Object> response = call(
                OperatorMcpCapabilityProfile.CONTROL_JOB_TOOL,
                Map.of(
                        "target_id", target.targetId(),
                        "job_name", target.jobName(),
                        "job_id", jobId,
                        "control_name", controlName,
                        "request_id", requestId));
        requireSchema(response);
        requireOneOf(response, "result", "ACCEPTED", "RED");
        requireEquals(response, "target_id", target.targetId());
        requireEquals(response, "job_name", target.jobName());
        requireEquals(response, "job_id", jobId);
        requireEquals(response, "control_name", controlName);
        requireEquals(
                response,
                "profile_sha256",
                requireString(capabilities, "profile_sha256"));
        requireBoolean(response, "idempotent_replay");
        requireNonNegativeInteger(response, "payload_bytes");
        if ("RED".equals(response.get("result"))) {
            requireString(response, "error");
        }
        Map<String, Object> frozen = OperatorMcpReadiness.freezeMap(response);
        controlResponses.put(requestId, frozen);
        return frozen;
    }

    private void requireAdvertisedControl(
            Map<String, Object> capabilities,
            String controlName) {
        if (!OperatorMcpCapabilityProfile.CONTRACT_VERSION.equals(
                capabilities.get("server_version"))) {
            throw contract("operator controls are unavailable on contract 1.0.0");
        }
        if (!OperatorMcpReadiness.controlsFor(capabilities, target.jobName())
                .contains(controlName)) {
            throw contract("control is not advertised for the selected job");
        }
    }

    private void requireControlBinding(
            String requestId,
            String jobId,
            String controlName) {
        ControlAttempt attempt = controlAttempts.get(requestId);
        if (attempt == null
                || !attempt.jobId().equals(jobId)
                || !attempt.controlName().equals(controlName)) {
            throw contract("control request id is already bound to another job/control");
        }
    }

    private Map<String, Object> call(String tool, Map<String, Object> arguments) {
        Map<String, Object> response = transport.call(target.endpoint(), tool, arguments);
        return OperatorMcpReadiness.freezeMap(response);
    }

    private static void requireSchema(Map<?, ?> response) {
        BigInteger version = requireInteger(response, "schema_version");
        if (!version.equals(BigInteger.valueOf(
                OperatorMcpCapabilityProfile.PROFILE_SCHEMA_VERSION))) {
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

    private static void requireNonNegativeInteger(Map<?, ?> response, String field) {
        if (requireInteger(response, field).signum() < 0) {
            throw contract(field + " is not a non-negative integer");
        }
    }

    private static BigInteger requireInteger(Map<?, ?> response, String field) {
        Object value = response.get(field);
        if (!(value instanceof Number number)) {
            throw contract(field + " is not an integer");
        }
        try {
            return new BigDecimal(number.toString()).toBigIntegerExact();
        } catch (ArithmeticException | NumberFormatException exception) {
            throw contract(field + " is not an integer");
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
