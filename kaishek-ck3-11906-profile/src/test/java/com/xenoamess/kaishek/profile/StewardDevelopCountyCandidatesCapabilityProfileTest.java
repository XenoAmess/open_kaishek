package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StewardDevelopCountyCandidatesCapabilityProfileTest {
    @Test
    void publicCandidateV1IdentityAndRequestArePinned() {
        assertEquals(
                "ck3_query_steward_develop_county_candidates_v1",
                StewardDevelopCountyCandidatesCapabilityProfile.QUERY.id());
        assertEquals(
                "game.command.query-steward-develop-county-candidates-v1",
                StewardDevelopCountyCandidatesCapabilityProfile.CAPABILITY_ID);
        assertEquals(
                "query-steward-develop-county-candidates-v1",
                StewardDevelopCountyCandidatesCapabilityProfile.STEP_ID);
        assertEquals(
                List.of("expected_revision"),
                StewardDevelopCountyCandidatesCapabilityProfile.REQUEST_FIELDS);
        assertEquals(1, StewardDevelopCountyCandidatesCapabilityProfile.SCHEMA_VERSION);
        assertEquals(
                "exact_build_contract_fixture_pending_live_reader",
                StewardDevelopCountyCandidatesCapabilityProfile.CONTRACT_STAGE);
    }

    @Test
    void payloadCandidateAndProvenanceFieldsArePinnedExactly() {
        assertEquals(21, StewardDevelopCountyCandidatesCapabilityProfile.PAYLOAD_FIELDS.size());
        assertEquals(
                List.of(
                        "county_title_id",
                        "capital_province_id",
                        "holder_character_id",
                        "is_player_capital",
                        "directly_held_by_player",
                        "native_legal",
                        "development_level_raw",
                        "development_progress_raw",
                        "monthly_development_rate_raw",
                        "max_development_level_raw",
                        "terrain_key",
                        "same_culture_as_player",
                        "cultural_acceptance_threshold_passed"),
                StewardDevelopCountyCandidatesCapabilityProfile.CANDIDATE_FIELDS);
        assertEquals(
                List.of(
                        "game_version",
                        "executable_sha256",
                        "backend_id",
                        "reader_mode",
                        "next_reverse_engineering_entry"),
                StewardDevelopCountyCandidatesCapabilityProfile.PROVENANCE_FIELDS);
        assertTrue(
                StewardDevelopCountyCandidatesCapabilityProfile.PAYLOAD_FIELDS
                        .contains("candidates"));
        assertTrue(
                StewardDevelopCountyCandidatesCapabilityProfile.PAYLOAD_FIELDS
                        .contains("readiness"));
    }

    @Test
    void mcpEnvelopeAndUnavailableVocabularyAreClosed() {
        assertEquals(
                List.of(
                        "step",
                        "accepted",
                        "status",
                        "query_sequence",
                        "snapshot_revision",
                        "steward_develop_county_candidates",
                        "backend_id"),
                StewardDevelopCountyCandidatesCapabilityProfile.COMMAND_ENVELOPE_FIELDS);
        assertEquals(
                "native-headless",
                StewardDevelopCountyCandidatesCapabilityProfile.COMMAND_BACKEND_ID);
        assertEquals(
                "ck3-1.19.0.6-native-steward-develop-county-candidates-v1",
                StewardDevelopCountyCandidatesCapabilityProfile.BACKEND_ID);
        assertEquals(
                List.of(
                        "reader_not_implemented",
                        "unsupported_build",
                        "requires_application_main",
                        "requires_paused",
                        "state_changed"),
                StewardDevelopCountyCandidatesCapabilityProfile.UNAVAILABLE_REASONS);
        assertEquals(
                StewardDevelopCountyCandidatesCapabilityProfile.MCP_RESPONSE_FIELDS,
                StewardDevelopCountyCandidatesCapabilityProfile.QUERY.requiredFields());
    }

    @Test
    void candidateProfileDoesNotClaimPendingNativeOrRuntimeCertification() {
        var capability = StewardDevelopCountyCandidatesCapabilityProfile.QUERY;

        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
        assertTrue(capability.invariants().contains(
                "production_reader_is_typed_unavailable_until_exact_build_abi_is_closed"));
        assertTrue(capability.invariants().contains(
                "unavailable_observed_date_is_null_or_same_frame_date"));
        assertTrue(capability.invariants().contains(
                "query_does_not_authorize_council_task_or_target_mutation"));
    }

    @Test
    void exactBuildAndPendingPairingAreExplicit() {
        assertEquals("1.19.0.6", Ck3Profile11906.GAME_VERSION);
        assertEquals(
                "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86",
                Ck3Profile11906.EXE_SHA256);
        assertEquals(
                "f0f10bf1863519881d9bd88c64a8ace4ec6abc41",
                StewardDevelopCountyCandidatesCapabilityProfile.UPSTREAM_PROVIDER_COMMIT);
        assertEquals(
                "paired_to_upstream_main",
                StewardDevelopCountyCandidatesCapabilityProfile.UPSTREAM_PAIRING_STATUS);
    }
}
