package com.xenoamess.kaishek.runtime;

import java.time.LocalDate;
import java.util.*;

/** Mutable execution frame; all mutation is observable through trace/read-write sets. */
public final class ExecutionContext {
    private ScopeContext scopes;
    private final ScopedVariableStore variables = new ScopedVariableStore();
    private final DrawTape drawTape;
    private final ExecutionTrace trace;
    private final PriorityQueue<EventEnvelope> events = new PriorityQueue<>(Comparator.comparingLong(EventEnvelope::dueEpochDay).thenComparingLong(EventEnvelope::sequence));
    private long epochDay;
    private long eventSequence;
    public ExecutionContext(ScopeContext scopes, long epochDay, DrawTape drawTape) {
        if (epochDay < 0) throw new IllegalArgumentException("negative epoch day"); this.scopes = Objects.requireNonNull(scopes); this.epochDay = epochDay; this.drawTape = Objects.requireNonNull(drawTape); this.trace = new ExecutionTrace();
    }
    public ScopeContext scopes() { return scopes; }
    public void scopes(ScopeContext scopes) { this.scopes = Objects.requireNonNull(scopes); }
    public long epochDay() { return epochDay; }
    public LocalDate date() { return LocalDate.ofEpochDay(epochDay); }
    public void advanceTo(long day) { if (day < epochDay) throw new IllegalArgumentException("clock cannot go backwards"); epochDay = day; trace.add("clock", "advance", Map.of("epochDay", day)); }
    public DrawTape drawTape() { return drawTape; }
    public ExecutionTrace trace() { return trace; }
    /** Read a variable on the current THIS scope. */
    public Optional<TypedValue> get(String key) {
        trace.read(key);
        return variables.get(scopes.current(), key);
    }
    /** Set a variable on the current THIS scope. */
    public void set(String key, TypedValue value) {
        variables.set(scopes.current(), key, value);
        trace.write(key);
        trace.add("set", "write", Map.of("key", key));
    }
    public void set(String key, Object value) { set(key, TypedValue.of(value)); }
    /** Apply a delta to an existing current-scope numeric variable. */
    public TypedValue change(String key, Number delta) {
        trace.read(key);
        TypedValue next = variables.change(scopes.current(), key, delta);
        trace.write(key);
        trace.add("change", "write", Map.of("key", key, "delta", delta));
        return next;
    }
    public boolean has(String key) {
        trace.read(key);
        return variables.has(scopes.current(), key);
    }
    public Optional<TypedValue> remove(String key) {
        trace.write(key);
        return variables.remove(scopes.current(), key);
    }
    /** Explicit scoped access for adapters traversing ROOT/PREV/iterator scopes. */
    public Optional<TypedValue> get(ScopeRef scope, String key) {
        trace.read(key);
        return variables.get(scope, key);
    }
    public void set(ScopeRef scope, String key, TypedValue value) {
        variables.set(scope, key, value);
        trace.write(key);
    }
    public TypedValue change(ScopeRef scope, String key, Number delta) {
        trace.read(key);
        TypedValue next = variables.change(scope, key, delta);
        trace.write(key);
        return next;
    }
    public boolean has(ScopeRef scope, String key) {
        trace.read(key);
        return variables.has(scope, key);
    }
    public Optional<TypedValue> remove(ScopeRef scope, String key) {
        trace.write(key);
        return variables.remove(scope, key);
    }
    public Optional<TypedValue> getGlobal(String key) {
        trace.read("global:" + key);
        return variables.getGlobal(key);
    }
    public void setGlobal(String key, TypedValue value) {
        variables.setGlobal(key, value);
        trace.write("global:" + key);
    }
    public void setGlobal(String key, Object value) { setGlobal(key, TypedValue.of(value)); }
    public TypedValue changeGlobal(String key, Number delta) {
        trace.read("global:" + key);
        TypedValue next = variables.changeGlobal(key, delta);
        trace.write("global:" + key);
        return next;
    }
    public boolean hasGlobal(String key) {
        trace.read("global:" + key);
        return variables.hasGlobal(key);
    }
    public Optional<TypedValue> removeGlobal(String key) {
        trace.write("global:" + key);
        return variables.removeGlobal(key);
    }
    public Map<String, TypedValue> variables() { return variables.variables(scopes.current()); }
    public Map<String, TypedValue> variables(ScopeRef scope) { return variables.variables(scope); }
    public Map<String, TypedValue> globalVariables() { return variables.globals(); }
    public ScopedVariableStore.Snapshot variableSnapshot() { return variables.snapshot(); }
    public EventEnvelope schedule(String hook, long dueDay, CaseBinding binding, Map<String,Object> payload) {
        if (dueDay < epochDay) throw new IllegalArgumentException("event due date in past"); EventEnvelope e = new EventEnvelope(eventSequence++, hook, dueDay, binding, payload); events.add(e); trace.add(hook, "schedule", Map.of("dueEpochDay", dueDay, "sequence", e.sequence())); return e;
    }
    public List<EventEnvelope> dueEvents() { List<EventEnvelope> out = new ArrayList<>(); while (!events.isEmpty() && events.peek().dueEpochDay() <= epochDay) out.add(events.remove()); return out; }
    public int queuedEvents() { return events.size(); }
}
