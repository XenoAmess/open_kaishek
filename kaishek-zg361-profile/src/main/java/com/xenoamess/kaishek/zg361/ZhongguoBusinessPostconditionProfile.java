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

    public static final CapabilityDescriptor PROJECTS_METRICS = descriptor(
            "game.command.query-zhongguo-projects-metrics-postcondition-v1",
            List.of(
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
