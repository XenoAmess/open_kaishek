package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;

/**
 * Small CK3 1.19.0.6 schema-only fixture for the native {@code war_days}
 * trigger shape.
 *
 * <p>The fixture exercises the scalar integer comparison forms used by the
 * stock war and interaction scripts.  It deliberately stops at parsing and
 * schema validation: the native evaluator is pinned by static evidence, but
 * no runtime certification is implied.</p>
 */
public final class Ck3WarDaysFixture {
    public static final String FIXTURE_ID = "ck3-war-days-trigger-11906";
    public static final String SOURCE_PATH =
            "common/scripted_triggers/ck3_war_days_fixture.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String OPCODE = "war_days";
    public static final String EVALUATOR_RVA = "0x2848230";
    public static final String WAR_START_OFFSET = "0xE0";
    public static final String DATE_DIVISOR = "24";

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private Ck3WarDaysFixture() { }

    /** Render a deterministic UTF-8-BOM CK3 source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 schema-only war-days trigger fixture\n"
                + "ck3_war_days_fixture = {\n"
                + "    limit = {\n"
                + "        war_days >= 365\n"
                + "        war_days < 9125\n"
                + "    }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
