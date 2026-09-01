package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.OpcodeRegistry;
import com.xenoamess.kaishek.runtime.CaseBinding;
import com.xenoamess.kaishek.runtime.CaseRegistry;
import com.xenoamess.kaishek.runtime.DeadlineTicket;
import com.xenoamess.kaishek.runtime.ExecutionContext;
import com.xenoamess.kaishek.runtime.ExecutionResult;
import com.xenoamess.kaishek.runtime.ExecutionStatus;
import com.xenoamess.kaishek.runtime.ExecutionTrace;
import com.xenoamess.kaishek.runtime.HookDispatcher;
import com.xenoamess.kaishek.runtime.Receipt;
import com.xenoamess.kaishek.runtime.ReceiptJournal;
import com.xenoamess.kaishek.runtime.RuntimeKernel;
import com.xenoamess.kaishek.runtime.ScopeContext;
import com.xenoamess.kaishek.runtime.ScopeRef;
import com.xenoamess.kaishek.runtime.DrawTape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Offline replay of the smallest useful part of the 361/014 appeal contract.
 *
 * <p>This class intentionally models a finite case, rather than pretending to
 * be a CK3 scheduler.  A case is opened with three already-defined penalty
 * receipts, an appeal schedules one deterministic deadline event, and a
 * matching reviewer may refund each receipt exactly once before the deadline.
 * Reopened/revised bindings and consumed deadlines are explicit no-ops.  The
 * class uses the shared runtime primitives so the replay exercises the same
 * scope, queue, stale-ticket, and receipt boundaries as other fixtures.</p>
 *
 * <p>The source contract is the companion mod's 014 acceptance entry and its
 * existing receipted regrade effect.  Those files are provenance only; this
 * package contains no copied CK3 script and never starts CK3.</p>
 */
public final class Appeal014Replay {
    /** Hidden hook name used only by this finite fixture. */
    public static final String EXPIRY_HOOK = "zg361.synthetic.014.appeal.expire";

    /** The three receipt kinds named by the 014/018 companion contracts. */
    public static final String TREASURY_RECEIPT = "treasury_penalty";
    public static final String GOLD_RECEIPT = "gold_penalty";
    public static final String MERIT_RECEIPT = "merit_penalty";

    /** Finite states represented by this slice; other 361 states are out of scope. */
    public enum State { CREATED, DELIVERED, APPEAL_OPEN, RESOLVED, EXPIRED }

    /** Normalized operation outcomes used by the differential fixture. */
    public enum Outcome {
        APPLIED,
        IDEMPOTENT_NOOP,
        STALE_NOOP,
        STALE_DEADLINE_NOOP,
        INVALID
    }

    /** Account/resource key kept separate from ReceiptJournal's internal string key. */
    public record BalanceKey(String account, String resource) {
        public BalanceKey {
            requireNonBlank(account, "account");
            requireNonBlank(resource, "resource");
        }

        String journalKey() {
            return account + "\u0000" + resource;
        }

        String path() {
            return account + "/" + resource;
        }
    }

    /** One immutable, pre-existing penalty receipt to be refunded on success. */
    public record ReceiptSpec(String key, String account, String resource, long amount) {
        public ReceiptSpec {
            requireNonBlank(key, "receipt key");
            requireNonBlank(account, "receipt account");
            requireNonBlank(resource, "receipt resource");
            if (amount <= 0) throw new IllegalArgumentException("receipt amount must be positive");
        }

        BalanceKey balanceKey() {
            return new BalanceKey(account, resource);
        }
    }

