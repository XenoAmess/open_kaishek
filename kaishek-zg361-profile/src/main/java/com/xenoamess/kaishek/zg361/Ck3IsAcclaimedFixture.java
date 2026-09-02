package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;

import java.nio.charset.StandardCharsets;

/**
 * Small CK3 1.19.0.6 schema-only fixture for the native
 * {@code is_acclaimed} trigger shape.
 *
 * <p>The exact-build reader follows the current Character to active
 * CAccolade identity chain.  This fixture records only the Character boolean
 * syntax and static reader provenance; it deliberately does not model an
 * ACCOLADE scope or claim native/runtime certification.</p>
 */
public final class Ck3IsAcclaimedFixture {
    public static final String FIXTURE_ID = "ck3-is-acclaimed-trigger-11906";
    public static final String SOURCE_PATH =
            "common/scripted_triggers/ck3_is_acclaimed_fixture.txt";
    public static final String PROFILE_ID = Ck3Profile11906.ID;
    public static final String GAME_VERSION = Ck3Profile11906.GAME_VERSION;
    public static final String OPCODE = "is_acclaimed";
    public static final String TRIGGER_LITERAL_RVA = "0x434FAB8";
    public static final String FACTORY_START_RVA = "0x52C0E0";
    public static final String FACTORY_END_RVA = "0x52C169";
    public static final String FACTORY_VTABLE_RVA = "0x43506D0";
    public static final String FACTORY_CREATOR_RVA = "0x281A610";
    public static final String COMPILED_VTABLE_RVA = "0x4350BE0";
    public static final String ACTUAL_STATE_LEAF_RVA = "0x28190C0";
    public static final String CHARACTER_STORE_GLOBAL = "0x570C130";
    public static final String CHARACTER_FALLBACK_GLOBAL = "0x570C138";
    public static final String CHARACTER_IDENTITY_OFFSET = "0x18";
    public static final String CHARACTER_ACCOLADE_LINK_OFFSET = "0x1A8";
    public static final String LINK_ACCOLADE_ID_OFFSET = "0x568";
    public static final String ACCOLADE_STORE_GLOBAL = "0x57BF1E0";
    public static final String ACCOLADE_FALLBACK_GLOBAL = "0x57BF198";
    public static final String ACCOLADE_IDENTITY_OFFSET = "0x08";
    public static final String ACCOLADE_VTABLE_RVA = "0x4314698";
    public static final String ACCOLADE_VCALL_TARGET = "0x10495A0";
    public static final String NO_ACCOLADE_SENTINEL = "-1";

    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    private Ck3IsAcclaimedFixture() { }

    /** Render a deterministic UTF-8-BOM CK3 source slice. */
    public static byte[] render() {
        String body = "# CK3 1.19.0.6 schema-only is-acclaimed trigger fixture\n"
                + "ck3_is_acclaimed_fixture = {\n"
                + "    limit = {\n"
                + "        is_acclaimed = yes\n"
                + "        is_acclaimed = no\n"
                + "    }\n"
                + "}\n";
        byte[] encoded = body.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[BOM.length + encoded.length];
        System.arraycopy(BOM, 0, result, 0, BOM.length);
        System.arraycopy(encoded, 0, result, BOM.length, encoded.length);
        return result;
    }
}
