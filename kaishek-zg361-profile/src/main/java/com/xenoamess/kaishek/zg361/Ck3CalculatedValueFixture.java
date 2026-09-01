package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;

/**
 * Small CK3 1.19.0.6 schema-only regression fixture for calculated values.
 *
 * <p>The two range comparisons are the supported shape observed in the
 * native loader.  The final direct equality intentionally preserves the
 * ZhongGuo phase-two RED ({@code Unknown trigger: value/add/subtract}) so an
 * offline preflight can identify it without claiming runtime semantics.</p>
 */
public final class Ck3CalculatedValueFixture {
    public static final String FIXTURE_ID = "ck3-calculated-value-014";
    public static final String SOURCE_PATH =
            "common/scripted_effects/zg361_calculated_value_fixture.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String EXPECTED_DIAGNOSTIC =
            "CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED";

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private Ck3CalculatedValueFixture() { }

    /** Render a deterministic UTF-8-BOM CK3 source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 schema-only calculated-value loader fixture\n"
                + "zg361_calculated_value_fixture = {\n"
                + "    limit = {\n"
                + "        var:zg361_fixture_value >= { value = var:zg361_fixture_base add = 1 }\n"
                + "        var:zg361_fixture_value <= { value = var:zg361_fixture_base subtract = 1 }\n"
                + "        var:zg361_fixture_value = { value = var:zg361_fixture_base add = 1 subtract = 2 }\n"
                + "    }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
