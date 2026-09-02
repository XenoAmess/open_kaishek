package com.xenoamess.kaishek.profile;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Immutable declaration for one externally visible, bounded capability.
 *
 * <p>This is intentionally separate from {@link OpcodeDescriptor}: a
 * capability may correlate several script variables and a native query
 * without inventing a new Paradox opcode.  Static recognition and fixture
 * coverage are recorded independently from native/runtime certification.</p>
 */
public record CapabilityDescriptor(
        String id,
        String profileVersion,
        List<String> requiredFields,
        List<String> invariants,
        boolean readOnly,
        boolean deterministic,
        boolean nativeCertified,
        boolean runtimeCertified) {

    public CapabilityDescriptor {
        if (id == null || id.isBlank() || !id.matches("[A-Za-z0-9_.:-]+")) {
            throw new IllegalArgumentException("invalid capability id");
        }
        if (profileVersion == null || profileVersion.isBlank()) {
            throw new IllegalArgumentException("profileVersion is blank");
        }
        requiredFields = immutableNonBlank(requiredFields, "requiredFields");
        invariants = immutableNonBlank(invariants, "invariants");
        if (requiredFields.isEmpty()) {
            throw new IllegalArgumentException("requiredFields is empty");
        }
        if (invariants.isEmpty()) {
            throw new IllegalArgumentException("invariants is empty");
        }
    }

    /** A capability is fully certified only when both native and runtime are certified. */
    public boolean certified() {
        return nativeCertified && runtimeCertified;
    }

    private static List<String> immutableNonBlank(List<String> values, String name) {
        List<String> copy = List.copyOf(Objects.requireNonNull(values, name));
        if (copy.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalArgumentException(name + " contains blank value");
        }
        if (new HashSet<>(copy).size() != copy.size()) {
            throw new IllegalArgumentException(name + " contains duplicate value");
        }
        return copy;
    }
}
