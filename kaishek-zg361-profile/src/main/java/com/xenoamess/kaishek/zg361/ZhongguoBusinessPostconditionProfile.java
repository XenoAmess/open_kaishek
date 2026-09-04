package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Static-only profile for the two cross-domain Phase-2 business receipts.
 *
 * <p>The descriptors name the fixed public query contracts and the minimum
 * fields/invariants needed by offline fixtures.  Neither descriptor certifies
 * the native provider or CK3 runtime behavior.</p>
 */
public final class ZhongguoBusinessPostconditionProfile {
    public static final String ID = "ck3-1.19.0.6-zg361-business-postconditions-v1";
    public static final String PROJECTS_METRICS_ROOT_COMMIT =
            "953634265ebf298cec3f2cf3065060e577dc8d17";
    public static final String PROJECTS_METRICS_SOURCE_CONTRACT_SHA256 =
            "362e9e88ff0a2ac8a7ec5cd396959a7200ed9f4f6d8519c953fe1798b903f0f2";
    public static final String PROJECTS_METRICS_ABI_SHA256 =
            "c0135b790089de3807ae2139431c3cd1df3867d61408e36cd64df17b5dc4fadd";
    public static final String PROJECTS_METRICS_SCHEMA_SHA256 =
            "3763b17f937d4c36c5643a41d54ccd449cd23a8f5f94cddb4a4edbed7bbdbfd4";
    public static final String PROJECTS_METRICS_PYTHON_CONTRACT_SHA256 =
            "468d4ce43a28606148290b00b715f04dc33ba7f5bf95299949afe37b66b37195";
    public static final String PROJECTS_METRICS_ALLOWLIST_ID =
            "zg361-cp26-direct-p3m229-lineage-v2";
    public static final boolean PROJECTS_METRICS_CHECKPOINT_STATE_REQUIRED = true;
    public static final boolean PROJECTS_METRICS_DEFAULT_CANDIDATE_ENABLED = false;
    public static final boolean PROJECTS_METRICS_PRODUCTION_LIVE = false;
    public static final String PROMOTION_COMPENSATION_ROOT_COMMIT =
            "cac1e85b616827a9ae11d755dd71f119325e6f3f";
    public static final String PROMOTION_COMPENSATION_SOURCE_CONTRACT_SHA256 =
            "98ab5f09bb44d6d5cb1062fea64e6fdf9e41cf160f64ecd8d5a644b9086ef627";
    public static final String PROMOTION_COMPENSATION_PRIVATE_CANDIDATE_SWITCH =
            "XAR_CK3_ENABLE_ZHONGGUO_PROMOTION_COMPENSATION_CANDIDATE_V1";
    public static final boolean PROMOTION_COMPENSATION_DEFAULT_SWITCH_ENABLED =
            false;
    public static final boolean PROMOTION_COMPENSATION_DEFAULT_ADAPTER_ADVERTISED =
            false;
    public static final boolean PROMOTION_COMPENSATION_PRIVATE_CANDIDATE_ADVERTISES =
            true;
    public static final boolean PROMOTION_COMPENSATION_CANDIDATE_LIVE_TESTED =
            false;
    public static final boolean PROMOTION_COMPENSATION_PUBLIC_API_CHANGED =
            false;
    public static final boolean PROMOTION_COMPENSATION_PRODUCTION_LIVE = false;

    public static final CapabilityDescriptor PROJECTS_METRICS = descriptor(
            "game.command.query-zhongguo-projects-metrics-postcondition-v1",
            List.of(
                    "checkpoint_state",
                    "source_identity.owner_character_id",
                    "source_identity.subject_character_id",
                    "source_identity.cycle_serial",
                    "source_identity.case_serial",
                    "result_identity.owner_character_id",
                    "result_identity.subject_character_id",
                    "result_identity.cycle_serial",
                    "result_identity.case_serial",
                    "contribution.receipt_id",
                    "contribution.receipt_revision",
                    "contribution.value",
                    "metrics_result.source_contribution_receipt_id",
                    "metrics_result.source_contribution_receipt_revision",
                    "metrics_result.metrics_revision",
                    "metrics_result.dictionary_key"),
            List.of(
                    "cp26_routes_a_b_mint_positive_monotonic_receipt_id",
                    "cp26_receipt_revision_matches_post_operation_case_e_revision",
                    "phase3_initializer_requires_current_owner_subject_cycle",
                    "source_result_contribution_metrics_share_project_identity",
                    "metrics_receipt_id_and_revision_equal_contribution",
                    "cp26_ready_p3_absent_exposes_no_p3_result",
                    "route_c_does_not_publish_business_lineage"));

    public static final CapabilityDescriptor PROMOTION_COMPENSATION = descriptor(
            "game.command.query-zhongguo-promotion-compensation-postcondition-v1",
            List.of(
                    "promotion.receipt_serial",
                    "promotion.receipt_revision",
                    "promotion.consumer_revision",
                    "compensation.choice_serial",
                    "compensation.receipt_serial",
                    "compensation.choice_revision",
                    "compensation.receipt_revision",
                    "portfolio.delivered_result_case",
                    "compensation.numbered_receipt_internal_case"),
            List.of(
                    "promotion_and_compensation_serials_are_positive_and_equal",
                    "business_serial_equals_portfolio_delivered_result_case",
                    "choice_revision_equals_promotion_consumer_and_posted_choice_revision",
                    "posted_receipt_revision_is_later_than_choice_revision",
                    "t_and_l_ae_af_kernel_case_identities_remain_independent"));

    private static final Map<String, CapabilityDescriptor> DESCRIPTORS;

    static {
        var descriptors = new TreeMap<String, CapabilityDescriptor>();
        descriptors.put(PROJECTS_METRICS.id(), PROJECTS_METRICS);
        descriptors.put(PROMOTION_COMPENSATION.id(), PROMOTION_COMPENSATION);
        DESCRIPTORS = Map.copyOf(descriptors);
    }

    private ZhongguoBusinessPostconditionProfile() { }

    public static Optional<CapabilityDescriptor> find(String id) {
        return id == null ? Optional.empty() : Optional.ofNullable(DESCRIPTORS.get(id));
    }

    public static CapabilityDescriptor require(String id) {
        return find(id).orElseThrow(
                () -> new IllegalArgumentException("unregistered capability: " + id));
    }

    public static Collection<CapabilityDescriptor> all() {
        return DESCRIPTORS.values();
    }

    private static CapabilityDescriptor descriptor(
            String id, List<String> fields, List<String> invariants) {
        return new CapabilityDescriptor(id, ID,
                fields, invariants, true, true, false, false);
    }
}
