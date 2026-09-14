package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFactionAlertsCapabilityProfileTest {
    @Test
    void publicIdentityRequestAndEnvelopeArePinned() {
        assertEquals(
                "ck3_query_player_faction_alerts_v1",
                PlayerFactionAlertsCapabilityProfile.QUERY.id());
        assertEquals(
                "game.command.query-player-faction-alerts-v1",
                PlayerFactionAlertsCapabilityProfile.CAPABILITY_ID);
        assertEquals(
                "query-player-faction-alerts-v1",
                PlayerFactionAlertsCapabilityProfile.STEP_ID);
        assertEquals(
                List.of("expected_revision"),
                PlayerFactionAlertsCapabilityProfile.REQUEST_FIELDS);
        assertEquals(
                List.of(
                        "step",
                        "accepted",
                        "status",
                        "query_sequence",
                        "snapshot_revision",
                        "player_faction_alerts",
                        "backend_id"),
                PlayerFactionAlertsCapabilityProfile.COMMAND_ENVELOPE_FIELDS);
        assertEquals(
                PlayerFactionAlertsCapabilityProfile.MCP_RESPONSE_FIELDS,
                PlayerFactionAlertsCapabilityProfile.QUERY.requiredFields());
    }

    @Test
    void payloadAndNestedRowsArePinnedExactly() {
        assertEquals(13, PlayerFactionAlertsCapabilityProfile.PAYLOAD_FIELDS.size());
        assertEquals(
                List.of(
                        "faction_id",
                        "faction_type_key",
                        "target_character_id",
                        "leader_character_id",
                        "leader_is_human",
                        "special_character_id",
                        "special_title_id",
                        "faction_at_war",
                        "faction_war_id",
                        "power",
                        "power_threshold",
                        "discontent",
                        "discontent_per_month",
                        "months_until_max_discontent",
                        "character_member_ids",
                        "county_member_title_ids",
                        "dangerous_by_stock_rule",
                        "danger_reason"),
                PlayerFactionAlertsCapabilityProfile.TARGETING_FACTION_FIELDS);
        assertEquals(
                List.of(
                        "county_title_id",
                        "faction_id",
                        "faction_type_key",
                        "target_character_id",
                        "power",
                        "power_threshold",
                        "dangerous_by_stock_rule",
                        "danger_reason"),
                PlayerFactionAlertsCapabilityProfile.COUNTY_EXPOSURE_FIELDS);
        assertEquals(8, PlayerFactionAlertsCapabilityProfile.PLANNER_PROJECTION_FIELDS.size());
        assertEquals(8, PlayerFactionAlertsCapabilityProfile.READINESS_FIELDS.size());
        assertEquals(
                List.of("targeting_rows", "county_exposure"),
                PlayerFactionAlertsCapabilityProfile.COMPONENT_UNAVAILABLE_REASON_FIELDS);
        assertEquals(
                List.of("game_version", "executable_sha256", "backend_id"),
                PlayerFactionAlertsCapabilityProfile.PROVENANCE_FIELDS);
    }

    @Test
    void strictPartialAndUnavailableVocabulariesArePinned() {
        assertEquals(
                List.of(
                        "unsupported_build",
                        "requires_application_main",
                        "requires_paused",
                        "state_changed",
                        "reader_not_implemented"),
                PlayerFactionAlertsCapabilityProfile.UNAVAILABLE_REASONS);
        assertEquals(
                List.of(
                        "targeting_rows_native_reader_not_frozen",
                        "county_exposure_native_reader_not_frozen"),
                PlayerFactionAlertsCapabilityProfile.PARTIAL_REASONS);
        assertTrue(PlayerFactionAlertsCapabilityProfile.TARGETING_COUNT_PRODUCTION_REUSED);
        assertFalse(PlayerFactionAlertsCapabilityProfile.TARGETING_ROWS_NATIVE_READY);
        assertFalse(PlayerFactionAlertsCapabilityProfile.COUNTY_EXPOSURE_NATIVE_READY);
        assertFalse(PlayerFactionAlertsCapabilityProfile.EXACT_ULTIMATUM_TIMING_READY);
        assertEquals(100_000, PlayerFactionAlertsCapabilityProfile.FIXED_POINT_SCALE);
        assertTrue(PlayerFactionAlertsCapabilityProfile.QUERY.invariants().contains(
                "player_faction_alerts_ready_mirrors_readiness_alert_ready"));
        assertTrue(PlayerFactionAlertsCapabilityProfile.QUERY.invariants().contains(
                "planner_projection_is_unavailable_until_alert_ready"));
    }

    @Test
    void candidateIsReadOnlyButDoesNotClaimFullNativeOrRuntimeReadiness() {
        var capability = PlayerFactionAlertsCapabilityProfile.QUERY;

        assertTrue(capability.readOnly());
        assertTrue(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
        assertTrue(capability.invariants().contains(
                "targeting_count_reuses_the_production_campaign_root_reader"));
        assertTrue(capability.invariants().contains(
                "unready_targeting_rows_are_empty_and_use_the_frozen_partial_reason"));
        assertTrue(capability.invariants().contains(
                "query_does_not_authorize_faction_or_war_mutation"));
    }

    @Test
    void exactBuildAndPendingUpstreamPairingAreExplicit() {
        assertEquals("1.19.0.6", Ck3Profile11906.GAME_VERSION);
        assertEquals(
                "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86",
                Ck3Profile11906.EXE_SHA256);
        assertEquals(1, PlayerFactionAlertsCapabilityProfile.SCHEMA_VERSION);
        assertEquals(
                "ck3-1.19.0.6-native-player-faction-alerts-v1",
                PlayerFactionAlertsCapabilityProfile.BACKEND_ID);
        assertEquals(
                "PAIRING_PENDING",
                PlayerFactionAlertsCapabilityProfile.UPSTREAM_PROVIDER_COMMIT);
        assertEquals(
                "pending_upstream_main",
                PlayerFactionAlertsCapabilityProfile.UPSTREAM_PAIRING_STATUS);
    }
}
