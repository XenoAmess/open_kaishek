package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/**
 * Static contract for the G2 truce-duration observation gap.
 *
 * <p>This descriptor deliberately records the fields needed by the
 * exact-build read-only observer without registering a script opcode or
 * claiming that CK3 has returned a value. Native and runtime certification
 * remain false until a paused live artifact proves the evaluator result.</p>
 */
public final class G2TruceEvaluatorCapabilityProfile {
    public static final String ID = "ck3-1.19.0.6-g2-truce-evaluator-v1";

    public static final CapabilityDescriptor EVALUATED_DAYS =
            new CapabilityDescriptor(
                    "game.command.query-g2-truce-evaluated-days-v1",
                    ID,
                    List.of(
                            "owner_character_id",
                            "toward_character_id",
                            "evaluated_days",
                            "evaluated_days_observable",
                            "current_date_raw",
                            "expiry_observable",
                            "expiry_date_raw",
                            "frame_identity"),
                    List.of(
                            "owner_and_toward_are_distinct_characters",
                            "evaluated_days_is_nonnegative_when_observable",
                            "two_evaluator_reads_match_on_one_paused_frame",
                            "expiry_is_not_inferred_from_evaluated_days",
                            "read_only_observer_never_submits_termination"),
                    true,
                    true,
                    false,
                    false);

    private G2TruceEvaluatorCapabilityProfile() { }
}
