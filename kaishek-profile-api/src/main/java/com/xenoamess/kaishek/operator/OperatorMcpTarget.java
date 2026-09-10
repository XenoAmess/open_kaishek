package com.xenoamess.kaishek.operator;

import java.util.Objects;
import java.util.regex.Pattern;

/** Client-side deployment selection; all values are supplied by the caller. */
public record OperatorMcpTarget(
        String endpoint,
        String targetId,
        String jobName,
        String expectedServerVersion) {
    private static final Pattern IDENTIFIER =
            Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._-]{0,127}$");

    public OperatorMcpTarget {
        endpoint = requireNonBlank(endpoint, "endpoint");
        targetId = requireIdentifier(targetId, "targetId");
        jobName = requireIdentifier(jobName, "jobName");
        expectedServerVersion = requireNonBlank(
                expectedServerVersion, "expectedServerVersion");
    }

    /** Select the compatible v1 baseline; the client also accepts its additive 1.1 upgrade. */
    public static OperatorMcpTarget contractV1(
            String endpoint, String targetId, String jobName) {
        return new OperatorMcpTarget(
                endpoint,
                targetId,
                jobName,
                OperatorMcpCapabilityProfile.LEGACY_CONTRACT_VERSION);
    }

    /** Require the 1.1 control-capable server contract. */
    public static OperatorMcpTarget contractV11(
            String endpoint, String targetId, String jobName) {
        return new OperatorMcpTarget(
                endpoint,
                targetId,
                jobName,
                OperatorMcpCapabilityProfile.CONTRACT_VERSION);
    }

    static String requireIdentifier(String value, String name) {
        String checked = requireNonBlank(value, name);
        if (!IDENTIFIER.matcher(checked).matches()) {
            throw new IllegalArgumentException(name + " must be a portable identifier");
        }
        return checked;
    }

    private static String requireNonBlank(String value, String name) {
        String checked = Objects.requireNonNull(value, name);
        if (checked.isBlank()) {
            throw new IllegalArgumentException(name + " is blank");
        }
        return checked;
    }
}
