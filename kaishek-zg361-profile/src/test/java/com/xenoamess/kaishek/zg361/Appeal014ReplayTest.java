package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.runtime.ExecutionStatus;
import com.xenoamess.kaishek.runtime.ScopeRef;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Differential and negative-path checks for the synthetic 014 replay. */
class Appeal014ReplayTest {
    @Test
    void everyHandAuthoredVectorMatchesItsIndependentExpectedSnapshot() {
        for (var fixture : Appeal014DifferentialFixture.cases()) {
            var comparison = Appeal014DifferentialFixture.compare(fixture);
            assertTrue(comparison.match(), () -> fixture.id() + ": " + comparison.mismatches());
        }
    }

    @Test
    void successfulResolutionRefundsThreeReceiptsExactlyOnceAndLeavesAuditRows() {
        var fixture = Appeal014DifferentialFixture.cases().stream()
                .filter(item -> item.id().equals("resolved-before-deadline"))
                .findFirst().orElseThrow();
        var replay = Appeal014DifferentialFixture.run(fixture);

        assertEquals(Appeal014Replay.State.RESOLVED, replay.state());
        assertEquals(1000L, replay.snapshot().balances().get("subject_treasury/gold"));
        assertEquals(1000L, replay.snapshot().balances().get("subject_personal_gold/gold"));
        assertEquals(1000L, replay.snapshot().balances().get("subject_merit/merit"));
        assertEquals(50L, replay.snapshot().refunded().get(Appeal014Replay.TREASURY_RECEIPT));
        assertEquals(25L, replay.snapshot().refunded().get(Appeal014Replay.GOLD_RECEIPT));
        assertEquals(60L, replay.snapshot().refunded().get(Appeal014Replay.MERIT_RECEIPT));
        assertEquals(5, replay.history().size());
        assertEquals(0, replay.context().queuedEvents());
    }

    @Test
    void changedOwnerMakesQueuedDeadlineStaleWithoutRefundOrStateMutation() {
        var scenario = Appeal014DifferentialFixture.cases().stream()
                .filter(item -> item.id().equals("stale-after-reopen"))
                .findFirst().orElseThrow().scenario();
        var replay = new Appeal014Replay(scenario);
        replay.open();
        replay.submit();
        replay.rebindForFixture(new ScopeRef(com.xenoamess.kaishek.profile.ScopeType.CHARACTER, "other-owner"),
                scenario.subject());
        var advanced = replay.advanceTo(190);

        assertEquals(Appeal014Replay.Outcome.STALE_DEADLINE_NOOP, advanced.outcome());
        assertEquals(ExecutionStatus.STALE, advanced.eventStatuses().get(0));
        assertEquals(Appeal014Replay.State.APPEAL_OPEN, replay.state());
        assertEquals(1, replay.snapshot().staleDeadlineCount());
        assertEquals(950L, replay.snapshot().balances().get("subject_treasury/gold"));
        assertEquals(0L, replay.snapshot().refunded().get(Appeal014Replay.TREASURY_RECEIPT));
    }

    @Test
    void changedExecutionScopesMakeDeadlineStaleEvenWhenRegistryBindingIsUnchanged() {
        var scenario = Appeal014DifferentialFixture.cases().get(0).scenario();
        var replay = new Appeal014Replay(scenario);
        replay.open();
        replay.submit();
        replay.context().scopes(replay.context().scopes().withCurrent(
                new ScopeRef(com.xenoamess.kaishek.profile.ScopeType.CHARACTER, "other-subject")));

        var advanced = replay.advanceTo(190);
        assertEquals(Appeal014Replay.Outcome.STALE_DEADLINE_NOOP, advanced.outcome());
        assertEquals(Appeal014Replay.State.APPEAL_OPEN, replay.state());
        assertEquals(1, replay.snapshot().staleDeadlineCount());
    }

    @Test
    void invalidOpeningBalanceFailsBeforeBindingOrReceiptWrites() {
        var base = Appeal014DifferentialFixture.cases().get(0).scenario();
        var insufficient = new Appeal014Replay.Scenario(
                "insufficient", "appeal-014-insufficient", base.owner(), base.subject(), base.reviewer(),
                base.startEpochDay(), base.deadlineDays(), base.receipts(),
                java.util.Map.of(new Appeal014Replay.BalanceKey("subject_treasury", "gold"), 49L,
                        new Appeal014Replay.BalanceKey("subject_personal_gold", "gold"), 1000L,
                        new Appeal014Replay.BalanceKey("subject_merit", "merit"), 1000L));
        var replay = new Appeal014Replay(insufficient);
        var step = replay.open();

        assertEquals(Appeal014Replay.Outcome.INVALID, step.outcome());
        assertTrue(replay.binding().isEmpty());
        assertTrue(replay.snapshot().balances().values().stream().allMatch(value -> value > 0));
        assertTrue(replay.snapshot().refunded().values().stream().allMatch(value -> value == 0));
    }

    @Test
    void provenanceSidecarIsPresentAndDeclaresFixtureOnlyReadiness() throws Exception {
        String resourceName = "/zg361-014-appeal-differential.json";
        String json;
        try (InputStream stream = Appeal014ReplayTest.class.getResourceAsStream(resourceName)) {
            assertNotNull(stream, "differential fixture resource is missing");
            json = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertTrue(json.contains("zg361-014-appeal-replay-v1"));
        assertTrue(json.contains("\"readiness\": \"runtime-fixture\""));
        assertTrue(json.contains(Appeal014DifferentialFixture.ACCEPTANCE_SOURCE_SHA256));
        assertTrue(json.contains(Appeal014DifferentialFixture.EFFECT_SOURCE_SHA256));
        assertFalse(json.contains("differential-certified"));
        assertFalse(json.contains("product-live"));
    }
}
