package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StellarisProfile446Test {
    private final StellarisProfile446 profile = new StellarisProfile446();

    @Test
    void exactBuildIdentityIsPinned() {
        assertEquals("stellaris-4.4.6", profile.id());
        assertEquals("4.4.6", profile.gameVersion());
        assertEquals(64, profile.executableSha256().length());
        assertTrue(profile.opcodes().values().stream()
                .allMatch(spec -> spec.introducedIn().equals("4.4.6")));
    }

    @Test
    void decisionsAndDepositsHaveSeparateDomains() {
        assertEquals(ScriptDomain.DECISIONS,
                profile.domainForPath("COMMON\\DECISIONS\\workplace.txt"));
        assertEquals(ScriptDomain.DEPOSITS,
                profile.domainForPath("common/deposits/extend_workplace.txt"));
        assertEquals(ScriptDomain.UNKNOWN, profile.domainForPath("common/unknown/x.txt"));
    }

    @Test
    void currentCorpusShapesAreStaticOnlyAndFailClosed() {
        assertEquals(OpcodeSpec.Kind.EFFECT, profile.opcode("add_deposit").kind());
        assertEquals(OpcodeSpec.Kind.STRUCTURAL,
                profile.opcode("triggered_planet_modifier").kind());
        assertNull(profile.opcode("unobserved_stellaris_opcode"));
        assertThrows(UnsupportedOperationException.class,
                () -> profile.allowedStructuralKeys().add("unsafe"));
        assertThrows(UnsupportedOperationException.class,
                () -> profile.opcodes().clear());
    }
}
