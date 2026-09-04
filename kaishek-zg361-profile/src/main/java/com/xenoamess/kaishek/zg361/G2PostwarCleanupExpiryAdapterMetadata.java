package com.xenoamess.kaishek.zg361;

/**
 * Source pins and negative readiness boundaries for the private default-OFF
 * G2 postwar cleanup/actual-expiry receipt adapter.
 *
 * <p>This class is metadata only. The private cleanup query is now dispatched,
 * but has not run against CK3.  It therefore exposes no
 * {@code CapabilityDescriptor}.</p>
 */
public final class G2PostwarCleanupExpiryAdapterMetadata {
    public static final String ID =
            "ck3-1.19.0.6-g2-postwar-cleanup-expiry-adapter-v1";
    public static final String MANIFEST_SCHEMA =
            "xar.ck3.g2_postwar_cleanup_expiry_adapter_manifest.v1";
    public static final String FIXTURE_SCHEMA =
            "xar.ck3.g2_postwar_cleanup_expiry_adapter_fixture.v1";
    public static final String STATUS =
            "STATIC_READY_PRIVATE_DISPATCH_LIVE_NOT_RUN";
    public static final String ROOT_INTEGRATION_COMMIT =
            "ff89dcdbefb9d8fc86ce4722df847946e96d0e81";
    public static final String ROOT_SOURCE_COMMIT =
            "7aae7e064b6e224dd3a5b95070b54d9205c32cf4";
    public static final String ROOT_CANDIDATE_BASE_COMMIT =
            "a01f8cb684d39e2ea8e95fbf0f20f170b6f1a396";
    public static final String ROOT_ACTUAL_EXPIRY_SOURCE_COMMIT =
            "04c1a00f0599378dfa8810be14ce535b2ed17f21";
    public static final String QUERY_STEP =
            "query-raiktor-actual-truce-expiry-v1-36769";
    public static final String CLEANUP_CAPABILITY_ID =
            "game.command.query-raiktor-war-bound-loss-cleanup-v1-N";
    public static final String CLEANUP_STEP_PREFIX =
            "query-raiktor-war-bound-loss-cleanup-v1-";
    public static final String RETENTION_TICKET_ID =
            "E0A93DDC584BB2313BC03CE076779BAFD261ABBABB69E9DE3BEF284DFE14823A";
    public static final String ROOT_RUNNER_SHA256 =
            "40696417f3bbd841e79116612697654c48868c90534bdfc0fdf43e161fdb47c8";
    public static final String ROOT_MANIFEST_SHA256 =
            "7874094361e8de6b38f77441b1ff59f512afcd13c309e0ffd02147185e86375f";
    public static final String ROOT_FIXTURE_SHA256 =
            "5ce4b1d261b3c96123a7065947ce55f832e46d33cd69624a150c17d1f4a3e8ce";
    public static final String ROOT_CLEANUP_CONTRACT_SHA256 =
            "11c7b77842676251bdb1516472cb15d4115105d4a7b3ab5b994b3430f5f48c61";
    public static final String ROOT_CLEANUP_DISPATCH_SOURCE_CONTRACT_SHA256 =
            "1863e7b53d852b83f8fc3432e66c90eee72e73ecfbcbdacc1f48e47c232ab4d9";
    public static final String CANDIDATE_DLL_SHA256 =
            "5d366fa321da436601819e52827210defe42d1fe14950380d3d2722d6b992ff5";
    public static final String CANDIDATE_NATIVE_TEST_SHA256 =
            "f7999cca5ae9ac70e64515afce391049d9e318a9f32b701482fcf1e4996ffe88";
    public static final int WAR_ID = 50331699;
    public static final int PLAYER_CHARACTER_ID = 29829;
    public static final int PRIMARY_DEFENDER_CHARACTER_ID = 36769;
    public static final int PRE_TERMINATION_SOLDIERS = 598;
    public static final int POST_TERMINATION_SOLDIERS = 0;
    public static final int PROVEN_BOUNDARY_SOLDIERS_LOST = 598;
    public static final boolean METADATA_ONLY = true;
    public static final boolean DEFAULT_ENABLED = false;
    public static final boolean SYNTHETIC_FIXTURE = true;
    public static final boolean FIXTURE_IS_LIVE = false;
    public static final boolean PUBLIC_CAPABILITY_ADDED = false;
    public static final boolean ACTUAL_EXPIRY_QUERY_DISPATCH_PRESENT = true;
    public static final boolean CLEANUP_CANDIDATE_LIBRARY_PRESENT = true;
    public static final boolean CLEANUP_QUERY_DISPATCH_PRESENT = true;
    public static final boolean CLEANUP_QUERY_PRIVATE = true;
    public static final boolean CLEANUP_DISPATCH_LIVE_TESTED = false;
    public static final boolean SAME_LIFECYCLE_NATIVE_CLEANUP_REQUIRED = true;
    public static final boolean WAR_ID_ABSENCE_ADMISSION_ONLY = true;
    public static final boolean OLD_WAR_ABSENCE_SUFFICIENT = false;
    public static final boolean DESTROYED_RESULT_FROM_EXACT_STORES = true;
    public static final boolean EXTERNAL_CLEANUP_INJECTION_ALLOWED = false;
    public static final boolean ADAPTER_ISSUES_CLEANUP_QUERY = true;
    public static final boolean PYTHON_ADAPTER_MAY_INFER_CLEANUP = false;
    public static final boolean LIVE_AUTHORIZED = false;
    public static final boolean PUBLIC_READINESS_PROMOTED = false;
    public static final boolean ACTION_READINESS_PROMOTED = false;
    public static final boolean RUNTIME_CLEANUP_READY = false;
    public static final boolean SOURCE_SPECIFIC_ATTRIBUTION_READY = false;
    public static final boolean DECISION_READY = false;
    public static final boolean AUTOMATIC_SURRENDER_READY = false;
    public static final boolean GEN_034_RESOLVED = false;

    private G2PostwarCleanupExpiryAdapterMetadata() { }
}
