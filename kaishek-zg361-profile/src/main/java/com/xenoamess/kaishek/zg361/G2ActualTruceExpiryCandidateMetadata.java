package com.xenoamess.kaishek.zg361;

/**
 * Source pins and negative readiness boundaries for the default-off G2
 * persisted truce-expiry candidate and its retention ticket.
 *
 * <p>This is metadata only. The default adapter does not advertise the query,
 * so this class deliberately exposes no {@code CapabilityDescriptor}.</p>
 */
public final class G2ActualTruceExpiryCandidateMetadata {
    public static final String ID =
            "ck3-1.19.0.6-g2-actual-truce-expiry-candidate-v1";
    public static final String CAPABILITY_ID =
            "game.command.query-raiktor-actual-truce-expiry-v1-N";
    public static final String STEP_PREFIX =
            "query-raiktor-actual-truce-expiry-v1-";
    public static final String BACKEND_ID =
            "ck3-1.19.0.6-native-raiktor-actual-truce-expiry-v1";
    public static final String CMAKE_OPTION =
            "XAR_CK3_ENABLE_G2_ACTUAL_TRUCE_EXPIRY_CANDIDATE_V1";
    public static final String GAME_VERSION = "1.19.0.6";
    public static final String EXECUTABLE_SHA256 =
            "2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86";
    public static final String ROOT_INTEGRATION_COMMIT =
            "04c1a00f0599378dfa8810be14ce535b2ed17f21";
    public static final String ROOT_RETENTION_COMMIT =
            "f16cdf0d63df06f4e6b0bbde08f6324e25c3d885";
    public static final String ROOT_SOURCE_CONTRACT_SHA256 =
            "9b71a5001453970df851e7b0d908929f5b598a0efceafc9ed7438a4d3bb214a3";
    public static final String ROOT_ABI_SHA256 =
            "422352b9989259f6f9060d47b5763c5e81b355cc36130124652d5a60cd78b6a7";
    public static final String ROOT_PYTHON_CONTRACT_SHA256 =
            "55fe165e5c001cd6858c4c47345fc8343dc270c9d77b7db9678e2d5596cf405c";
    public static final String ROOT_HEADER_SHA256 =
            "43b90ee1f4ebaae83785c786e9c38d9a7396bab07858f0ccd702165181572747";
    public static final String ROOT_SOURCE_SHA256 =
            "9074c83277697f1f652fb7216bda4647694ac1ef545bcd5f483055afec4c747c";
    public static final String RETENTION_MANIFEST_SHA256 =
            "21d5e530df76d80ec5919f536276bcb0340a607d0c83dbbd73e6451b724d5e91";
    public static final String RETENTION_RUNNER_SHA256 =
            "ea4b81a037c59b20f64e3e6800e9bec7e4de8ec856c1d0029639edf620498e04";
    public static final String RETENTION_TICKET_ID =
            "E0A93DDC584BB2313BC03CE076779BAFD261ABBABB69E9DE3BEF284DFE14823A";
    public static final String FROZEN_GENERATION_SHA256 =
            "6BD3E54354B267F9E785DE6FB2C2B3CB16AB72ADEF53204D2DB67299A857313F";
    public static final int RETAINED_PRE_TERMINATION_SOLDIERS = 598;
    public static final int RETAINED_EVALUATED_DAYS = 1825;
    public static final boolean DEFAULT_ENABLED = false;
    public static final boolean CAPABILITY_ADVERTISED_BY_DEFAULT = false;
    public static final boolean READ_ONLY = true;
    public static final boolean ACK_SUFFICIENT = false;
    public static final boolean NATIVE_CERTIFIED = false;
    public static final boolean RUNTIME_CERTIFIED = false;
    public static final boolean PRODUCTION_LIVE = false;
    public static final boolean RETENTION_LIVE_AUTHORIZED = false;
    public static final boolean TERMINATION_ACTION_BOUND = false;
    public static final boolean ACTUAL_EXPIRY_OBSERVABLE = false;
    public static final boolean DECISION_READY = false;
    public static final boolean AUTOMATIC_SURRENDER_READY = false;
    public static final boolean GEN_034_RESOLVED = false;

    private G2ActualTruceExpiryCandidateMetadata() { }
}
