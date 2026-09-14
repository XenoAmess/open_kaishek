package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StewardDevelopCountyTaskActionCapabilityProfileTest {
    @Test
    void publicIdentityAndFixedTaskRequestArePinned() {
        assertEquals(
                "ck3_change_steward_develop_county_task_v1",
                StewardDevelopCountyTaskActionCapabilityProfile.CHANGE_TASK.id());
        assertEquals(
                "game.command.change-steward-develop-county-task-v1",
                StewardDevelopCountyTaskActionCapabilityProfile.CAPABILITY_ID);
        assertEquals(
                "change-steward-develop-county-task-v1",
                StewardDevelopCountyTaskActionCapabilityProfile.STEP_ID);
        assertEquals("task_develop_county",
                StewardDevelopCountyTaskActionCapabilityProfile.FIXED_TASK_KEY);
        assertEquals(
                List.of(
                        "councillor_character_id",
                        "target_county_title_id",
                        "expected_revision",
                        "replace_existing_task"),
                StewardDevelopCountyTaskActionCapabilityProfile.REQUEST_FIELDS);
    }

    @Test
    void acknowledgementCannotClaimAppliedOrSuccess() {
        assertEquals(
                List.of(
                        "submitted_verification_pending",
                        "rejected_before_submit"),
                StewardDevelopCountyTaskActionCapabilityProfile.ACK_STATUSES);
        assertFalse(
                StewardDevelopCountyTaskActionCapabilityProfile.ACK_FIELDS
                        .contains("applied"));
        assertFalse(
                StewardDevelopCountyTaskActionCapabilityProfile.ACK_FIELDS
                        .contains("success"));
        assertEquals(
                StewardDevelopCountyTaskActionCapabilityProfile.ACK_FIELDS,
                StewardDevelopCountyTaskActionCapabilityProfile.CHANGE_TASK
                        .requiredFields());
    }

    @Test
    void failureClassesAndReceiptAreClosedSeparately() {
        assertEquals(
                List.of(
                        "request_contract",
                        "snapshot_binding",
                        "councillor_binding",
                        "task_or_target_legality",
                        "native_command_dispatch"),
                StewardDevelopCountyTaskActionCapabilityProfile.FAILURE_CLASSES);
        assertEquals(
                List.of("applied", "rejected", "postcondition_failed"),
                StewardDevelopCountyTaskActionCapabilityProfile.RECEIPT_STATUSES);
        for (String field : List.of(
                "post_snapshot_revision",
                "post_native_snapshot_revision",
                "active_task_key",
                "target_county_title_id",
                "target_province_id",
                "progress_kind",
                "postcondition_verified")) {
            assertTrue(
                    StewardDevelopCountyTaskActionCapabilityProfile.RECEIPT_FIELDS
                            .contains(field),
                    field);
        }
    }

    @Test
    void candidateActionRemainsUnadvertisedAndUncertified() {
        var capability =
                StewardDevelopCountyTaskActionCapabilityProfile.CHANGE_TASK;

        assertFalse(capability.readOnly());
        assertFalse(capability.deterministic());
        assertFalse(capability.nativeCertified());
        assertFalse(capability.runtimeCertified());
        assertFalse(capability.certified());
        assertTrue(capability.invariants().contains(
                "production_capability_is_not_advertised"));
        assertTrue(capability.invariants().contains(
                "ack_never_proves_native_command_application"));
        assertTrue(capability.invariants().contains(
                "receipt_is_derived_from_an_independent_new_paused_snapshot"));
    }

    @Test
    void exactBuildAndPendingImplementationPairingAreExplicit() {
        assertEquals(1,
                StewardDevelopCountyTaskActionCapabilityProfile.SCHEMA_VERSION);
        assertEquals("1.19.0.6", Ck3Profile11906.GAME_VERSION);
        assertEquals(
                "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86",
                Ck3Profile11906.EXE_SHA256);
        assertEquals(
                "b2ac42c3c42ce742e5f16ce3bcc007840f99a07c",
                StewardDevelopCountyTaskActionCapabilityProfile.UPSTREAM_CONTRACT_COMMIT);
        assertEquals(
                "pending_upstream_devact2_commit",
                StewardDevelopCountyTaskActionCapabilityProfile.UPSTREAM_PAIRING_STATUS);
    }
}
