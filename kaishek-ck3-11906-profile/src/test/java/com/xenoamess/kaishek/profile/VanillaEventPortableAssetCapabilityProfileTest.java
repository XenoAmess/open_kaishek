package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VanillaEventPortableAssetCapabilityProfileTest {
    @Test
    void fourAdditiveMcpToolsAndRequestsArePinnedExactly() {
        assertEquals(
                List.of(
                        "ck3_list_vanilla_event_knowledge_v1",
                        "ck3_list_vanilla_event_evidence_v1",
                        "ck3_read_vanilla_event_evidence_v1",
                        "ck3_query_vanilla_event_source_provenance_v1"),
                List.of(
                        VanillaEventPortableAssetCapabilityProfile.LIST_KNOWLEDGE.id(),
                        VanillaEventPortableAssetCapabilityProfile.LIST_EVIDENCE.id(),
                        VanillaEventPortableAssetCapabilityProfile.READ_EVIDENCE.id(),
                        VanillaEventPortableAssetCapabilityProfile.QUERY_SOURCE_PROVENANCE.id()));
        assertEquals(
                List.of("ck3_build", "query", "namespace", "evidence_class",
                        "has_observations", "after_key", "limit"),
                VanillaEventPortableAssetCapabilityProfile.LIST_KNOWLEDGE_REQUEST_FIELDS);
        assertEquals(
                List.of("event_definition_key", "kind", "after_evidence_id", "limit", "ck3_build"),
                VanillaEventPortableAssetCapabilityProfile.LIST_EVIDENCE_REQUEST_FIELDS);
        assertEquals(
                List.of("evidence_id", "offset", "max_bytes", "ck3_build"),
                VanillaEventPortableAssetCapabilityProfile.READ_EVIDENCE_REQUEST_FIELDS);
        assertEquals(
                List.of("key", "build"),
                VanillaEventPortableAssetCapabilityProfile.QUERY_SOURCE_PROVENANCE_REQUEST_FIELDS);
    }

    @Test
    void allFourCapabilitiesAreOfflineReadOnlyAndUncertified() {
        for (var capability : List.of(
                VanillaEventPortableAssetCapabilityProfile.LIST_KNOWLEDGE,
                VanillaEventPortableAssetCapabilityProfile.LIST_EVIDENCE,
                VanillaEventPortableAssetCapabilityProfile.READ_EVIDENCE,
                VanillaEventPortableAssetCapabilityProfile.QUERY_SOURCE_PROVENANCE)) {
            assertTrue(capability.readOnly());
            assertTrue(capability.deterministic());
            assertFalse(capability.nativeCertified());
            assertFalse(capability.runtimeCertified());
            assertFalse(capability.certified());
            assertTrue(capability.invariants().contains(
                    "query_does_not_touch_gameplay_driver_or_ck3"));
            assertTrue(capability.invariants().contains(
                    "unknown_mcp_arguments_are_rejected"));
        }
    }

    @Test
    void schemasAndProviderInputsAreHashBound() {
        assertEquals(1, VanillaEventPortableAssetCapabilityProfile.SCHEMA_VERSION);
        assertEquals(
                "fdf23f537d4238850154f85184f503ba63136a3d",
                VanillaEventPortableAssetCapabilityProfile.ROOT_PROVIDER_COMMIT);
        assertEquals(
                "FE3A1D8472B14A6901117243A21E7A97E5AD60EE685D03057D5F17BC568F43DC",
                VanillaEventPortableAssetCapabilityProfile.ROOT_SOURCE_INDEX_DATASET_SHA256);
        for (String hash : List.of(
                VanillaEventPortableAssetCapabilityProfile.ROOT_MCP_SERVER_SHA256,
                VanillaEventPortableAssetCapabilityProfile.ROOT_DISCOVERY_SHA256,
                VanillaEventPortableAssetCapabilityProfile.ROOT_PORTABLE_EVIDENCE_SHA256,
                VanillaEventPortableAssetCapabilityProfile.ROOT_SOURCE_INDEX_SHA256,
                VanillaEventPortableAssetCapabilityProfile.ROOT_PORTABLE_EVIDENCE_MANIFEST_SHA256,
                VanillaEventPortableAssetCapabilityProfile.ROOT_SOURCE_INDEX_DATASET_SHA256,
                VanillaEventPortableAssetCapabilityProfile.KNOWLEDGE_INDEX_SCHEMA_SHA256,
                VanillaEventPortableAssetCapabilityProfile.EVIDENCE_LIST_SCHEMA_SHA256,
                VanillaEventPortableAssetCapabilityProfile.EVIDENCE_READ_SCHEMA_SHA256,
                VanillaEventPortableAssetCapabilityProfile.SOURCE_PROVENANCE_SCHEMA_SHA256)) {
            assertTrue(hash.matches("[0-9A-F]{64}"));
        }
        assertEquals(
                "xar.ck3.vanilla-event-knowledge-index",
                VanillaEventPortableAssetCapabilityProfile.KNOWLEDGE_INDEX_SCHEMA);
        assertEquals(
                "xar.ck3.vanilla-event-evidence-list",
                VanillaEventPortableAssetCapabilityProfile.EVIDENCE_LIST_SCHEMA);
        assertEquals(
                "xar.ck3.vanilla-event-evidence-read",
                VanillaEventPortableAssetCapabilityProfile.EVIDENCE_READ_SCHEMA);
        assertEquals(
                "xar.ck3.vanilla-event-source-provenance",
                VanillaEventPortableAssetCapabilityProfile.SOURCE_PROVENANCE_SCHEMA);
    }

    @Test
    void readAndSourceResponseSafetyFieldsRemainVisible() {
        assertTrue(VanillaEventPortableAssetCapabilityProfile.READ_EVIDENCE.requiredFields()
                .contains("historical_artifact_may_contain_nonportable_locators"));
        assertTrue(VanillaEventPortableAssetCapabilityProfile.QUERY_SOURCE_PROVENANCE.requiredFields()
                .contains("caller_candidates_are_lexical_only"));
        assertTrue(VanillaEventPortableAssetCapabilityProfile.QUERY_SOURCE_PROVENANCE.requiredFields()
                .contains("caller_candidates_review_status"));
    }
}
