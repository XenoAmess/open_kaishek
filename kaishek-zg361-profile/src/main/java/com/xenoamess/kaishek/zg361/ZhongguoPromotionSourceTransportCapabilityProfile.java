package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/**
 * Static contract for the B7 promotion source fail-closed transports.
 *
 * <p>The companion project advertises only the two transport capabilities.
 * The product query/action capability flags remain false pending a live run,
 * so this profile does not promote either product capability.</p>
 */
public final class ZhongguoPromotionSourceTransportCapabilityProfile {
    public static final String ID =
            "ck3-1.19.0.6-zg361-promotion-source-transport-v1";
    public static final String QUERY_CAPABILITY_ID =
            "game.command.query-zhongguo-promotion-source-progress-v1";
    public static final String QUERY_TRANSPORT_CAPABILITY_ID =
            "game.contract.zhongguo-promotion-source-progress-v1-fail-closed";
    public static final String QUERY_STEP_ID =
            "query-zhongguo-promotion-source-progress-v1";
    public static final String ACTION_CAPABILITY_ID =
            "game.command.activate-zhongguo-review-now-v1";
    public static final String ACTION_TRANSPORT_CAPABILITY_ID =
            "game.contract.zhongguo-review-now-action-v1-fail-closed";
    public static final String ACTION_STEP_ID =
            "activate-zhongguo-review-now-v1";
    public static final String ALLOWLIST_ID =
            "zg361-promotion-source-fixed-widget-progress-v1";
    public static final String GAME_VERSION = "1.19.0.6";
    public static final String EXECUTABLE_SHA256 =
            "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86";
    public static final String ROOT_INTEGRATION_COMMIT =
            "4974324fece6ba152bada6069f8a12718e4da8f6";
    public static final String ROOT_SOURCE_CONTRACT_SHA256 =
            "4ebeb1463d421d2278d69b68ab9a070de776a3d38a7e03e54719b0401d6817b5";
    public static final String ROOT_ABI_SHA256 =
            "f489cae78e8a15da7b284b93eb33a6ad3fe6fd5735899f797a4a0e690e75f400";
    public static final String ROOT_PYTHON_CONTRACT_SHA256 =
            "9f8acf825ed2df8410484f48a5da979f0fb290056cf5080936e1c52409d21094";
    public static final boolean QUERY_PRODUCTION_CAPABILITY_ADVERTISED = false;
    public static final boolean ACTION_PRODUCTION_CAPABILITY_ADVERTISED = false;
    public static final boolean PRODUCTION_LIVE_READY = false;
    public static final boolean ACTION_ACK_IS_STATE_EVIDENCE = false;

    public static final List<String> FIXED_WIDGETS = List.of(
            "zg361_promotion_source_bridge_window",
            "zg361_promotion_source_review_now_action",
            "zg361_promotion_source_b1_active",
            "zg361_promotion_source_central_active",
            "zg361_promotion_source_pp_active",
            "zg361_promotion_source_central_stage_1",
            "zg361_promotion_source_central_stage_2",
            "zg361_promotion_source_central_stage_3",
            "zg361_promotion_source_central_stage_4",
            "zg361_promotion_source_central_stage_5",
            "zg361_promotion_source_central_stage_6",
            "zg361_promotion_source_central_stage_7",
            "zg361_promotion_source_central_stage_8",
            "zg361_promotion_source_central_stage_9",
            "zg361_promotion_source_central_stage_10",
            "zg361_promotion_source_central_stage_11",
            "zg361_promotion_source_central_status_0",
            "zg361_promotion_source_central_status_1",
            "zg361_promotion_source_central_status_2",
            "zg361_promotion_source_central_status_3",
            "zg361_promotion_source_central_status_4",
            "zg361_promotion_source_central_status_5",
            "zg361_promotion_source_cl_partial_open",
            "zg361_promotion_source_cl_digest_pending",
            "zg361_promotion_source_cl_cycle_matches",
            "zg361_promotion_source_cl_frozen_positive",
            "zg361_promotion_source_cl_expectations_match",
            "zg361_promotion_source_cl_ah_complete",
            "zg361_promotion_source_cl_ai_complete");

    public static final CapabilityDescriptor QUERY_TRANSPORT =
            new CapabilityDescriptor(
                    QUERY_TRANSPORT_CAPABILITY_ID,
                    ID,
                    List.of(
                            "request_nonce",
                            "snapshot_revision",
                            "date_raw",
                            "paused",
                            "player_character_id",
                            "widgets.stable_identity",
                            "widgets.runtime_name",
                            "widgets.exists",
                            "widgets.effective_visible",
                            "widgets.enabled",
                            "readiness.player_binding_ready",
                            "readiness.gui_root_ready",
                            "readiness.exact_widget_set_ready",
                            "readiness.same_frame_ready",
                            "readiness.query_ready",
                            "readiness.production_live_ready",
                            "unavailable_reason",
                            "binding.query_sequence",
                            "production_capability_advertised"),
                    List.of(
                            "public_request_is_nonce_plus_expected_revision_only",
                            "caller_cannot_select_character_widget_variable_or_decision",
                            "fixed_twenty_nine_widget_allowlist_is_exact",
                            "query_runs_twice_on_one_paused_application_main_frame",
                            "missing_or_inconsistent_data_is_typed_unavailable",
                            "product_query_capability_remains_unadvertised_until_live"),
                    true,
                    true,
                    false,
                    false);

    public static final CapabilityDescriptor ACTION_TRANSPORT =
            new CapabilityDescriptor(
                    ACTION_TRANSPORT_CAPABILITY_ID,
                    ID,
                    List.of(
                            "request_nonce",
                            "action_sequence",
                            "snapshot_revision",
                            "accepted",
                            "status",
                            "rejection_reason",
                            "action_ack",
                            "production_capability_advertised"),
                    List.of(
                            "action_is_fixed_to_review_now_product_semantics",
                            "request_is_bound_to_prior_progress_query_and_paused_frame",
                            "caller_cannot_select_character_widget_variable_or_decision",
                            "accepted_ack_is_verification_pending_not_state_evidence",
                            "independent_later_progress_query_must_prove_b1_entry",
                            "product_action_capability_remains_unadvertised_until_live"),
                    false,
                    false,
                    false,
                    false);

    private ZhongguoPromotionSourceTransportCapabilityProfile() { }
}
