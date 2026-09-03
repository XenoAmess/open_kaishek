package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/** Contract test for the deliberate G2 activity-type schema RED boundary. */
class G2ActivityTypeSchemaRedFixtureTest {
    @Test
    void fixtureParsesButKeepsActivityVocabularyRed() {
        byte[] source = G2ActivityTypeSchemaRedFixture.render();
        assertArrayEquals(new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf},
                java.util.Arrays.copyOf(source, 3));

        ParseResult parsed = Parser.parse(source);
        assertTrue(parsed.diagnostics().isEmpty(),
                () -> parsed.diagnostics().toString());

        var diagnostics = Validator.validate(parsed,
                G2ActivityTypeSchemaRedFixture.SOURCE_PATH,
                new Ck3Profile11906());
        assertEquals(G2ActivityTypeSchemaRedFixture.UNSUPPORTED_ACTIVITY_KEYS.size(),
                diagnostics.size(), diagnostics::toString);
        assertTrue(diagnostics.stream().allMatch(d ->
                        d.code().equals(G2ActivityTypeSchemaRedFixture.EXPECTED_DIAGNOSTIC)
                                && d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);

        Set<String> paths = diagnostics.stream()
                .map(Diagnostic::path)
                .map(path -> path.substring(path.lastIndexOf('.') + 1))
                .collect(Collectors.toCollection(HashSet::new));
        assertEquals(Set.copyOf(G2ActivityTypeSchemaRedFixture.UNSUPPORTED_ACTIVITY_KEYS),
                paths);
    }

    @Test
    void metadataPinsTheCurrentBoundaryWithoutClaimingLiveSemantics() {
        assertEquals("ck3-g2-activity-type-schema-red-11906",
                G2ActivityTypeSchemaRedFixture.FIXTURE_ID);
        assertEquals("g2-activity-type-schema-red",
                G2ActivityTypeSchemaRedFixture.ALIAS);
        assertEquals(Ck3Profile11906.ID, G2ActivityTypeSchemaRedFixture.PROFILE_ID);
        assertEquals("1.19.0.6", G2ActivityTypeSchemaRedFixture.GAME_VERSION);
        assertEquals(Ck3Profile11906.EXE_SHA256,
                G2ActivityTypeSchemaRedFixture.EXE_SHA256);
        assertEquals("UNKNOWN_OPCODE",
                G2ActivityTypeSchemaRedFixture.EXPECTED_DIAGNOSTIC);
        assertEquals(172255,
                G2ActivityTypeSchemaRedFixture.OBSERVED_CORPUS_VALIDATOR_DIAGNOSTICS);
        assertEquals(64,
                G2ActivityTypeSchemaRedFixture.OBSERVED_CORPUS_SHA256.length());
        assertTrue(G2ActivityTypeSchemaRedFixture.UNSUPPORTED_ACTIVITY_KEYS
                .contains("province_filter"));
        assertTrue(G2ActivityTypeSchemaRedFixture.UNSUPPORTED_ACTIVITY_KEYS
                .contains("guest_invite_rules"));
    }
}
