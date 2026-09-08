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
            "45024edf723a75502728f8f07b345f4271697cfe";
    public static final String PROJECTS_METRICS_SOURCE_CONTRACT_SHA256 =
            "bea30b47cee6fdc66c04e48a42ebf5ac0115c62a8ea34698fcd0428530f4649a";
    public static final String PROJECTS_METRICS_ABI_SHA256 =
            "1624d793b9461dbf6d08c64f60219f3567bc9fdf2805afe94c60f6aaf4deac6c";
    public static final String PROJECTS_METRICS_SCHEMA_SHA256 =
            "44f0429e8b5ab46db38611a9779198cefd97b0fe435c0c847f3cb57674732380";
    public static final String PROJECTS_METRICS_PYTHON_CONTRACT_SHA256 =
            "b2dba9ee76457d0ec72bd87464eec145618e76482bb77d4b6687a03c88a60436";
    public static final String PROJECTS_METRICS_ALLOWLIST_ID =
            "zg361-cp-portfolio-cp26-direct-p3m229-lineage-v3";
    public static final int PROJECTS_METRICS_ALLOWLIST_COUNT = 49;
    public static final boolean PROJECTS_METRICS_CHECKPOINT_STATE_REQUIRED = true;
    public static final boolean PROJECTS_METRICS_EXPLICIT_SUBJECT_REQUIRED = true;
    public static final boolean PROJECTS_METRICS_PORTFOLIO_CLOSURE_REQUIRED = true;
    public static final String PROJECTS_METRICS_PRIVATE_CANDIDATE_SWITCH =
            "XAR_CK3_ENABLE_ZHONGGUO_PROJECTS_METRICS_CANDIDATE_V1";
    public static final boolean PROJECTS_METRICS_PRIVATE_CANDIDATE_LIVE_TESTED = true;
    public static final String PROJECTS_METRICS_PAUSED_LIVE_ARTIFACT_SHA256 =
            "926BBD25076F69205B8AAA7CCC366AB470227BCBE174017B7E86C282862D7B01";
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
                    "requested_subject_character_id",
                    "credit_project_portfolio.closed",
                    "credit_project_portfolio.cycle_serial",
                    "credit_project_portfolio.final_owner_character_id",
                    "credit_project_portfolio.final_subject_character_id",
                    "credit_project_portfolio.final_cycle_serial",
                    "credit_project_portfolio.final_case_serial",
                    "credit_project_portfolio.final_state",
                    "credit_project_portfolio.final_conservation_ok",
                    "credit_project_portfolio.pending_player_event",
                    "credit_project_portfolio.provider_observed",
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
                    "metrics_result.dictionary_key",
                    "readiness.portfolio_observed",
                    "readiness.portfolio_closed"),
            List.of(
                    "cp26_routes_a_b_mint_positive_monotonic_receipt_id",
                    "cp26_receipt_revision_matches_post_operation_case_e_revision",
                    "provider_reads_explicit_subject_portfolio_closure_while_paused_player_is_bound_owner_or_subject",
                    "portfolio_closure_requires_final_tuple_conservation_and_no_pending_player_event",
                    "provider_reads_only_the_explicit_project_subject_scope",
                    "r303_exact_build_private_candidate_proves_explicit_subject_portfolio_closure_and_cp26_to_p3m229_lineage",
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
