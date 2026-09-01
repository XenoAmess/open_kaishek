package com.xenoamess.kaishek.runtime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Scope-aware variable storage for the finite runtime.
 *
 * <p>CK3 variables belong to the scope on which they are written; a variable
 * written for one character must not leak into another character reached by an
 * iterator. Global variables are a separate namespace and are addressed by
 * the explicit {@code *Global} methods. This class deliberately models only
 * the small, deterministic set/change/has/get/remove contract. It is not a
 * claim that the finite runtime implements every CK3 variable type.</p>
 */
public final class ScopedVariableStore {
    private final Map<ScopeRef, Map<String, TypedValue>> scoped = new LinkedHashMap<>();
    private final Map<String, TypedValue> globals = new LinkedHashMap<>();

    /** Read a variable in an explicit scope. */
    public synchronized Optional<TypedValue> get(ScopeRef scope, String name) {
        requireScope(scope);
        requireName(name);
        return Optional.ofNullable(scoped.getOrDefault(scope, Map.of()).get(name));
    }

    /** Test a variable in an explicit scope without manufacturing a value. */
    public synchronized boolean has(ScopeRef scope, String name) {
        return get(scope, name).isPresent();
    }

    /** Set (or replace) a variable in an explicit scope. */
    public synchronized void set(ScopeRef scope, String name, TypedValue value) {
        requireScope(scope);
        requireName(name);
        Objects.requireNonNull(value, "value");
        scoped.computeIfAbsent(scope, ignored -> new LinkedHashMap<>()).put(name, value);
    }

    public void set(ScopeRef scope, String name, Object value) {
        set(scope, name, TypedValue.of(value));
    }

    /**
     * Apply a numeric delta to an existing variable.
     *
     * <p>An unset variable is an explicit invalid operation. CK3 1.19.0.6
     * reports this shape for {@code change_variable}; callers that need a
     * first write must use {@link #set(ScopeRef, String, Object)} first. All
     * arithmetic is completed before publishing the new value.</p>
     */
    public synchronized TypedValue change(ScopeRef scope, String name, Number delta) {
        requireScope(scope);
        requireName(name);
        Objects.requireNonNull(delta, "delta");
        TypedValue old = scoped.getOrDefault(scope, Map.of()).get(name);
        if (old == null) {
            throw new IllegalStateException("cannot change unset variable: " + name);
        }
        TypedValue next = add(old, delta, name);
        scoped.get(scope).put(name, next);
        return next;
    }

    /** Remove a variable from an explicit scope, if present. */
    public synchronized Optional<TypedValue> remove(ScopeRef scope, String name) {
        requireScope(scope);
        requireName(name);
        Map<String, TypedValue> values = scoped.get(scope);
        if (values == null) return Optional.empty();
        TypedValue removed = values.remove(name);
        if (values.isEmpty()) scoped.remove(scope);
        return Optional.ofNullable(removed);
    }

    /** Return a stable copy of variables in one scope. */
    public synchronized Map<String, TypedValue> variables(ScopeRef scope) {
        requireScope(scope);
        return immutableCopy(scoped.getOrDefault(scope, Map.of()));
    }

    public synchronized Optional<TypedValue> getGlobal(String name) {
        requireName(name);
        return Optional.ofNullable(globals.get(name));
    }

    public synchronized boolean hasGlobal(String name) {
        return getGlobal(name).isPresent();
    }

    public synchronized void setGlobal(String name, TypedValue value) {
        requireName(name);
        globals.put(name, Objects.requireNonNull(value, "value"));
    }

    public void setGlobal(String name, Object value) {
        setGlobal(name, TypedValue.of(value));
    }

    /** Global counterpart of {@link #change(ScopeRef, String, Number)}. */
    public synchronized TypedValue changeGlobal(String name, Number delta) {
        requireName(name);
        Objects.requireNonNull(delta, "delta");
        TypedValue old = globals.get(name);
        if (old == null) {
            throw new IllegalStateException("cannot change unset global variable: " + name);
        }
        TypedValue next = add(old, delta, name);
        globals.put(name, next);
        return next;
    }

    public synchronized Optional<TypedValue> removeGlobal(String name) {
        requireName(name);
        return Optional.ofNullable(globals.remove(name));
    }

    public synchronized Map<String, TypedValue> globals() {
        return immutableCopy(globals);
    }

    /** Immutable deterministic snapshot useful to replay/fixture adapters. */
    public synchronized Snapshot snapshot() {
        Map<ScopeRef, Map<String, TypedValue>> scopes = new LinkedHashMap<>();
        scoped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(java.util.Comparator
                        .comparing((ScopeRef ref) -> ref.type().name())
                        .thenComparing(ScopeRef::id)))
                .forEach(entry -> scopes.put(entry.getKey(), immutableCopy(entry.getValue())));
        return new Snapshot(scopes, immutableCopy(globals));
    }

    public record Snapshot(Map<ScopeRef, Map<String, TypedValue>> scopes,
                           Map<String, TypedValue> globals) {
        public Snapshot {
            scopes = immutableNestedCopy(scopes);
            globals = immutableCopy(globals);
        }

        private static Map<ScopeRef, Map<String, TypedValue>> immutableNestedCopy(
                Map<ScopeRef, Map<String, TypedValue>> input) {
            Objects.requireNonNull(input, "scopes");
            Map<ScopeRef, Map<String, TypedValue>> copy = new LinkedHashMap<>();
            input.forEach((scope, values) -> {
                requireScope(scope);
                copy.put(scope, immutableCopy(values));
            });
            return Collections.unmodifiableMap(copy);
        }
    }

    private static TypedValue add(TypedValue old, Number delta, String name) {
        if (!(old.value() instanceof Number oldNumber)) {
            throw new IllegalArgumentException("cannot change non-numeric variable: " + name);
        }
        BigDecimal left = decimal(oldNumber, name);
        BigDecimal right = decimal(delta, name);
        BigDecimal sum = left.add(right);
        if (old.type() == TypedValue.Type.INTEGER && isIntegral(sum)) {
            try {
                return TypedValue.of(sum.longValueExact());
            } catch (ArithmeticException ex) {
                throw new ArithmeticException("integer overflow changing variable: " + name);
            }
        }
        return new TypedValue(TypedValue.Type.DECIMAL, sum);
    }

    private static BigDecimal decimal(Number value, String name) {
        if (value instanceof Double d && !Double.isFinite(d)
                || value instanceof Float f && !Float.isFinite(f)) {
            throw new IllegalArgumentException("non-finite numeric delta/value: " + name);
        }
        try {
            return value instanceof BigDecimal bd ? bd : new BigDecimal(value.toString());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid numeric value: " + name, ex);
        }
    }

    private static boolean isIntegral(BigDecimal value) {
        return value.stripTrailingZeros().scale() <= 0;
    }

    private static Map<String, TypedValue> immutableCopy(Map<String, TypedValue> input) {
        Objects.requireNonNull(input, "values");
        List<String> names = new ArrayList<>(input.keySet());
        if (names.stream().anyMatch(name -> name == null || name.isBlank()))
            throw new IllegalArgumentException("variable name is blank");
        names.sort(String::compareTo);
        Map<String, TypedValue> copy = new LinkedHashMap<>();
        for (String name : names) copy.put(name, Objects.requireNonNull(input.get(name), "value"));
        return Collections.unmodifiableMap(copy);
    }

    private static void requireScope(ScopeRef scope) {
        Objects.requireNonNull(scope, "scope");
    }

    private static void requireName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("variable name is blank");
    }
}
