package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class G2WarBoundLossCandidateMetadataTest {
    @Test
    void pinsDefaultOffFrozenSetContractWithoutAdvertisingCapability() {
        assertEquals(
                "714be35d1e4420889e92775be10d7826b2508da1",
                G2WarBoundLossCandidateMetadata.ROOT_INTEGRATION_COMMIT);
        assertEquals(
                "2911ed72b6179a5c8a19649deedfa919d235beb1",
                G2WarBoundLossCandidateMetadata.ROOT_CANDIDATE_COMMIT);
        assertEquals(
                "6b85024d6964dd715d88f502c5d21bc6987a7debad2a309955e25f3334ddc991",
                G2WarBoundLossCandidateMetadata.ROOT_STATIC_ARTIFACT_SHA256);
        assertEquals(
                "d97b47faca7d161983f960eace025bc1b8ef948ac198c3aaca12d71c3375da15",
                G2WarBoundLossCandidateMetadata.ROOT_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "2da1310487fcf5dfe175ed491fa82eb8b46195bec3919d1b8021f32c4cdc5e7f",
                G2WarBoundLossCandidateMetadata.ROOT_HEADER_SHA256);
        assertEquals(
                "4a1fb99b5d668a0b9fcbadbc2d1a37876b27ca6ee542dcdc62fb03ae361df7fc",
                G2WarBoundLossCandidateMetadata.ROOT_SOURCE_SHA256);
        assertEquals(598,
                G2WarBoundLossCandidateMetadata.FROZEN_PRE_TERMINATION_SOLDIERS);
        assertEquals(0,
                G2WarBoundLossCandidateMetadata.DESTROYED_POST_TERMINATION_SOLDIERS);
        assertEquals(598,
                G2WarBoundLossCandidateMetadata.PROVEN_BOUNDARY_SOLDIERS_LOST);
        assertFalse(G2WarBoundLossCandidateMetadata.DEFAULT_ENABLED);
        assertTrue(G2WarBoundLossCandidateMetadata.READ_ONLY);
        assertFalse(G2WarBoundLossCandidateMetadata.PUBLIC_CAPABILITY_ADDED);
        assertFalse(G2WarBoundLossCandidateMetadata.PUBLIC_WIRE_CHANGED);
    }

    @Test
    void keepsUnprovedCausalityAndReadinessClosed() {
        assertFalse(
                G2WarBoundLossCandidateMetadata.SOURCE_SPECIFIC_ATTRIBUTION_READY);
        assertFalse(G2WarBoundLossCandidateMetadata.TERMINATION_ACTION_BOUND);
        assertFalse(G2WarBoundLossCandidateMetadata.SURRENDER_CAUSALITY_PROVEN);
        assertFalse(G2WarBoundLossCandidateMetadata.PUBLIC_TERMS_READY);
        assertFalse(G2WarBoundLossCandidateMetadata.AUTOMATIC_SURRENDER_READY);
        assertFalse(G2WarBoundLossCandidateMetadata.PRODUCTION_LIVE);
        assertFalse(G2WarBoundLossCandidateMetadata.GEN_034_RESOLVED);
    }
}
