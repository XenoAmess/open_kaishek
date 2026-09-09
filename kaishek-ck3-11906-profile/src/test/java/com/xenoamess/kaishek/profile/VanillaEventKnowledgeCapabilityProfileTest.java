package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VanillaEventKnowledgeCapabilityProfileTest {
    @Test
    void publicV1ShapeIsPinnedExactly() {
        var capability = VanillaEventKnowledgeCapabilityProfile.QUERY;

        assertEquals(
                "ck3_query_vanilla_event_knowledge_v1",
                capability.id());
        assertEquals(
                "ck3-1.19.0.6-vanilla-event-knowledge-v1",
                capability.profileVersion());
        assertEquals(
                List.of("event_definition_key", "ck3_build"),
                VanillaEventKnowledgeCapabilityProfile.REQUEST_FIELDS);
        assertEquals(
                List.of(
                        "schema",
                        "schema_version",
                        "status",
                        "event_definition_key",
                        "ck3_build",
                        "ck3_exe_sha256",
                        "contract",
                        "analysis",
                        "observations",
                        "unavailable_reason"),
                capability.requiredFields());
        assertEquals(
                "xar.ck3.vanilla-event-knowledge",
                VanillaEventKnowledgeCapabilityProfile.SCHEMA);
        assertEquals(1, VanillaEventKnowledgeCapabilityProfile.SCHEMA_VERSION);
    }

    @Test
    void offlineQueryIsReadOnlyButNotNativeOrRuntimeCertified() {
        var capability = VanillaEventKnowledgeCapabilityProfile.QUERY;

        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
        assertEquals(
                List.of(
                        "query_does_not_touch_gameplay_driver_or_ck3",
                        "request_accepts_only_event_definition_key_and_ck3_build",
                        "unsupported_build_is_typed_unavailable",
                        "invalid_or_unregistered_event_key_is_typed_unavailable",
                        "returned_payload_is_detached_and_json_safe",
                        "unknown_mcp_arguments_are_rejected"),
                capability.invariants());
    }

    @Test
    void exactBuildAndCompanionSourcesAreHashBound() {
        assertEquals(
                "1.19.0.6",
                Ck3Profile11906.GAME_VERSION);
        assertEquals(
                "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86",
                Ck3Profile11906.EXE_SHA256);
        assertEquals(
                "493c4bf9ece11e0b2de01ea03fe39e296c6d5e2d52f2fb5d27baabb3a834bbb9",
                VanillaEventKnowledgeCapabilityProfile.ROOT_REGISTRY_SHA256);
        assertEquals(
                "d1490c78446a8f0bb725ab977cecf7acd6c198a6cf040d48dffeaf300a1fa9ae",
                VanillaEventKnowledgeCapabilityProfile.ROOT_MCP_SERVER_SHA256);
    }
}
