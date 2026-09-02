package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Small CK3 1.19.0.6 schema-only fixture for the native
 * {@code has_innovation} trigger shape.
 *
 * <p>The fixture exercises scalar innovation-key membership on a Culture
 * scope.  It deliberately stops at parsing and schema validation: the native
 * evaluator and ownership reader are pinned by static evidence, but no
 * runtime certification is implied.</p>
 */
public final class Ck3HasInnovationFixture {
    public static final String FIXTURE_ID = "ck3-has-innovation-trigger-11906";
    public static final String SOURCE_PATH =
            "common/scripted_triggers/ck3_has_innovation_fixture.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String OPCODE = "has_innovation";
    public static final String EVALUATOR_RVA = "0x282CE90";
    public static final String EVALUATOR_BYTES_LENGTH = "134";
    public static final String EVALUATOR_BYTES_SHA256 =
            "6EBC1F53ED9E1F045A88B5EA1628297A9029BA01A076085ABDFC6013980C0F79";
    public static final String INNOVATION_DATABASE_RVA = "0x9A6690";
    public static final String INNOVATION_DATABASE_GLOBAL = "0x570C7A8";
    public static final String CULTURE_OWNED_DATA_OFFSET = "0x758";
    public static final String CULTURE_OWNED_COUNT_OFFSET = "0x764";
    public static final String MEMBERSHIP_HELPER_RVA = "0x9A3C20";
    public static final String STABLE_KEY_OFFSET = "0x18";
    public static final String FALLBACK_GLOBAL = "0x57C04E0";
    public static final List<String> REQUIRED_KEYS = List.of(
            "innovation_quilted_armor", "innovation_sarawit", "innovation_legionnaires",
            "innovation_arched_saddle", "innovation_valets", "innovation_tiefutu",
            "innovation_advanced_bowmaking", "innovation_repeating_crossbow",
            "innovation_war_camels", "innovation_elephantry", "innovation_gunpowder",
            "innovation_fire_medicine");

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private Ck3HasInnovationFixture() { }

    /** Render a deterministic UTF-8-BOM CK3 source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 schema-only has-innovation trigger fixture\n"
                + "ck3_has_innovation_fixture = {\n"
                + "    limit = {\n"
                + "        has_innovation = innovation_quilted_armor\n"
                + "        has_innovation = innovation_war_camels\n"
                + "    }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
