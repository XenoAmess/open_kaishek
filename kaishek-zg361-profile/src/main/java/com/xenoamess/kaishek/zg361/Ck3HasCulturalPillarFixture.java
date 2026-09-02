package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Small CK3 1.19.0.6 schema-only fixture for the native
 * {@code has_cultural_pillar} trigger shape.
 *
 * <p>The fixture exercises scalar pillar-key membership on a Culture scope.
 * It deliberately stops at parsing and schema validation: the native
 * category/span reader is pinned by static evidence, but no runtime
 * certification is implied.</p>
 */
public final class Ck3HasCulturalPillarFixture {
    public static final String FIXTURE_ID = "ck3-has-cultural-pillar-trigger-11906";
    public static final String SOURCE_PATH =
            "common/scripted_triggers/ck3_has_cultural_pillar_fixture.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String OPCODE = "has_cultural_pillar";
    public static final String EVALUATOR_RVA = "0x282D900";
    public static final String EVALUATOR_END_RVA = "0x282D97B";
    public static final String EVALUATOR_BYTES_LENGTH = "124";
    public static final String EVALUATOR_BYTES_SHA256 =
            "7B8B0972F9A0324E408E4B9DE88C209BF9CE52409091BA934303BF8596D28054";
    public static final String CULTURE_PILLAR_DATA_OFFSET = "0x190";
    public static final String CULTURE_PILLAR_COUNT_OFFSET = "0x19C";
    public static final String PILLAR_CATEGORY_OFFSET = "0x1610";
    public static final String STABLE_KEY_OFFSET = "0x18";
    public static final String EXPECTED_CATEGORY_COUNT = "5";
    public static final List<String> REPRESENTATIVE_KEYS = List.of(
            "heritage_north_germanic", "ethos_bellicose");

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private Ck3HasCulturalPillarFixture() { }

    /** Render a deterministic UTF-8-BOM CK3 source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 schema-only cultural-pillar trigger fixture\n"
                + "ck3_has_cultural_pillar_fixture = {\n"
                + "    limit = {\n"
                + "        has_cultural_pillar = heritage_north_germanic\n"
                + "        has_cultural_pillar = ethos_bellicose\n"
                + "    }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
