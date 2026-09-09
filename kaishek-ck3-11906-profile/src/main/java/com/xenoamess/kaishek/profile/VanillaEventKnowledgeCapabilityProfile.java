package com.xenoamess.kaishek.profile;

import java.util.List;

/**
 * Static compatibility projection for the companion vanilla-event knowledge query.
 *
 * <p>The companion owns the canonical Python registry and MCP server. This
 * descriptor freezes only the public, offline, read-only v1 request and
 * response boundary. It does not copy event records into open_kaishek, contact
 * a gameplay driver, register a Paradox opcode, or certify native/runtime
 * behavior.</p>
 */
public final class VanillaEventKnowledgeCapabilityProfile {
    public static final String ID =
            "ck3-1.19.0.6-vanilla-event-knowledge-v1";
    public static final String MCP_TOOL_ID =
            "ck3_query_vanilla_event_knowledge_v1";
    public static final String SCHEMA =
            "xar.ck3.vanilla-event-knowledge";
    public static final int SCHEMA_VERSION = 1;
    public static final String ROOT_REGISTRY_SHA256 =
            "493c4bf9ece11e0b2de01ea03fe39e296c6d5e2d52f2fb5d27baabb3a834bbb9";
    public static final String ROOT_MCP_SERVER_SHA256 =
            "d1490c78446a8f0bb725ab977cecf7acd6c198a6cf040d48dffeaf300a1fa9ae";

    public static final List<String> REQUEST_FIELDS = List.of(
            "event_definition_key",
            "ck3_build");

    public static final List<String> RESPONSE_FIELDS = List.of(
            "schema",
            "schema_version",
            "status",
            "event_definition_key",
            "ck3_build",
            "ck3_exe_sha256",
            "contract",
            "analysis",
            "observations",
            "unavailable_reason");

    public static final List<String> INVARIANTS = List.of(
            "query_does_not_touch_gameplay_driver_or_ck3",
            "request_accepts_only_event_definition_key_and_ck3_build",
            "unsupported_build_is_typed_unavailable",
            "invalid_or_unregistered_event_key_is_typed_unavailable",
            "returned_payload_is_detached_and_json_safe",
            "unknown_mcp_arguments_are_rejected");

    public static final CapabilityDescriptor QUERY =
            new CapabilityDescriptor(
                    MCP_TOOL_ID,
                    ID,
                    RESPONSE_FIELDS,
                    INVARIANTS,
                    true,
                    true,
                    false,
                    false);

    private VanillaEventKnowledgeCapabilityProfile() { }
}
