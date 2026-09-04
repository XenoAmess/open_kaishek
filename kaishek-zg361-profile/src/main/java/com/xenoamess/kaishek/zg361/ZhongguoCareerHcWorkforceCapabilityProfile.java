package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/**
 * Static contract for the exact-build B4 career-HC/workforce postcondition query.
 *
 * <p>The companion project owns the native allowlist reader, mailbox, driver,
 * service, and MCP transport. This profile records only the public read-only
 * query and its observable response boundary. It does not register a Paradox
 * opcode, execute the provider, or advertise the downstream action cell.</p>
 */
public final class ZhongguoCareerHcWorkforceCapabilityProfile {
    public static final String ID =
            "ck3-1.19.0.6-zg361-career-hc-workforce-postcondition-v1";
    public static final String CAPABILITY_ID =
            "game.command.query-zhongguo-career-hc-workforce-postcondition-v1";
    public static final String STEP_ID =
            "query-zhongguo-career-hc-workforce-postcondition-v1";
    public static final String CASE_KIND =
            "zhongguo.career-hc.workforce.route-b-no-hc-debit";
    public static final String GAME_VERSION = "1.19.0.6";
    public static final String EXECUTABLE_SHA256 =
            "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86";
    public static final String ROOT_ABI_SHA256 =
            "8e91879901316bb165983f577887cdf7cb37c30ab7c98cf63d97f3fee77c0928";
    public static final String ROOT_SOURCE_CONTRACT_SHA256 =
            "26b1f859cb6194d1d9295443be370862926dc81dabaa57d1f447f62c09a4062e";
    public static final String ROOT_SCHEMA_SHA256 =
            "453b9a93fde9d472aa82c687bb2fd3db4fbef06d315ab91dcc4e167fa2f5b6c5";
    public static final String ROOT_PYTHON_CONTRACT_SHA256 =
            "398210553c74fdefa800b77f1287570598e4c2c4c73be35f243bec889bc14c76";
    public static final String ROOT_PROVIDER_COMMIT =
            "5e6fc9a0073ea7bbf9542bb3d95dfcd812c3a1f6";
    public static final boolean DEFAULT_ADAPTER_ADVERTISED = false;
    public static final boolean DOWNSTREAM_ACTION_CAPABILITY_ADVERTISED = false;

    public static final CapabilityDescriptor POSTCONDITION =
            new CapabilityDescriptor(
                    CAPABILITY_ID,
                    ID,
                    List.of(
                            "schema_version",
                            "status",
                            "capability",
                            "case_kind",
                            "source_backend_id",
                            "request_nonce",
                            "snapshot_revision",
                            "date_raw",
                            "paused",
                            "player_character_id",
                            "subject_character_id",
                            "requested_owner_character_id",
                            "m360_identity.owner_character_id",
                            "m360_identity.subject_character_id",
                            "m360_identity.cycle_serial",
                            "m360_identity.case_serial",
                            "m360_receipt.owner_character_id",
                            "m360_receipt.subject_character_id",
                            "m360_receipt.cycle_serial",
                            "m360_receipt.case_serial",
                            "m360_receipt.state",
                            "m360_receipt.choice",
                            "m360_receipt.provider_observed",
                            "career_hc_partition.authorized",
                            "career_hc_partition.available",
                            "career_hc_partition.reserved",
                            "career_hc_partition.occupied",
                            "career_hc_partition.frozen",
                            "career_hc_partition.reclaimed",
                            "career_hc_partition.conserved",
                            "career_hc_partition.provider_observed",
                            "route_b_cost.manager_cost_total",
                            "route_b_cost.provider_observed",
                            "readiness.player_subject_binding_ready",
                            "readiness.owner_binding_ready",
                            "readiness.m360_identity_ready",
                            "readiness.m360_route_b_receipt_ready",
                            "readiness.career_hc_partition_ready",
                            "readiness.career_hc_conservation_ready",
                            "readiness.route_b_manager_cost_zero_ready",
                            "readiness.same_frame_ready",
                            "readiness.ready",
                            "provenance.game_version",
                            "provenance.executable_sha256",
                            "provenance.backend_id",
                            "provenance.consumer_id",
                            "provenance.allowlist_id",
                            "provenance.variable_context_for_scope_rva",
                            "provenance.variable_identifier_table_rva",
                            "provenance.variable_identifier_lookup_rva",
                            "provenance.variable_identifier_name_rva",
                            "provenance.character_storage_slot_rva",
                            "provenance.character_fallback_slot_rva",
                            "unavailable_reason",
                            "build.version",
                            "build.exe_sha256",
                            "source.bridge_version",
                            "source.game_adapter_id",
                            "source.backend_id",
                            "source.connection_generation",
                            "source.snapshot_id",
                            "source.revision",
                            "source.native_revision",
                            "source.date_raw",
                            "source.paused",
                            "source.player_character_id",
                            "binding.request_nonce",
                            "binding.snapshot_id",
                            "binding.revision",
                            "binding.native_revision",
                            "binding.connection_generation",
                            "binding.date_raw",
                            "binding.paused",
                            "binding.player_character_id",
                            "binding.subject_character_id",
                            "binding.owner_character_id",
                            "binding.expected_revision"),
                    List.of(
                            "public_request_is_nonce_revision_and_owner_only",
                            "subject_is_the_paused_played_character",
                            "receipt_owner_matches_filter_and_differs_from_subject",
                            "caller_cannot_select_subject_variable_or_receipt_values",
                            "route_b_receipt_is_state_4_choice_2",
                            "career_hc_buckets_are_nonnegative",
                            "partition_sum_equals_authorized_and_conserved_is_true",
                            "route_b_manager_cost_total_is_zero",
                            "two_complete_allowlist_reads_are_identical",
                            "frame_before_and_after_are_identical",
                            "typed_unavailable_never_produces_a_green_postcondition",
                            "action_ack_is_never_a_business_postcondition"),
                    true,
                    true,
                    false,
                    false);

    private ZhongguoCareerHcWorkforceCapabilityProfile() { }
}
