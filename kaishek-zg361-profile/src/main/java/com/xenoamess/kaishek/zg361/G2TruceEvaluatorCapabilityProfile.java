package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/**
 * Static contract for the G2 truce-duration observation boundary.
 *
 * <p>This descriptor deliberately records the fields needed by the
 * exact-build read-only observer without registering a script opcode. The
 * provider is installed in the default production build after a private-live
 * proof, but native and runtime certification remain false until that default
 * production binary passes the paused public-wire acceptance.</p>
 */
public final class G2TruceEvaluatorCapabilityProfile {
    public static final String ID = "ck3-1.19.0.6-g2-truce-evaluator-v1";
    public static final String ROOT_PROVIDER_COMMIT =
            "a3c13246ef32b35e117b08dbb86f61986c1dabe3";
    public static final String ROOT_PRODUCTION_CANDIDATE_COMMIT =
            "0b0fbc047610a8ef25f47a59f7b42c83c176d69e";
    public static final String ROOT_SOURCE_CONTRACT_SHA256 =
            "df720cd33d3606634378a5cff20d77227b82a35265269789bde4a51cff988e0d";
    public static final String ROOT_PRODUCTION_MANIFEST_SHA256 =
            "6b5783bca00a1b082aa5fec834ee73a95860535549b7525a616c82f178265c58";
    public static final String ROOT_PROVIDER_SOURCE_SHA256 =
            "5299c88f4cd7b27959e4518d5a48061ae0ef39ae629a2590c269a8fe912f397a";
    public static final String ROOT_PROVIDER_HEADER_SHA256 =
            "e49d31f35fbb3f5bc713ea94cb9ff3e83ec9fa713772968a0dcffefd20200b2a";
    public static final boolean PUBLIC_SCHEMA_CHANGED = false;
    public static final boolean PRIVATE_LEAF_READER_LIVE_OBSERVED = true;
    public static final boolean DEFAULT_PRODUCTION_LEAF_READER_INSTALLED = true;
    public static final boolean DEFAULT_PRODUCTION_BINARY_LIVE_VALIDATED = false;
    public static final boolean EXPIRY_OBSERVABLE = false;

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
