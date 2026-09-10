package com.xenoamess.kaishek.operator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Detached result of the capabilities -> status -> preflight read-only sequence. */
public record OperatorMcpReadiness(
        Map<String, Object> capabilities,
        Map<String, Object> status,
        Map<String, Object> preflight) {
    public OperatorMcpReadiness {
        capabilities = freezeMap(capabilities);
        status = freezeMap(status);
        preflight = freezeMap(preflight);
    }

    public boolean green() {
        return "GREEN".equals(preflight.get("result"));
    }

    public String serverInstanceId() {
        return (String) capabilities.get("server_instance_id");
    }

    public String profileSha256() {
        return (String) capabilities.get("profile_sha256");
    }

    public String serverVersion() {
        return (String) capabilities.get("server_version");
    }

    public List<String> controlsFor(String jobName) {
        return controlsFor(capabilities, jobName);
    }

    static List<String> controlsFor(Map<String, Object> capabilities, String jobName) {
        Object rawJobControls = capabilities.get("job_controls");
        if (rawJobControls == null) {
            return List.of();
        }
        if (!(rawJobControls instanceof Map<?, ?> jobControls)) {
            throw new OperatorMcpContractException("capabilities.job_controls is not an object");
        }
        Object rawControls = jobControls.get(jobName);
        if (!(rawControls instanceof List<?> controls)) {
            throw new OperatorMcpContractException(
                    "capabilities.job_controls does not contain the selected job");
        }
        List<String> result = new ArrayList<>(controls.size());
        for (Object rawControl : controls) {
            if (!(rawControl instanceof String control) || control.isBlank()) {
                throw new OperatorMcpContractException(
                        "capabilities.job_controls contains an invalid control name");
            }
            result.add(control);
        }
        return List.copyOf(result);
    }

    static Map<String, Object> freezeMap(Map<String, Object> source) {
        if (source == null) {
            throw new OperatorMcpContractException("MCP response is null");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (!(entry.getKey() instanceof String key)) {
                throw new OperatorMcpContractException(
                        "MCP response contains a non-string key");
            }
            result.put(key, freeze(entry.getValue()));
        }
        return Collections.unmodifiableMap(result);
    }

    private static Object freeze(Object value) {
        if (value instanceof Map<?, ?> mapping) {
            Map<String, Object> normalized = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : mapping.entrySet()) {
                if (!(entry.getKey() instanceof String key)) {
                    throw new OperatorMcpContractException(
                            "MCP response contains a non-string nested key");
                }
                normalized.put(key, freeze(entry.getValue()));
            }
            return Collections.unmodifiableMap(normalized);
        }
        if (value instanceof List<?> list) {
            List<Object> normalized = new ArrayList<>(list.size());
            for (Object item : list) {
                normalized.add(freeze(item));
            }
            return Collections.unmodifiableList(normalized);
        }
        if (value == null
                || value instanceof String
                || value instanceof Number
                || value instanceof Boolean) {
            return value;
        }
        throw new OperatorMcpContractException(
                "MCP response contains an unsupported value: "
                        + value.getClass().getName());
    }
}
