package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Small CK3 1.19.0.6 schema-only fixture for the native
 * {@code has_cultural_parameter} trigger shape.
 *
 * <p>The fixture exercises scalar culture-parameter identifiers on a Culture
 * scope.  It deliberately stops at parsing and schema validation: the native
 * evaluator and identifier helper are pinned by exact-build static evidence,
 * but no runtime certification is implied.</p>
 */
public final class Ck3HasCulturalParameterFixture {
    public static final String FIXTURE_ID = "ck3-has-cultural-parameter-trigger-11906";
    public static final String SOURCE_PATH =
            "common/scripted_triggers/ck3_has_cultural_parameter_fixture.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String OPCODE = "has_cultural_parameter";
    public static final String EVALUATOR_RVA = "0x282DBD0";
    public static final String CULTURE_PARAMETER_HELPER_RVA = "0x22C5800";
    public static final String SCRIPT_IDENTIFIER_LOOKUP_RVA = "0x3B588E0";
    public static final String SCRIPT_IDENTIFIER_NAME_RVA = "0x3B58970";
    public static final String IDENTIFIER_MISSING_SENTINEL = "12";
    public static final String REPRESENTATIVE_KEY =
            "knights_slightly_more_prone_to_injury";
    public static final List<String> REQUIRED_KEYS = List.of(
            REPRESENTATIVE_KEY,
            "unlock_zhanmadao",
            "unlock_burenjia",
            "unlock_maa_cataphract_archers",
            "unlock_maa_black_armor_cavalry",
            "unlock_maa_horse_archers",
            "unlock_maa_mangudai",
            "unlock_emishi_horse_archers_units",
            "unlock_mounted_samurai_units");

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private Ck3HasCulturalParameterFixture() { }

    /** Render a deterministic UTF-8-BOM CK3 source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 schema-only cultural-parameter trigger fixture\n"
                + "ck3_has_cultural_parameter_fixture = {\n"
                + "    limit = {\n"
                + "        has_cultural_parameter = knights_slightly_more_prone_to_injury\n"
                + "        has_cultural_parameter = unlock_zhanmadao\n"
                + "    }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
