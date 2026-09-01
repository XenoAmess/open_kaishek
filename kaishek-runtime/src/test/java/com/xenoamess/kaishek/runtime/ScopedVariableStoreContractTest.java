package com.xenoamess.kaishek.runtime;

import com.xenoamess.kaishek.profile.ScopeType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression contracts derived from the CK3 1.19.0.6 phase-two script
 * failures: scoped variables do not leak across owner/subject iterators, and
 * change_variable on an unset value is an explicit RED instead of an implicit
 * zero. These are finite-runtime semantics only, not CK3 live certification.
 */
class ScopedVariableStoreContractTest {
    private static final ScopeRef OWNER = new ScopeRef(ScopeType.CHARACTER, "owner");
    private static final ScopeRef SUBJECT = new ScopeRef(ScopeType.CHARACTER, "subject");

    @Test
    void ownerAndSubjectVariablesAreIsolated() {
        ScopedVariableStore store = new ScopedVariableStore();
        store.set(OWNER, "posted_serial", 0L);
        assertTrue(store.has(OWNER, "posted_serial"));
        assertFalse(store.has(SUBJECT, "posted_serial"));
        assertEquals(1L, store.change(OWNER, "posted_serial", 1L).value());
        assertEquals(1L, store.get(OWNER, "posted_serial").orElseThrow().value());
        assertTrue(store.get(SUBJECT, "posted_serial").isEmpty());
    }

    @Test
    void unsetChangeFailsWithoutCreatingAValue() {
        ScopedVariableStore store = new ScopedVariableStore();
        assertThrows(IllegalStateException.class,
                () -> store.change(SUBJECT, "posted_serial", 1L));
        assertFalse(store.has(SUBJECT, "posted_serial"));
        store.set(SUBJECT, "posted_serial", 0L);
        assertEquals(1L, store.change(SUBJECT, "posted_serial", 1L).value());
    }

    @Test
    void runtimeKernelConvertsUnsetChangeToTypedInvalidResult() {
        var descriptor = new com.xenoamess.kaishek.profile.OpcodeDescriptor(
                "change_variable", "fixture-1", com.xenoamess.kaishek.profile.OpcodeKind.EFFECT,
                com.xenoamess.kaishek.profile.InputType.BLOCK, ScopeType.CHARACTER,
                java.util.List.of("name", "add"),
                com.xenoamess.kaishek.profile.RandomnessClass.DETERMINISTIC,
                true, true, true);
        var kernel = new RuntimeKernel(new com.xenoamess.kaishek.profile.OpcodeRegistry(
                java.util.List.of(descriptor)));
        kernel.register("change_variable", invocation ->
                invocation.context().change("posted_serial", 1L));
        var context = new ExecutionContext(
                new ScopeContext(OWNER, SUBJECT, OWNER), 0, DrawTape.of());
        var result = kernel.execute("change_variable", context,
                java.util.Map.of("name", "posted_serial", "add", 1L));
        assertEquals(ExecutionStatus.INVALID, result.status());
        assertTrue(result.reason().contains("unset"));
        assertFalse(context.has("posted_serial"));
    }

    @Test
    void globalNamespaceIsExplicitAndNumericTypesRemainStrict() {
        ScopedVariableStore store = new ScopedVariableStore();
        store.setGlobal("review_cycle", 2L);
        store.set(OWNER, "review_cycle", 7L);
        assertEquals(2L, store.getGlobal("review_cycle").orElseThrow().value());
        assertEquals(7L, store.get(OWNER, "review_cycle").orElseThrow().value());
        store.set(OWNER, "ratio", new BigDecimal("1.25"));
        assertEquals(new BigDecimal("1.50"),
                store.change(OWNER, "ratio", new BigDecimal("0.25")).value());
        assertThrows(IllegalArgumentException.class,
                () -> store.change(OWNER, "ratio", Double.NaN));
    }

    @Test
    void executionContextFollowsCurrentScopeAndKeepsSnapshotDeterministic() {
        ExecutionContext context = new ExecutionContext(
                new ScopeContext(OWNER, OWNER, null), 0, DrawTape.of());
        context.set("posted_serial", 0L);
        context.scopes(new ScopeContext(OWNER, SUBJECT, OWNER));
        assertFalse(context.has("posted_serial"));
        assertThrows(IllegalStateException.class,
                () -> context.change("posted_serial", 1L));
        context.set("posted_serial", 0L);
        context.change("posted_serial", 1L);
        context.setGlobal("shared_cycle", 4L);
        assertEquals(4L, context.getGlobal("shared_cycle").orElseThrow().value());
        assertEquals(1L, context.variableSnapshot().scopes().get(SUBJECT)
                .get("posted_serial").value());
        assertEquals(4L, context.variableSnapshot().globals().get("shared_cycle").value());
    }
}
