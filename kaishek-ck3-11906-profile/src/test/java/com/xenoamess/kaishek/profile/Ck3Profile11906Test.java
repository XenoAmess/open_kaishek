package com.xenoamess.kaishek.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Contract checks for the version-pinned profile projection. */
class Ck3Profile11906Test {
    private final Ck3Profile11906 profile = new Ck3Profile11906();

    @Test
    void exactBuildIdentityIsPinned() {
        assertEquals(Ck3Profile11906.ID, profile.id());
        assertEquals(Ck3Profile11906.GAME_VERSION, profile.gameVersion());
        assertEquals(Ck3Profile11906.EXE_SHA256, profile.executableSha256());
        // BuildFingerprint canonicalizes hashes to lowercase while the
        // published profile constant retains the documentation's uppercase
        // spelling.
        assertEquals(Ck3Profile11906.EXE_SHA256.toLowerCase(java.util.Locale.ROOT),
                profile.fingerprint().exeSha256());
        assertEquals("ck3", profile.fingerprint().gameId());
        assertEquals(Ck3Profile11906.GAME_VERSION, profile.fingerprint().gameVersion());
    }

    @Test
    void genericProjectionAndSchemaShareTheSameOpcodeIds() {
        assertEquals(profile.opcodes().size(), profile.opcodeRegistry().size());
        assertTrue(profile.opcode("set_variable") != null);
        assertTrue(profile.opcode("has_variable") != null);
        assertTrue(profile.opcode("has_game_rule") != null);
        assertTrue(profile.opcode("has_dlc_feature") != null);
        assertTrue(profile.opcode("government_has_flag") != null);
        assertTrue(profile.opcode("has_character_modifier") != null);
        assertTrue(profile.opcode("has_perk") != null);
        assertTrue(profile.opcode("has_dynasty_perk") != null);
        assertTrue(profile.opcode("has_court_position") != null);
        assertTrue(profile.opcode("war_days") != null);
        assertTrue(profile.opcodeRegistry().contains("set_variable"));
        assertEquals(Ck3Profile11906.GAME_VERSION,
                profile.opcodeRegistry().require("set_variable").profileVersion());
        assertEquals(profile.gameProfile().opcodes().size(), profile.opcodeRegistry().size());
        assertTrue(profile.certifiedSemantics().isEmpty(),
                "Phase 0 must not claim exact-build runtime certification");
        assertEquals(1, profile.opcodeRegistry().require("trigger_event").minParameters());
        assertEquals(2, profile.opcodeRegistry().require("trigger_event").maxParameters());
    }

    @Test
    void hasVariableIsPinnedAsScalarPresenceTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("has_variable");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.STRING, descriptor.inputType());
        assertEquals(ScopeType.THIS, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void hasGameRuleIsPinnedAsScalarSettingTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("has_game_rule");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.STRING, descriptor.inputType());
        assertEquals(ScopeType.THIS, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void hasDlcFeatureIsPinnedAsScalarFeatureTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("has_dlc_feature");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.STRING, descriptor.inputType());
        assertEquals(ScopeType.THIS, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void governmentHasFlagIsPinnedAsScalarMembershipTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("government_has_flag");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.STRING, descriptor.inputType());
        assertEquals(ScopeType.CHARACTER, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void hasCharacterModifierIsPinnedAsScalarMembershipTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("has_character_modifier");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.STRING, descriptor.inputType());
        assertEquals(ScopeType.CHARACTER, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void hasPerkIsPinnedAsScalarMembershipTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("has_perk");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.STRING, descriptor.inputType());
        assertEquals(ScopeType.CHARACTER, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void hasDynastyPerkIsPinnedAsScalarMembershipTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("has_dynasty_perk");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.STRING, descriptor.inputType());
        // A dedicated DYNASTY scope is intentionally not claimed yet; THIS
        // keeps the native current-scope requirement open for the future
        // scope-transition slice.
        assertEquals(ScopeType.THIS, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void hasCourtPositionIsPinnedAsScalarMembershipTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("has_court_position");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.STRING, descriptor.inputType());
        assertEquals(ScopeType.CHARACTER, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void warDaysIsPinnedAsScalarIntegerWarTriggerShape() {
        OpcodeDescriptor descriptor = profile.opcodeRegistry().require("war_days");
        assertEquals(OpcodeKind.TRIGGER, descriptor.kind());
        assertEquals(InputType.INTEGER, descriptor.inputType());
        assertEquals(ScopeType.WAR, descriptor.requiredScope());
        assertTrue(descriptor.parameterNames().isEmpty());
        assertEquals(0, descriptor.minParameters());
        assertEquals(0, descriptor.maxParameters());
        assertFalse(descriptor.certified(), "static evidence must not claim runtime certification");
    }

    @Test
    void schemaArityMatchesEveryTypedOpcodeDescriptor() {
        for (OpcodeDescriptor descriptor : profile.opcodeRegistry().all()) {
            OpcodeSpec spec = profile.opcode(descriptor.id());
            assertNotNull(spec, "schema is missing " + descriptor.id());
            assertEquals(descriptor.minParameters(), spec.minParameters(), descriptor.id());
            assertEquals(descriptor.maxParameters(), spec.maxParameters(), descriptor.id());
        }
    }

    @Test
    void directoryMappingIsCaseAndSeparatorIndependent() {
        assertEquals(ScriptDomain.SCRIPTED_EFFECTS,
                profile.domainForPath("COMMON\\SCRIPTED_EFFECTS\\xar.txt"));
        assertEquals(ScriptDomain.ON_ACTION,
                profile.domainForPath("common/on_actions/xar.txt"));
        assertEquals(ScriptDomain.SCRIPTED_VALUES,
                profile.domainForPath("common/script_values/xar.txt"));
        assertEquals(ScriptDomain.INTERACTIONS,
                profile.domainForPath("common/character_interactions/xar.txt"));
        assertEquals(ScriptDomain.GUI_REGISTRATION,
                profile.domainForPath("gui/zg361_scoreboard.gui"));
        assertEquals(ScriptDomain.UNKNOWN, profile.domainForPath(null));
    }

    @Test
    void exposedCollectionsAreImmutable() {
        assertThrows(UnsupportedOperationException.class,
                () -> profile.allowedStructuralKeys().add("unsafe"));
        assertThrows(UnsupportedOperationException.class,
                () -> profile.opcodes().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> profile.scopeLinks().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> profile.gameProfile().certifiedSemantics().add("fake"));
    }
}
