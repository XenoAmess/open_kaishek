package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Contract test for the exact-build cultural-parameter schema fixture. */
class Ck3HasCulturalParameterFixtureTest {
    @Test
    void fixtureIsUtf8BomAndSchemaValid() {
        byte[] source = Ck3HasCulturalParameterFixture.render();
        assertArrayEquals(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf},
                java.util.Arrays.copyOf(source, 3));
        ParseResult parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(),
                () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                Ck3HasCulturalParameterFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains(
                "has_cultural_parameter = knights_slightly_more_prone_to_injury"));
        assertTrue(text.contains("has_cultural_parameter = unlock_zhanmadao"));
    }

    @Test
    void fixtureMetadataPinsTheStaticReaderContract() {
        assertEquals("ck3-has-cultural-parameter-trigger-11906",
                Ck3HasCulturalParameterFixture.FIXTURE_ID);
        assertEquals(Ck3Profile11906.ID, Ck3HasCulturalParameterFixture.PROFILE_ID);
        assertEquals("0x282DBD0", Ck3HasCulturalParameterFixture.EVALUATOR_RVA);
        assertEquals("0x22C5800",
                Ck3HasCulturalParameterFixture.CULTURE_PARAMETER_HELPER_RVA);
        assertEquals("0x3B588E0",
                Ck3HasCulturalParameterFixture.SCRIPT_IDENTIFIER_LOOKUP_RVA);
        assertEquals("0x3B58970",
                Ck3HasCulturalParameterFixture.SCRIPT_IDENTIFIER_NAME_RVA);
        assertEquals("12", Ck3HasCulturalParameterFixture.IDENTIFIER_MISSING_SENTINEL);
        assertTrue(Ck3HasCulturalParameterFixture.REQUIRED_KEYS.contains(
                "knights_slightly_more_prone_to_injury"));
        assertTrue(Ck3HasCulturalParameterFixture.REQUIRED_KEYS.contains(
                "unlock_zhanmadao"));
    }
}