    /** Immutable input for one deterministic replay. */
    public record Scenario(
            String id,
            String caseId,
            ScopeRef owner,
            ScopeRef subject,
            ScopeRef reviewer,
            long startEpochDay,
            long deadlineDays,
            List<ReceiptSpec> receipts,
            Map<BalanceKey, Long> initialBalances) {
        public Scenario {
            requireNonBlank(id, "scenario id");
            requireNonBlank(caseId, "case id");
            Objects.requireNonNull(owner, "owner");
            Objects.requireNonNull(subject, "subject");
            Objects.requireNonNull(reviewer, "reviewer");
            if (startEpochDay < 0) throw new IllegalArgumentException("start day cannot be negative");
            if (deadlineDays < 1) throw new IllegalArgumentException("deadline must be positive");
            if (deadlineDays > 3650) throw new IllegalArgumentException("deadline exceeds finite fixture bound");
            receipts = List.copyOf(Objects.requireNonNull(receipts, "receipts"));
            if (receipts.size() != 3) {
                throw new IllegalArgumentException("014 replay requires exactly three receipt specs");
            }
            long distinct = receipts.stream().map(ReceiptSpec::key).distinct().count();
            if (distinct != receipts.size()) throw new IllegalArgumentException("receipt keys must be unique");
            if (receipts.stream().map(ReceiptSpec::key).noneMatch(TREASURY_RECEIPT::equals)
                    || receipts.stream().map(ReceiptSpec::key).noneMatch(GOLD_RECEIPT::equals)
                    || receipts.stream().map(ReceiptSpec::key).noneMatch(MERIT_RECEIPT::equals)) {
                throw new IllegalArgumentException("014 replay must bind treasury, gold, and merit receipts");
            }
            initialBalances = immutableBalances(initialBalances);
        }

        private static Map<BalanceKey, Long> immutableBalances(Map<BalanceKey, Long> values) {
            Objects.requireNonNull(values, "initialBalances");
            Map<BalanceKey, Long> copy = new LinkedHashMap<>();
            for (var entry : values.entrySet()) {
                Objects.requireNonNull(entry.getKey(), "initial balance key");
                Long amount = Objects.requireNonNull(entry.getValue(), "initial balance amount");
                if (amount < 0) throw new IllegalArgumentException("initial balance cannot be negative");
                copy.put(entry.getKey(), amount);
            }
            return Collections.unmodifiableMap(copy);
        }
    }

    /** Canonical finite state exposed to a differential comparator. */
    public record Snapshot(
            String scenarioId,
            State state,
            long currentEpochDay,
            long boundSerial,
            long boundRevision,
            long registrySerial,
            long registryRevision,
            long deadlineEpochDay,
            int queuedEvents,
            int staleDeadlineCount,
            Map<String, Long> balances,
            Map<String, Long> refunded) {
        public Snapshot {
            requireNonBlank(scenarioId, "scenario id");
            Objects.requireNonNull(state, "state");
            if (currentEpochDay < 0) throw new IllegalArgumentException("current day cannot be negative");
            if (boundSerial < -1 || boundRevision < -1 || registrySerial < -1 || registryRevision < -1) {
                throw new IllegalArgumentException("invalid binding sentinel");
            }
            if (deadlineEpochDay < -1 || queuedEvents < 0 || staleDeadlineCount < 0) {
                throw new IllegalArgumentException("invalid snapshot counters");
            }
            balances = immutableLongMap(balances, "balances");
            refunded = immutableLongMap(refunded, "refunded");
        }

        private static Map<String, Long> immutableLongMap(Map<String, Long> values, String name) {
            Objects.requireNonNull(values, name);
            Map<String, Long> copy = new TreeMap<>();
            for (var entry : values.entrySet()) {
                requireNonBlank(entry.getKey(), name + " key");
                Long value = Objects.requireNonNull(entry.getValue(), name + " value");
                if (value < 0) throw new IllegalArgumentException(name + " value cannot be negative");
                copy.put(entry.getKey(), value);
            }
            return Collections.unmodifiableMap(copy);
        }
    }

    /** One replay step, including event statuses observed while advancing time. */
    public record Step(
            String operation,
            Outcome outcome,
            String reason,
            List<ExecutionStatus> eventStatuses,
            Snapshot snapshot) {
        public Step {
            requireNonBlank(operation, "operation");
            Objects.requireNonNull(outcome, "outcome");
            reason = reason == null ? "" : reason;
            eventStatuses = List.copyOf(Objects.requireNonNull(eventStatuses, "eventStatuses"));
            Objects.requireNonNull(snapshot, "snapshot");
        }
    }

    private final Scenario scenario;
    private final CaseRegistry cases;
    private final HookDispatcher hooks;
    private final RuntimeKernel kernel;
    private final ExecutionContext context;
    private final ReceiptJournal journal;
    private final Map<String, String> transactionIds = new LinkedHashMap<>();
    private final List<Step> history = new ArrayList<>();

    private CaseBinding binding;
    private DeadlineTicket deadline;
    private State state = State.CREATED;
    private int staleDeadlineCount;

    public Appeal014Replay(Scenario scenario) {
        this.scenario = Objects.requireNonNull(scenario, "scenario");
        this.cases = new CaseRegistry();
        this.hooks = new HookDispatcher();
        this.kernel = new RuntimeKernel(OpcodeRegistry.empty(), hooks, cases);
        this.context = new ExecutionContext(
                new ScopeContext(scenario.owner(), scenario.subject(), scenario.reviewer()),
                scenario.startEpochDay(), DrawTape.of());
        this.journal = new ReceiptJournal(toJournalBalances(scenario.initialBalances()));
        hooks.register(EXPIRY_HOOK, this::handleExpiry);
    }

