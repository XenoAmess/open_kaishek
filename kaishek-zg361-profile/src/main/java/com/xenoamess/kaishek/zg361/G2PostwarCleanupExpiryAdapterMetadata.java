package com.xenoamess.kaishek.zg361;

/**
 * Source pins and negative readiness boundaries for the fixture-only G2
 * postwar cleanup/actual-expiry receipt adapter.
 *
 * <p>This class is metadata only. The adapter is live-blocked because the
 * native cleanup reader has no runtime query dispatch, so this class exposes
 * no {@code CapabilityDescriptor}.</p>
 */
public final class G2PostwarCleanupExpiryAdapterMetadata {
    public static final String ID =
            "ck3-1.19.0.6-g2-postwar-cleanup-expiry-adapter-v1";
    public static final String MANIFEST_SCHEMA =
            "xar.ck3.g2_postwar_cleanup_expiry_adapter_manifest.v1";
    public static final String FIXTURE_SCHEMA =
            "xar.ck3.g2_postwar_cleanup_expiry_adapter_fixture.v1";
    public static final String STATUS =
            "GREEN_STATIC_ADAPTER_LIVE_BLOCKED_ON_CLEANUP_DISPATCH";
    public static final String ROOT_INTEGRATION_COMMIT =
            "a01f8cb684d39e2ea8e95fbf0f20f170b6f1a396";
    public static final String ROOT_SOURCE_COMMIT =
            "beb17743a6440650eec2ca9c0bf270733bce2527";
    public static final String ROOT_CANDIDATE_SOURCE_COMMIT =
            "04c1a00f0599378dfa8810be14ce535b2ed17f21";
    public static final String QUERY_STEP =
            "query-raiktor-actual-truce-expiry-v1-36769";
    public static final String RETENTION_TICKET_ID =
            "E0A93DDC584BB2313BC03CE076779BAFD261ABBABB69E9DE3BEF284DFE14823A";
    public static final String ROOT_RUNNER_SHA256 =
            "7d8a0e3ce8560b7153ec5e4a89407aa019d0ea20671254d1286351880ad5d80d";
    public static final String ROOT_MANIFEST_SHA256 =
            "7ce021720d0749288142040da5233f7bbdddd5a2f3b8ac187df1f72770b5a051";
    public static final String ROOT_FIXTURE_SHA256 =
            "5ce4b1d261b3c96123a7065947ce55f832e46d33cd69624a150c17d1f4a3e8ce";
    public static final String ROOT_PREFLIGHT_SHA256 =
            "da4b52f29cbb3dec478061877080c674ae11f08c80a13dbe353b6ca170fa6adf";
    public static final String ROOT_SYNTHETIC_RECEIPT_SHA256 =
            "66e09371be876f5bf4a8e97bccb8be8b0e19398894fa7b88a0056fdf2e49e4d1";
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
    public static final boolean CLEANUP_QUERY_DISPATCH_PRESENT = false;
    public static final boolean SAME_LIFECYCLE_NATIVE_CLEANUP_REQUIRED = true;
    public static final boolean OLD_WAR_ABSENCE_SUFFICIENT = false;
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
