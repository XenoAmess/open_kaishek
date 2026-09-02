package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ZhongguoBusinessPostconditionFixtureTest {
    private static final byte[] BOM = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};

    @Test
    void projectsMetricsFixtureRoundTripsAndValidates() {
        byte[] source = ZhongguoProjectsMetricsPostconditionFixture.render();
        assertArrayEquals(BOM, Arrays.copyOf(source, 3));
        var parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(), parsed.diagnostics()::toString);
        assertArrayEquals(source, parsed.source());
        var diagnostics = Validator.validate(parsed,
                ZhongguoProjectsMetricsPostconditionFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(
                        diagnostic -> diagnostic.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains("zg361_cp_m26_contribution_receipt_id"));
        assertTrue(text.contains("zg361_p3_m229_source_contribution_receipt_revision"));
    }

    @Test
    void promotionCompensationFixtureRoundTripsAndValidates() {
        byte[] source = ZhongguoPromotionCompensationPostconditionFixture.render();
        assertArrayEquals(BOM, Arrays.copyOf(source, 3));
        var parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(), parsed.diagnostics()::toString);
        assertArrayEquals(source, parsed.source());
        var diagnostics = Validator.validate(parsed,
                ZhongguoPromotionCompensationPostconditionFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertTrue(diagnostics.stream().noneMatch(
                        diagnostic -> diagnostic.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        String text = new String(source, StandardCharsets.UTF_8);
        assertTrue(text.contains("zg361_pp_m147_receipt_serial"));
        assertTrue(text.contains("zg361_comp_promotion_receipt_choice_revision"));
        assertTrue(text.contains("zg361_comp_m082_visible_revision"));
        assertTrue(text.contains(
                "var:zg361_comp_promotion_receipt_revision > var:zg361_comp_promotion_receipt_choice_revision"));
    }

    @Test
    void fixtureCapabilitiesUseTheProfileApiAndRemainUncertified() {
        assertEquals(ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS.id(),
                ZhongguoProjectsMetricsPostconditionFixture.CAPABILITY_ID);
        assertEquals(ZhongguoBusinessPostconditionProfile.PROMOTION_COMPENSATION.id(),
                ZhongguoPromotionCompensationPostconditionFixture.CAPABILITY_ID);
        assertFalse(ZhongguoBusinessPostconditionProfile.PROJECTS_METRICS.certified());
        assertFalse(ZhongguoBusinessPostconditionProfile.PROMOTION_COMPENSATION.certified());
    }
}