    public Scenario scenario() {
        return scenario;
    }

    public ExecutionContext context() {
        return context;
    }

    public Optional<CaseBinding> binding() {
        return Optional.ofNullable(binding);
    }

    public Optional<DeadlineTicket> deadline() {
        return Optional.ofNullable(deadline);
    }

    public State state() {
        return state;
    }

    public List<Step> history() {
        return List.copyOf(history);
    }

    /** Open the frozen case and post the three pre-existing penalty receipts. */
    public Step open() {
        if (state != State.CREATED) return record("open", Outcome.IDEMPOTENT_NOOP, "case already opened", List.of());
        try {
            preflightOpeningBalances();
            binding = cases.open(scenario.caseId(), scenario.owner(), scenario.subject());
            for (ReceiptSpec receipt : scenario.receipts()) {
                String transactionId = transactionId(receipt);
                transactionIds.put(receipt.key(), transactionId);
                journal.debit(transactionId, receipt.account(), receipt.resource(), receipt.amount());
            }
            state = State.DELIVERED;
            context.trace().add("zg361.014.open", "applied", Map.of(
                    "caseId", scenario.caseId(), "serial", binding.serial()));
            return record("open", Outcome.APPLIED, "frozen penalty receipts posted", List.of());
        } catch (RuntimeException exception) {
            return record("open", Outcome.INVALID, message(exception), List.of());
        }
    }

    /** Submit an appeal as the bound subject and schedule its finite deadline. */
    public Step submit() {
        return submit(scenario.subject());
    }

    public Step submit(ScopeRef actor) {
        if (binding == null) return record("submit", Outcome.INVALID, "case is not open", List.of());
        if (!scenario.subject().equals(actor)) return record("submit", Outcome.STALE_NOOP, "subject scope mismatch", List.of());
        if (!cases.matches(binding)) return record("submit", Outcome.STALE_NOOP, "case binding is stale", List.of());
        if (state == State.APPEAL_OPEN) return record("submit", Outcome.IDEMPOTENT_NOOP, "appeal already open", List.of());
        if (state != State.DELIVERED) return record("submit", Outcome.STALE_NOOP, "case is not deliverable", List.of());

        try {
            long due = Math.addExact(context.epochDay(), scenario.deadlineDays());
            deadline = DeadlineTicket.forBinding(binding, due);
            context.schedule(EXPIRY_HOOK, due, binding, Map.of(
                    "case_id", binding.caseId(),
                    "case_serial", binding.serial(),
                    "revision", binding.revision(),
                    "owner", binding.owner().id(),
                    "subject", binding.subject().id(),
                    "reviewer", scenario.reviewer().id(),
                    "expected_state", State.APPEAL_OPEN.name()));
            state = State.APPEAL_OPEN;
            context.trace().add("zg361.014.submit", "applied", Map.of("dueEpochDay", due));
            return record("submit", Outcome.APPLIED, "deadline scheduled", List.of());
        } catch (RuntimeException exception) {
            return record("submit", Outcome.INVALID, message(exception), List.of());
        }
    }

    /** Resolve as the frozen reviewer, refunding every receipt exactly once. */
    public Step resolve() {
        return resolve(scenario.reviewer());
    }

    public Step resolve(ScopeRef actor) {
        if (binding == null) return record("resolve", Outcome.INVALID, "case is not open", List.of());
        if (!scenario.reviewer().equals(actor)) return record("resolve", Outcome.STALE_NOOP, "reviewer scope mismatch", List.of());
        if (!cases.matches(binding)) return record("resolve", Outcome.STALE_NOOP, "case binding is stale", List.of());
        if (state == State.RESOLVED) return record("resolve", Outcome.IDEMPOTENT_NOOP, "appeal already resolved", List.of());
        if (state != State.APPEAL_OPEN) return record("resolve", Outcome.STALE_NOOP, "appeal is not open", List.of());

        List<ExecutionResult<?>> dueResults = List.of();
        if (deadline == null) return record("resolve", Outcome.INVALID, "open appeal has no deadline", List.of());
        if (deadline.isDue(context.epochDay())) {
            dueResults = kernel.dispatchDueEvents(context);
            if (state != State.APPEAL_OPEN) {
                return record("resolve", Outcome.STALE_NOOP, "deadline closed the appeal", statuses(dueResults));
            }
            if (!cases.matches(binding)) {
                return record("resolve", Outcome.STALE_NOOP, "deadline binding is stale", statuses(dueResults));
            }
        }

        try {
            preflightRefunds();
            for (ReceiptSpec receipt : scenario.receipts()) {
                String transactionId = transactionIds.get(receipt.key());
                journal.refund(transactionId, receipt.amount());
            }
            state = State.RESOLVED;
            context.trace().add("zg361.014.resolve", "applied", Map.of("receiptCount", scenario.receipts().size()));
            return record("resolve", Outcome.APPLIED, "all receipts refunded once", statuses(dueResults));
        } catch (RuntimeException exception) {
            return record("resolve", Outcome.INVALID, message(exception), statuses(dueResults));
        }
    }

