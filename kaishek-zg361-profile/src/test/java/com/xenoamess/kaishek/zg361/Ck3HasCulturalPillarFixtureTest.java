package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Contract test for the exact-build cultural-pillar schema fixture. */
class Ck3HasCulturalPillarFixtureTest {
    @Test
    void fixtureIsUtf8BomAndSchemaValid() {
        byte[] source = Ck3HasCulturalPillarFixture.render();
        assertArrayEquals(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf},
                java.util.Arrays.copyOf(source, 3));
        ParseResult parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(),
                () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed, Ck3HasCulturalPillarFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains("has_cultural_pillar = heritage_north_germanic"));
        assertTrue(text.contains("has_cultural_pillar = ethos_bellicose"));
    }

    @Test
    void fixtureMetadataPinsTheStaticReaderContract() {
        assertEquals("ck3-has-cultural-pillar-trigger-11906",
                Ck3HasCulturalPillarFixture.FIXTURE_ID);
        assertEquals(Ck3Profile11906.ID, Ck3HasCulturalPillarFixture.PROFILE_ID);
        assertEquals("0x282D900", Ck3HasCulturalPillarFixture.EVALUATOR_RVA);
        assertEquals("0x282D97B", Ck3HasCulturalPillarFixture.EVALUATOR_END_RVA);
        assertEquals("124", Ck3HasCulturalPillarFixture.EVALUATOR_BYTES_LENGTH);
        assertEquals("7B8B0972F9A0324E408E4B9DE88C209BF9CE52409091BA934303BF8596D28054",
                Ck3HasCulturalPillarFixture.EVALUATOR_BYTES_SHA256);
        assertEquals("0x190", Ck3HasCulturalPillarFixture.CULTURE_PILLAR_DATA_OFFSET);
        assertEquals("0x19C", Ck3HasCulturalPillarFixture.CULTURE_PILLAR_COUNT_OFFSET);
        assertEquals("0x1610", Ck3HasCulturalPillarFixture.PILLAR_CATEGORY_OFFSET);
        assertEquals("0x18", Ck3HasCulturalPillarFixture.STABLE_KEY_OFFSET);
        assertEquals("5", Ck3HasCulturalPillarFixture.EXPECTED_CATEGORY_COUNT);
        assertTrue(Ck3HasCulturalPillarFixture.REPRESENTATIVE_KEYS.contains(
                "heritage_north_germanic"));
    }
}
