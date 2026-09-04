package com.xenoamess.kaishek.zg361;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class G2ActualTruceExpiryCandidateMetadataTest {
    @Test
    void pinsTheExactBuildAndCandidateSources() {
        assertEquals(
                "04c1a00f0599378dfa8810be14ce535b2ed17f21",
                G2ActualTruceExpiryCandidateMetadata.ROOT_INTEGRATION_COMMIT);
        assertEquals(
                "f16cdf0d63df06f4e6b0bbde08f6324e25c3d885",
                G2ActualTruceExpiryCandidateMetadata.ROOT_RETENTION_COMMIT);
        assertEquals(
                "9b71a5001453970df851e7b0d908929f5b598a0efceafc9ed7438a4d3bb214a3",
                G2ActualTruceExpiryCandidateMetadata
                        .ROOT_SOURCE_CONTRACT_SHA256);
        assertEquals(
                "422352b9989259f6f9060d47b5763c5e81b355cc36130124652d5a60cd78b6a7",
                G2ActualTruceExpiryCandidateMetadata.ROOT_ABI_SHA256);
        assertEquals(
                "55fe165e5c001cd6858c4c47345fc8343dc270c9d77b7db9678e2d5596cf405c",
                G2ActualTruceExpiryCandidateMetadata
                        .ROOT_PYTHON_CONTRACT_SHA256);
        assertEquals(
                "XAR_CK3_ENABLE_G2_ACTUAL_TRUCE_EXPIRY_CANDIDATE_V1",
                G2ActualTruceExpiryCandidateMetadata.CMAKE_OPTION);
    }

    @Test
    void pinsTheRetentionTicketWithoutPromotingIt() {
        assertEquals(
                "21d5e530df76d80ec5919f536276bcb0340a607d0c83dbbd73e6451b724d5e91",
                G2ActualTruceExpiryCandidateMetadata
                        .RETENTION_MANIFEST_SHA256);
        assertEquals(
                "E0A93DDC584BB2313BC03CE076779BAFD261ABBABB69E9DE3BEF284DFE14823A",
                G2ActualTruceExpiryCandidateMetadata.RETENTION_TICKET_ID);
        assertEquals(
                "6BD3E54354B267F9E785DE6FB2C2B3CB16AB72ADEF53204D2DB67299A857313F",
                G2ActualTruceExpiryCandidateMetadata
                        .FROZEN_GENERATION_SHA256);
        assertEquals(598,
                G2ActualTruceExpiryCandidateMetadata
                        .RETAINED_PRE_TERMINATION_SOLDIERS);
        assertEquals(1825,
                G2ActualTruceExpiryCandidateMetadata.RETAINED_EVALUATED_DAYS);
        assertFalse(
                G2ActualTruceExpiryCandidateMetadata
                        .RETENTION_LIVE_AUTHORIZED);
    }

    @Test
    void keepsEveryUnprovedReadinessBoundaryClosed() {
        assertTrue(G2ActualTruceExpiryCandidateMetadata.READ_ONLY);
        assertFalse(G2ActualTruceExpiryCandidateMetadata.DEFAULT_ENABLED);
        assertFalse(
                G2ActualTruceExpiryCandidateMetadata
                        .CAPABILITY_ADVERTISED_BY_DEFAULT);
        assertFalse(G2ActualTruceExpiryCandidateMetadata.ACK_SUFFICIENT);
        assertFalse(G2ActualTruceExpiryCandidateMetadata.NATIVE_CERTIFIED);
        assertFalse(G2ActualTruceExpiryCandidateMetadata.RUNTIME_CERTIFIED);
        assertFalse(G2ActualTruceExpiryCandidateMetadata.PRODUCTION_LIVE);
        assertFalse(
                G2ActualTruceExpiryCandidateMetadata.TERMINATION_ACTION_BOUND);
        assertFalse(
                G2ActualTruceExpiryCandidateMetadata.ACTUAL_EXPIRY_OBSERVABLE);
        assertFalse(G2ActualTruceExpiryCandidateMetadata.DECISION_READY);
        assertFalse(
                G2ActualTruceExpiryCandidateMetadata
                        .AUTOMATIC_SURRENDER_READY);
        assertFalse(G2ActualTruceExpiryCandidateMetadata.GEN_034_RESOLVED);
    }
}
