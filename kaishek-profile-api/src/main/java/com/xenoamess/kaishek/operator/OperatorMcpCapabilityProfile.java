package com.xenoamess.kaishek.operator;

import com.xenoamess.kaishek.profile.CapabilityDescriptor;

import java.util.List;

/** Static consumer contract for the portable target-side operator MCP. */
public final class OperatorMcpCapabilityProfile {
    public static final String CONTRACT_VERSION = "1.0.0";
    public static final int PROFILE_SCHEMA_VERSION = 1;
    public static final String ROOT_PROVIDER_COMMIT =
            "e684fd05f94290d58e9fb7009046eb95d60468f4";
    public static final String ROOT_IMPLEMENTATION_SHA256 =
            "8CFB6AACD81854BF44FFCEC228D3CD91C6222262513467759231D0D3E6DC5BA5";
    public static final String ROOT_SERVER_ENTRYPOINT_SHA256 =
            "43F7B451A8EBE63A4C3C89B3516343DEB0F7CD6D6ED30A40623653E41A089834";
    public static final String ROOT_PROFILE_EXAMPLE_SHA256 =
            "8B3EA7DAFACE3ABEB313813CB5A4984531E5B89FD516C43B8148858160AE397E";
    public static final String ROOT_CONTRACT_TEST_SHA256 =
            "6B539D809D835A1D16D2690DF9124D10B327B28EE37EDD05DF61FF16D0133CC1";

    public static final String GET_CAPABILITIES_TOOL = "operator_get_capabilities";
    public static final String GET_STATUS_TOOL = "operator_get_status";
    public static final String PREFLIGHT_JOB_TOOL = "operator_preflight_job";
    public static final String HANDOFF_JOB_TOOL = "operator_handoff_job";
    public static final List<String> TOOLS = List.of(
            GET_CAPABILITIES_TOOL,
            GET_STATUS_TOOL,
            PREFLIGHT_JOB_TOOL,
            HANDOFF_JOB_TOOL);

    public static final List<String> GET_CAPABILITIES_REQUEST_FIELDS = List.of();
    public static final List<String> GET_STATUS_REQUEST_FIELDS = List.of("target_id");
    public static final List<String> PREFLIGHT_JOB_REQUEST_FIELDS =
            List.of("target_id", "job_name");
    public static final List<String> HANDOFF_JOB_REQUEST_FIELDS =
            List.of("target_id", "job_name", "request_id");

    public static final CapabilityDescriptor GET_CAPABILITIES =
            new CapabilityDescriptor(
                    GET_CAPABILITIES_TOOL,
                    "operator-mcp-capabilities-1.0.0",
                    List.of(
                            "schema_version", "server_version", "server_instance_id",
                            "target_id", "display_name", "profile_sha256", "endpoint",
                            "jobs", "tools", "caller_supplied_commands",
                            "operator_bootstrap_required"),
                    List.of(
                            "read_only_tool",
                            "target_endpoint_identity_and_jobs_come_from_profile",
                            "caller_supplied_commands_are_false",
                            "operator_bootstrap_is_explicit"),
                    true, false, false, false);

    public static final CapabilityDescriptor GET_STATUS =
            new CapabilityDescriptor(
                    GET_STATUS_TOOL,
                    "operator-mcp-status-1.0.0",
                    List.of(
                            "schema_version", "server_instance_id", "target_id", "identity",
                            "identity_matches_profile", "process_gates",
                            "process_gate_errors", "jobs"),
                    List.of(
                            "read_only_tool",
                            "target_id_is_caller_selected_from_deployment_configuration",
                            "token_desktop_machine_and_process_state_are_observed_not_inferred"),
                    true, false, false, false);

    public static final CapabilityDescriptor PREFLIGHT_JOB =
            new CapabilityDescriptor(
                    PREFLIGHT_JOB_TOOL,
                    "operator-mcp-preflight-job-1.0.0",
                    List.of(
                            "schema_version", "result", "target_id", "job_name",
                            "profile_sha256", "job_spec_sha256", "checks", "observations",
                            "process_gate_errors", "running_job_ids", "failed_checks",
                            "evidence_sha256"),
                    List.of(
                            "read_only_tool",
                            "job_name_selects_only_a_profile_frozen_command",
                            "identity_paths_and_exclusive_processes_are_rechecked",
                            "green_or_red_is_evidence_hash_bound"),
                    true, false, false, false);

    public static final CapabilityDescriptor HANDOFF_JOB =
            new CapabilityDescriptor(
                    HANDOFF_JOB_TOOL,
                    "operator-mcp-handoff-job-1.0.0",
                    List.of(
                            "schema_version", "result", "idempotent_replay", "target_id",
                            "profile_sha256", "job"),
                    List.of(
                            "caller_cannot_supply_a_command_or_working_directory",
                            "new_request_repeats_preflight_inside_the_handoff_call",
                            "request_id_is_portable_and_idempotent_per_server_instance",
                            "same_request_id_cannot_be_rebound_to_another_job",
                            "handoff_is_non_destructive_but_may_start_an_open_world_process"),
                    false, false, false, false);

    private OperatorMcpCapabilityProfile() { }
}
