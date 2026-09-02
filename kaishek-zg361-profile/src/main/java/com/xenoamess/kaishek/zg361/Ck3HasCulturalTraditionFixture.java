package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Small CK3 1.19.0.6 schema-only fixture for the native
 * {@code has_cultural_tradition} trigger shape.
 *
 * <p>The fixture exercises scalar cultural-tradition-key membership on a
 * Culture scope. It deliberately stops at parsing and schema validation:
 * the native evaluator and ownership reader are pinned by static evidence,
 * but no runtime certification is implied.</p>
 */
public final class Ck3HasCulturalTraditionFixture {
    public static final String FIXTURE_ID = "ck3-has-cultural-tradition-trigger-11906";
    public static final String SOURCE_PATH =
            "common/scripted_triggers/ck3_has_cultural_tradition_fixture.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String OPCODE = "has_cultural_tradition";
    public static final String EVALUATOR_RVA = "0x282D990";
    public static final String EVALUATOR_BYTES_LENGTH = "376";
    public static final String EVALUATOR_BYTES_SHA256 =
            "2073F14B5611955D899F21A35DD945239EE325C2EAD6652EBA1DBCBE5CAC78A9";
    public static final String TRADITION_DATABASE_RVA = "0x9A66F0";
    public static final String TRADITION_DATABASE_GLOBAL = "0x570C7A0";
    public static final String TRADITION_LOOKUP_RVA = "0xC8FC40";
    public static final String HASH_LOOKUP_RVA = "0x3B8B000";
    public static final String CULTURE_OWNED_DATA_OFFSET = "0x178";
    public static final String CULTURE_OWNED_COUNT_OFFSET = "0x184";
    public static final String MEMBERSHIP_HELPER_RVA = "0x9A3E60";
    public static final String STABLE_KEY_OFFSET = "0x18";
    public static final String FALLBACK_GLOBAL = "0x57BF050";
    public static final List<String> REQUIRED_KEYS = List.of(
            "tradition_fp1_coastal_warriors", "tradition_hird", "tradition_futuwaa",
            "tradition_druzhina", "tradition_khadga_puja", "tradition_garuda_warriors",
            "tradition_himalayan_settlers", "tradition_mubarizuns",
            "tradition_burman_royal_army", "tradition_mountaineer_ruralism",
            "tradition_caucasian_wolves", "tradition_roman_legacy",
            "tradition_ep3_audacious_cadets", "tradition_ep3_imperial_tagmata");

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private Ck3HasCulturalTraditionFixture() { }

    /** Render a deterministic UTF-8-BOM CK3 source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 schema-only cultural-tradition trigger fixture\n"
                + "ck3_has_cultural_tradition_fixture = {\n"
                + "    limit = {\n"
                + "        has_cultural_tradition = tradition_fp1_coastal_warriors\n"
                + "        has_cultural_tradition = tradition_ep3_imperial_tagmata\n"
                + "    }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
