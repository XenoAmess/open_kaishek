package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;

/**
 * Small CK3 1.19.0.6 schema-only fixture for the native
 * {@code can_be_acclaimed} trigger shape.
 *
 * <p>The exact-build leaf enters a full acclaim-qualification evaluator.
 * This fixture records only the Character boolean syntax and static reader
 * provenance; it deliberately does not invoke the evaluator, construct an
 * accolade scope, or claim native/runtime certification.</p>
 */
public final class Ck3CanBeAcclaimedFixture {
    public static final String FIXTURE_ID = "ck3-can-be-acclaimed-trigger-11906";
    public static final String SOURCE_PATH =
            "common/scripted_triggers/ck3_can_be_acclaimed_fixture.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String OPCODE = "can_be_acclaimed";
    public static final String TRIGGER_LITERAL_RVA = "0x434FB90";
    public static final String FACTORY_START_RVA = "0x52C220";
    public static final String FACTORY_END_RVA = "0x52C2B3";
    public static final String FACTORY_VTABLE_RVA = "0x4350750";
    public static final String FACTORY_CREATOR_RVA = "0x281A6F0";
    public static final String COMPILED_VTABLE_RVA = "0x4350DB0";
    public static final String ACTUAL_STATE_LEAF_RVA = "0x2819200";
    public static final String QUALIFICATION_EVALUATOR_RVA = "0x28A4870";
    public static final String CHARACTER_STORE_GLOBAL = "0x570C130";
    public static final String CHARACTER_FALLBACK_GLOBAL = "0x570C138";
    public static final String CHARACTER_IDENTITY_OFFSET = "0x18";
    public static final String RULES_SINGLETON_ABSOLUTE = "0x1457C2060";
    public static final String ACCOLADE_TYPE_DB_GLOBAL = "0x570C030";
    public static final String OWNER_SCOPE_KEY_ID_GLOBAL = "0x57EB620";

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private Ck3CanBeAcclaimedFixture() { }

    /** Render a deterministic UTF-8-BOM CK3 source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 schema-only can-be-acclaimed trigger fixture\n"
                + "ck3_can_be_acclaimed_fixture = {\n"
                + "    limit = {\n"
                + "        can_be_acclaimed = yes\n"
                + "        can_be_acclaimed = no\n"
                + "    }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
