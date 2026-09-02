package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/** Contract test for the exact-build war-days schema fixture. */
class Ck3WarDaysFixtureTest {
    @Test
    void fixtureIsUtf8BomAndSchemaValid() {
        byte[] source = Ck3WarDaysFixture.render();
        assertArrayEquals(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf},
                java.util.Arrays.copyOf(source, 3));
        ParseResult parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(),
                () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed, Ck3WarDaysFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains("war_days >= 365"));
        assertTrue(text.contains("war_days < 9125"));
    }

    @Test
    void fixtureMetadataPinsTheStaticReaderContract() {
        assertEquals("ck3-war-days-trigger-11906", Ck3WarDaysFixture.FIXTURE_ID);
        assertEquals(Ck3Profile11906.ID, Ck3WarDaysFixture.PROFILE_ID);
        assertEquals("0x2848230", Ck3WarDaysFixture.EVALUATOR_RVA);
        assertEquals("0xE0", Ck3WarDaysFixture.WAR_START_OFFSET);
        assertEquals("24", Ck3WarDaysFixture.DATE_DIVISOR);
    }
}