    /** Advance the finite fixture clock and consume all due events in order. */
    public Step advanceTo(long day) {
        if (day < context.epochDay()) return record("advance", Outcome.INVALID, "clock cannot go backwards", List.of());
        context.advanceTo(day);
        List<ExecutionResult<?>> results = kernel.dispatchDueEvents(context);
        List<ExecutionStatus> statuses = statuses(results);
        long staleResults = results.stream()
                .filter(result -> result.status() == ExecutionStatus.STALE)
                .count();
        if (staleResults > 0) {
            staleDeadlineCount = Math.addExact(staleDeadlineCount, Math.toIntExact(staleResults));
            return record("advance", Outcome.STALE_DEADLINE_NOOP, "queued deadline binding is stale", statuses);
        }
        if (results.stream().anyMatch(result -> result.status() == ExecutionStatus.INVALID
                || result.status() == ExecutionStatus.UNSUPPORTED)) {
            return record("advance", Outcome.INVALID, "deadline hook rejected its payload", statuses);
        }
        if (state == State.EXPIRED) return record("advance", Outcome.APPLIED, "deadline expired appeal", statuses);
        if (results.stream().anyMatch(result -> "idempotent_noop".equals(result.value()))) {
            return record("advance", Outcome.IDEMPOTENT_NOOP, "deadline was already settled", statuses);
        }
        return record("advance", Outcome.APPLIED, results.isEmpty() ? "no due events" : "due events consumed", statuses);
    }

    /**
     * Replace the registry binding to exercise a stale queued event.  This is
     * deliberately named as a fixture control and is not a product operation.
     */
    public Step reopenForFixture() {
        return rebindForFixture(scenario.owner(), scenario.subject());
    }

    /** Replace the registry owner/subject to exercise scope and serial guards. */
    public Step rebindForFixture(ScopeRef owner, ScopeRef subject) {
        if (binding == null) return record("rebind_fixture", Outcome.INVALID, "case is not open", List.of());
        Objects.requireNonNull(owner, "owner");
        Objects.requireNonNull(subject, "subject");
        CaseBinding replacement = cases.open(scenario.caseId(), owner, subject);
        context.trace().add("zg361.014.rebind_fixture", "stale", Map.of(
                "serial", replacement.serial(), "owner", owner.id(), "subject", subject.id()));
        return record("rebind_fixture", Outcome.STALE_NOOP, "registry binding replaced", List.of());
    }

    /** Increment only the registry revision to exercise a stale deadline ticket. */
    public Step reviseForFixture() {
        if (binding == null) return record("revise_fixture", Outcome.INVALID, "case is not open", List.of());
        CaseBinding replacement = cases.revise(scenario.caseId());
        if (replacement == null) return record("revise_fixture", Outcome.INVALID, "case is not registered", List.of());
        context.trace().add("zg361.014.revise_fixture", "stale", Map.of("revision", replacement.revision()));
        return record("revise_fixture", Outcome.STALE_NOOP, "registry revision replaced", List.of());
    }

    public Snapshot snapshot() {
        long boundSerial = binding == null ? -1 : binding.serial();
        long boundRevision = binding == null ? -1 : binding.revision();
        CaseBinding current = binding == null ? null : cases.get(scenario.caseId()).orElse(null);
        Map<String, Long> balances = new TreeMap<>();
        for (BalanceKey key : scenario.initialBalances().keySet()) {
            balances.put(key.path(), journal.balance(key.account(), key.resource()));
        }
        Map<String, Long> refunded = new TreeMap<>();
        for (ReceiptSpec receipt : scenario.receipts()) {
            Receipt value = transactionIds.containsKey(receipt.key())
                    ? journal.get(transactionIds.get(receipt.key())) : null;
            refunded.put(receipt.key(), value == null ? 0L : value.refunded());
        }
        return new Snapshot(
                scenario.id(), state, context.epochDay(), boundSerial, boundRevision,
                current == null ? -1 : current.serial(), current == null ? -1 : current.revision(),
                deadline == null ? -1 : deadline.dueEpochDay(), context.queuedEvents(),
                staleDeadlineCount, balances, refunded);
    }

