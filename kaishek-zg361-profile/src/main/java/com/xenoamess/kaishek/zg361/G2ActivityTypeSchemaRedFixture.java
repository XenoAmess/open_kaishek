package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Small negative fixture for the current G2 activity-type schema boundary.
 *
 * <p>The companion {@code zg361_jingcha.txt} corpus parses successfully, but
 * the Phase 0 profile does not yet model activity-type-owned keys such as
 * {@code province_filter} and {@code phases}. This fixture preserves that
 * distinction as an explicit RED validator result. It is deliberately not a
 * profile update: adding guessed activity vocabulary before exact-build
 * evidence would hide the real coverage gap.</p>
 */
public final class G2ActivityTypeSchemaRedFixture {
    public static final String FIXTURE_ID = "ck3-g2-activity-type-schema-red-11906";
    public static final String ALIAS = "g2-activity-type-schema-red";
    public static final String SOURCE_PATH =
            "common/activities/activity_types/zg361_jingcha.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String EXE_SHA256 = Ck3Profile11906.EXE_SHA256;
    public static final String EXPECTED_DIAGNOSTIC = "UNKNOWN_OPCODE";

    /** Activity-owned keys intentionally kept outside the Phase 0 vocabulary. */
    public static final List<String> UNSUPPORTED_ACTIVITY_KEYS = List.of(
            "province_filter",
            "phases",
            "on_start",
            "on_complete",
            "guest_invite_rules");

    /** Full companion-corpus observation captured by the parent preflight. */
    public static final String OBSERVED_CORPUS_SHA256 =
            "28e681358558f5e975fae911bf5ddf54eacb3c95dac3133852eaaeca6425c284";
    public static final int OBSERVED_CORPUS_VALIDATOR_DIAGNOSTICS = 172255;

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private G2ActivityTypeSchemaRedFixture() { }

    /** Render a deterministic UTF-8-BOM activity-type source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 G2 activity-type schema boundary fixture\n"
                + "activity_zg361_jingcha = {\n"
                + "    province_filter = capital\n"
                + "    phases = { }\n"
                + "    on_start = { }\n"
                + "    on_complete = { }\n"
                + "    guest_invite_rules = { }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
