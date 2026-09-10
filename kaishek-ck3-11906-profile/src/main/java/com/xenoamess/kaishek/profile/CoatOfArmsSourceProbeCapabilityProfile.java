package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static compatibility projection for the companion's exact-build coat-of-arms probe.
 *
 * <p>The companion owns the native hook, MCP server, clipboard interaction and
 * result validation.  This profile records the public v1 envelope without
 * treating the dedicated coat-of-arms reader as Paradox script syntax or
 * granting open_kaishek any CK3 process authority.</p>
 */
public final class CoatOfArmsSourceProbeCapabilityProfile {
    public static final String ROOT_PROVIDER_COMMIT =
            "16755189c172772669a0d30456da1f977d6b6de0";
    public static final String ROOT_MCP_SERVER_SHA256 =
            "7DD3207AB2C53385FBAEBB4C28B9A30614ED229C05A20B58694EF7B4A355ABC1";
    public static final String ROOT_PYTHON_CONTRACT_SHA256 =
            "031BF5DF84094953910C2AF147B16ED40949C649DFDCF76295F876E7F0F99E5A";
    public static final String ROOT_NATIVE_ABI_SHA256 =
            "EFABF2A04C0A8AD4A61A63B7FA0E6301F2B63AAB96C7D362EBCDA0F61F8FA66B";
    public static final String ROOT_NATIVE_IMPLEMENTATION_SHA256 =
            "8E4209801529AB6C9505BFEB5CE0E587DA69D89C4289A4621FFEE122A66290A8";
    public static final String ROOT_SERVICE_SHA256 =
            "336BD52A1E32F62003AB53585401D961A060FBF8F37F40BC54ACD3D68DDBF564";
    public static final String ROOT_NATIVE_DRIVER_SHA256 =
            "82F6D94E22525400801E0885A4D0D77B6D1C4011F30462D124C062D02A9E3092";
    public static final String ROOT_PROVIDER_TEST_SHA256 =
            "C8DE570BD7226094A4D52D0F607B2EBBDBE3319EBB877795CB0CCBFB44194FBE";

    public static final String TOOL_ID = "ck3_probe_coat_of_arms_source_v1";
    public static final String CAPABILITY_ID =
            "game.command.probe-coat-of-arms-source-v1";
    public static final String STEP_ID = "probe-coat-of-arms-source-v1";
    public static final String PROFILE_VERSION =
            "ck3-1.19.0.6-coat-of-arms-source-probe-v1";
    public static final String RESULT_SCHEMA = "coat-of-arms-source-probe-v1";
    public static final String NATIVE_RESULT_SCHEMA =
            "xar.ck3.coat-of-arms-designer-probe.v1";
    public static final int SCHEMA_VERSION = 1;
    public static final int MAXIMUM_SOURCE_BYTES = 128 * 1024;

    public static final List<String> REQUEST_FIELDS = List.of(
            "source", "expected_revision", "apply");

    public static final CapabilityDescriptor PROBE =
            new CapabilityDescriptor(
                    TOOL_ID,
                    PROFILE_VERSION,
                    List.of(
                            "schema",
                            "schema_version",
                            "step",
                            "status",
                            "detected",
                            "designer_observed",
                            "clipboard_written",
                            "clipboard_readback_matched",
                            "apply_requested",
                            "paste_invoked",
                            "applied",
                            "candidate_index",
                            "preview_coat_of_arms_handle",
                            "active_coat_of_arms_index",
                            "reason",
                            "source_sha256",
                            "source_bytes",
                            "binding.mode",
                            "binding.revision",
                            "binding.connection_generation",
                            "binding.bridge_pid"),
                    List.of(
                            "source_is_nonempty_ascii_and_bounded_to_131072_bytes",
                            "expected_revision_is_zero_only_for_frontend_binding",
                            "frontend_snapshot_and_gameplay_bindings_use_positive_revisions",
                            "request_and_result_are_bound_to_one_bridge_pid_and_connection_generation",
                            "hybrid_backend_routes_to_native_without_visual_fallback",
                            "detection_requires_exact_clipboard_write_and_readback",
                            "apply_requires_detection_and_verified_designer_working_state_change",
                            "detect_only_still_mutates_clipboard_and_paste_preview",
                            "applied_does_not_claim_upper_window_finish_or_saved_persistence",
                            "parameterized_probe_never_enters_autonomous_planner_actions",
                            "unknown_mcp_arguments_are_rejected",
                            "dedicated_coat_of_arms_reader_is_not_general_paradox_script_execution"),
                    false,
                    false,
                    false,
                    false);

    private CoatOfArmsSourceProbeCapabilityProfile() { }
}
