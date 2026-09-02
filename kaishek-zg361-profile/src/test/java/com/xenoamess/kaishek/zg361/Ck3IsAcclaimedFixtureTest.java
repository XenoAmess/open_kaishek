package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Contract test for the exact-build is-acclaimed schema fixture. */
class Ck3IsAcclaimedFixtureTest {
    @Test
    void fixtureIsUtf8BomAndSchemaValid() {
        byte[] source = Ck3IsAcclaimedFixture.render();
        assertArrayEquals(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf},
                java.util.Arrays.copyOf(source, 3));
        ParseResult parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(),
                () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed, Ck3IsAcclaimedFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains("is_acclaimed = yes"));
        assertTrue(text.contains("is_acclaimed = no"));
    }

    @Test
    void fixtureMetadataPinsTheStaticCharacterReaderContract() {
        assertEquals("ck3-is-acclaimed-trigger-11906", Ck3IsAcclaimedFixture.FIXTURE_ID);
        assertEquals(Ck3Profile11906.ID, Ck3IsAcclaimedFixture.PROFILE_ID);
        assertEquals("0x434FAB8", Ck3IsAcclaimedFixture.TRIGGER_LITERAL_RVA);
        assertEquals("0x52C0E0", Ck3IsAcclaimedFixture.FACTORY_START_RVA);
        assertEquals("0x52C169", Ck3IsAcclaimedFixture.FACTORY_END_RVA);
        assertEquals("0x43506D0", Ck3IsAcclaimedFixture.FACTORY_VTABLE_RVA);
        assertEquals("0x281A610", Ck3IsAcclaimedFixture.FACTORY_CREATOR_RVA);
        assertEquals("0x4350BE0", Ck3IsAcclaimedFixture.COMPILED_VTABLE_RVA);
        assertEquals("0x28190C0", Ck3IsAcclaimedFixture.ACTUAL_STATE_LEAF_RVA);
        assertEquals("0x570C130", Ck3IsAcclaimedFixture.CHARACTER_STORE_GLOBAL);
        assertEquals("0x570C138", Ck3IsAcclaimedFixture.CHARACTER_FALLBACK_GLOBAL);
        assertEquals("0x1A8", Ck3IsAcclaimedFixture.CHARACTER_ACCOLADE_LINK_OFFSET);
        assertEquals("0x568", Ck3IsAcclaimedFixture.LINK_ACCOLADE_ID_OFFSET);
        assertEquals("0x57BF1E0", Ck3IsAcclaimedFixture.ACCOLADE_STORE_GLOBAL);
        assertEquals("0x57BF198", Ck3IsAcclaimedFixture.ACCOLADE_FALLBACK_GLOBAL);
        assertEquals("0x08", Ck3IsAcclaimedFixture.ACCOLADE_IDENTITY_OFFSET);
        assertEquals("0x4314698", Ck3IsAcclaimedFixture.ACCOLADE_VTABLE_RVA);
        assertEquals("0x10495A0", Ck3IsAcclaimedFixture.ACCOLADE_VCALL_TARGET);
        assertEquals("-1", Ck3IsAcclaimedFixture.NO_ACCOLADE_SENTINEL);
    }
}
