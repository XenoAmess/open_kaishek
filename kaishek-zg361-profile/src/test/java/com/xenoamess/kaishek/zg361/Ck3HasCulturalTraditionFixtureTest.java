package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Contract test for the exact-build cultural-tradition schema fixture. */
class Ck3HasCulturalTraditionFixtureTest {
    @Test
    void fixtureIsUtf8BomAndSchemaValid() {
        byte[] source = Ck3HasCulturalTraditionFixture.render();
        assertArrayEquals(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf},
                java.util.Arrays.copyOf(source, 3));
        ParseResult parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(),
                () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed, Ck3HasCulturalTraditionFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains(
                "has_cultural_tradition = tradition_fp1_coastal_warriors"));
        assertTrue(text.contains(
                "has_cultural_tradition = tradition_ep3_imperial_tagmata"));
    }

    @Test
    void fixtureMetadataPinsTheStaticReaderContract() {
        assertEquals("ck3-has-cultural-tradition-trigger-11906",
                Ck3HasCulturalTraditionFixture.FIXTURE_ID);
        assertEquals(Ck3Profile11906.ID, Ck3HasCulturalTraditionFixture.PROFILE_ID);
        assertEquals("0x282D990", Ck3HasCulturalTraditionFixture.EVALUATOR_RVA);
        assertEquals("376", Ck3HasCulturalTraditionFixture.EVALUATOR_BYTES_LENGTH);
        assertEquals("2073F14B5611955D899F21A35DD945239EE325C2EAD6652EBA1DBCBE5CAC78A9",
                Ck3HasCulturalTraditionFixture.EVALUATOR_BYTES_SHA256);
        assertEquals("0x9A66F0", Ck3HasCulturalTraditionFixture.TRADITION_DATABASE_RVA);
        assertEquals("0x570C7A0", Ck3HasCulturalTraditionFixture.TRADITION_DATABASE_GLOBAL);
        assertEquals("0xC8FC40", Ck3HasCulturalTraditionFixture.TRADITION_LOOKUP_RVA);
        assertEquals("0x3B8B000", Ck3HasCulturalTraditionFixture.HASH_LOOKUP_RVA);
        assertEquals("0x178", Ck3HasCulturalTraditionFixture.CULTURE_OWNED_DATA_OFFSET);
        assertEquals("0x184", Ck3HasCulturalTraditionFixture.CULTURE_OWNED_COUNT_OFFSET);
        assertEquals("0x9A3E60", Ck3HasCulturalTraditionFixture.MEMBERSHIP_HELPER_RVA);
        assertEquals("0x18", Ck3HasCulturalTraditionFixture.STABLE_KEY_OFFSET);
        assertEquals("0x57BF050", Ck3HasCulturalTraditionFixture.FALLBACK_GLOBAL);
        assertTrue(Ck3HasCulturalTraditionFixture.REQUIRED_KEYS.contains(
                "tradition_fp1_coastal_warriors"));
        assertTrue(Ck3HasCulturalTraditionFixture.REQUIRED_KEYS.contains(
                "tradition_ep3_imperial_tagmata"));
    }
}
