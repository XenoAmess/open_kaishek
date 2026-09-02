package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CapabilityDescriptorTest {
    @Test
    void descriptorKeepsStaticAndLiveCertificationSeparate() {
        var fields = new ArrayList<>(List.of("source.receipt_id"));
        var descriptor = new CapabilityDescriptor(
                "game.command.query-example-v1", "1.19.0.6", fields,
                List.of("receipt_id_positive"), true, true, false, false);
        fields.add("mutated-after-construction");

        assertEquals(List.of("source.receipt_id"), descriptor.requiredFields());
        assertTrue(descriptor.readOnly());
        assertTrue(descriptor.deterministic());
        assertFalse(descriptor.nativeCertified());
        assertFalse(descriptor.runtimeCertified());
        assertFalse(descriptor.certified());
        assertThrows(UnsupportedOperationException.class,
                () -> descriptor.invariants().add("new"));
    }

    @Test
    void descriptorRejectsAmbiguousContracts() {
        assertThrows(IllegalArgumentException.class,
                () -> new CapabilityDescriptor("bad capability", "1.19.0.6",
                        List.of("field"), List.of("invariant"),
                        true, true, false, false));
        assertThrows(IllegalArgumentException.class,
                () -> new CapabilityDescriptor("valid", "1.19.0.6",
                        List.of("field", "field"), List.of("invariant"),
                        true, true, false, false));
        assertThrows(IllegalArgumentException.class,
                () -> new CapabilityDescriptor("valid", "1.19.0.6",
                        List.of("field"), List.of(),
                        true, true, false, false));
    }
}
