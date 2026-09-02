package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Contract test for the exact-build has-innovation schema fixture. */
class Ck3HasInnovationFixtureTest {
    @Test
    void fixtureIsUtf8BomAndSchemaValid() {
        byte[] source = Ck3HasInnovationFixture.render();
        assertArrayEquals(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf},
                java.util.Arrays.copyOf(source, 3));
        ParseResult parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(),
                () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed, Ck3HasInnovationFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains("has_innovation = innovation_quilted_armor"));
        assertTrue(text.contains("has_innovation = innovation_war_camels"));
    }

    @Test
    void fixtureMetadataPinsTheStaticReaderContract() {
        assertEquals("ck3-has-innovation-trigger-11906", Ck3HasInnovationFixture.FIXTURE_ID);
        assertEquals(Ck3Profile11906.ID, Ck3HasInnovationFixture.PROFILE_ID);
        assertEquals("0x282CE90", Ck3HasInnovationFixture.EVALUATOR_RVA);
        assertEquals("134", Ck3HasInnovationFixture.EVALUATOR_BYTES_LENGTH);
        assertEquals("6EBC1F53ED9E1F045A88B5EA1628297A9029BA01A076085ABDFC6013980C0F79",
                Ck3HasInnovationFixture.EVALUATOR_BYTES_SHA256);
        assertEquals("0x758", Ck3HasInnovationFixture.CULTURE_OWNED_DATA_OFFSET);
        assertEquals("0x764", Ck3HasInnovationFixture.CULTURE_OWNED_COUNT_OFFSET);
        assertEquals("0x9A3C20", Ck3HasInnovationFixture.MEMBERSHIP_HELPER_RVA);
        assertEquals("0x18", Ck3HasInnovationFixture.STABLE_KEY_OFFSET);
        assertEquals("0x57C04E0", Ck3HasInnovationFixture.FALLBACK_GLOBAL);
        assertTrue(Ck3HasInnovationFixture.REQUIRED_KEYS.contains(
                "innovation_quilted_armor"));
        assertTrue(Ck3HasInnovationFixture.REQUIRED_KEYS.contains(
                "innovation_fire_medicine"));
    }
}