    private Step record(String operation, Outcome outcome, String reason, List<ExecutionStatus> eventStatuses) {
        Step step = new Step(operation, outcome, reason, eventStatuses, snapshot());
        history.add(step);
        return step;
    }

    private ExecutionResult<?> handleExpiry(HookDispatcher.HookInvocation invocation, ExecutionTrace trace) {
        Map<String, Object> payload = invocation.payload();
        if (binding == null || deadline == null || !payloadMatches(payload)
                || !binding.owner().equals(invocation.context().root())
                || !binding.subject().equals(invocation.context().current())) {
            trace.add(EXPIRY_HOOK, "stale", Map.of("reason", "payload does not match frozen case"));
            return new ExecutionResult<>(ExecutionStatus.STALE, null, "stale deadline payload", trace);
        }
        if (!deadline.isDue(context.epochDay())) {
            return new ExecutionResult<>(ExecutionStatus.INVALID, null, "deadline event fired before due day", trace);
        }
        if (state == State.RESOLVED || state == State.EXPIRED) {
            trace.add(EXPIRY_HOOK, "idempotent", Map.of("state", state.name()));
            return ExecutionResult.success("idempotent_noop", trace);
        }
        if (state != State.APPEAL_OPEN) {
            trace.add(EXPIRY_HOOK, "stale", Map.of("state", state.name()));
            return new ExecutionResult<>(ExecutionStatus.STALE, null, "appeal is not open", trace);
        }
        state = State.EXPIRED;
        trace.add(EXPIRY_HOOK, "applied", Map.of("state", State.EXPIRED.name()));
        return ExecutionResult.success("expired", trace);
    }

    private boolean payloadMatches(Map<String, Object> payload) {
        return Objects.equals(payload.get("case_id"), binding.caseId())
                && Objects.equals(payload.get("case_serial"), binding.serial())
                && Objects.equals(payload.get("revision"), binding.revision())
                && Objects.equals(payload.get("owner"), binding.owner().id())
                && Objects.equals(payload.get("subject"), binding.subject().id())
                && Objects.equals(payload.get("reviewer"), scenario.reviewer().id())
                && Objects.equals(payload.get("expected_state"), State.APPEAL_OPEN.name());
    }

    private void preflightOpeningBalances() {
        Map<BalanceKey, Long> required = new LinkedHashMap<>();
        for (ReceiptSpec receipt : scenario.receipts()) {
            BalanceKey key = receipt.balanceKey();
            long next = Math.addExact(required.getOrDefault(key, 0L), receipt.amount());
            required.put(key, next);
        }
        for (var entry : required.entrySet()) {
            long available = journal.balance(entry.getKey().account(), entry.getKey().resource());
            if (available < entry.getValue()) {
                throw new IllegalStateException("insufficient opening balance for " + entry.getKey().path());
            }
        }
    }

    private void preflightRefunds() {
        for (ReceiptSpec receipt : scenario.receipts()) {
            String transactionId = transactionIds.get(receipt.key());
            Receipt existing = transactionId == null ? null : journal.get(transactionId);
            if (existing == null || existing.net() != receipt.amount()) {
                throw new IllegalStateException("receipt is missing or already partially consumed: " + receipt.key());
            }
            long balance = journal.balance(receipt.account(), receipt.resource());
            if (Long.MAX_VALUE - balance < receipt.amount()) {
                throw new ArithmeticException("refund would overflow " + receipt.key());
            }
        }
    }

    private String transactionId(ReceiptSpec receipt) {
        return scenario.caseId() + "." + binding.serial() + "." + receipt.key();
    }

    private static Map<String, Long> toJournalBalances(Map<BalanceKey, Long> balances) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (var entry : balances.entrySet()) result.put(entry.getKey().journalKey(), entry.getValue());
        return result;
    }

    private static List<ExecutionStatus> statuses(List<ExecutionResult<?>> results) {
        return results.stream().map(ExecutionResult::status).toList();
    }

    private static String message(RuntimeException exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? exception.getClass().getSimpleName() : exception.getMessage();
    }

    private static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " is blank");
    }
}
