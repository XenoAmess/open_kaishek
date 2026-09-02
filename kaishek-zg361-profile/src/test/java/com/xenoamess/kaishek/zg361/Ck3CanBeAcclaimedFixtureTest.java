package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Contract test for the exact-build can-be-acclaimed schema fixture. */
class Ck3CanBeAcclaimedFixtureTest {
    @Test
    void fixtureIsUtf8BomAndSchemaValid() {
        byte[] source = Ck3CanBeAcclaimedFixture.render();
        assertArrayEquals(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf},
                java.util.Arrays.copyOf(source, 3));
        ParseResult parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(),
                () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                Ck3CanBeAcclaimedFixture.SOURCE_PATH, new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains("can_be_acclaimed = yes"));
        assertTrue(text.contains("can_be_acclaimed = no"));
    }

    @Test
    void fixtureMetadataPinsTheStaticCharacterReaderContract() {
        assertEquals("ck3-can-be-acclaimed-trigger-11906",
                Ck3CanBeAcclaimedFixture.FIXTURE_ID);
        assertEquals(Ck3Profile11906.ID, Ck3CanBeAcclaimedFixture.PROFILE_ID);
        assertEquals("0x434FB90", Ck3CanBeAcclaimedFixture.TRIGGER_LITERAL_RVA);
        assertEquals("0x52C220", Ck3CanBeAcclaimedFixture.FACTORY_START_RVA);
        assertEquals("0x52C2B3", Ck3CanBeAcclaimedFixture.FACTORY_END_RVA);
        assertEquals("0x4350750", Ck3CanBeAcclaimedFixture.FACTORY_VTABLE_RVA);
        assertEquals("0x281A6F0", Ck3CanBeAcclaimedFixture.FACTORY_CREATOR_RVA);
        assertEquals("0x4350DB0", Ck3CanBeAcclaimedFixture.COMPILED_VTABLE_RVA);
        assertEquals("0x2819200", Ck3CanBeAcclaimedFixture.ACTUAL_STATE_LEAF_RVA);
        assertEquals("0x28A4870", Ck3CanBeAcclaimedFixture.QUALIFICATION_EVALUATOR_RVA);
        assertEquals("0x570C130", Ck3CanBeAcclaimedFixture.CHARACTER_STORE_GLOBAL);
        assertEquals("0x570C138", Ck3CanBeAcclaimedFixture.CHARACTER_FALLBACK_GLOBAL);
        assertEquals("0x18", Ck3CanBeAcclaimedFixture.CHARACTER_IDENTITY_OFFSET);
        assertEquals("0x1457C2060", Ck3CanBeAcclaimedFixture.RULES_SINGLETON_ABSOLUTE);
        assertEquals("0x570C030", Ck3CanBeAcclaimedFixture.ACCOLADE_TYPE_DB_GLOBAL);
        assertEquals("0x57EB620", Ck3CanBeAcclaimedFixture.OWNER_SCOPE_KEY_ID_GLOBAL);
    }
}
