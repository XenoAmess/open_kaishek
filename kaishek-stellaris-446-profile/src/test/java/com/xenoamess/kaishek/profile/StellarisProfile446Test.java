package com.xenoamess.kaishek.profile;

import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

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
        assertEquals(ScriptDomain.SCRIPTED_TRIGGERS,
                profile.domainForPath("common/scripted_triggers/vivhite_workplace_triggers.txt"));
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

    @Test
    void arkshipCompatibilityTriggerAndDecisionCallValidate() {
        String trigger = "vivhite_workplace_supported_colony = {\n"
                + "  OR = {\n"
                + "    is_planet_class = pc_ark\n"
                + "    owner = { is_nomadic = no }\n"
                + "  }\n"
                + "}\n";
        var parsedTrigger = Parser.parse(trigger.getBytes(StandardCharsets.UTF_8));
        var triggerDiagnostics = Validator.validate(parsedTrigger,
                "common/scripted_triggers/vivhite_workplace_triggers.txt", profile);
        assertTrue(triggerDiagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                triggerDiagnostics::toString);

        String decision = "test_decision = {\n"
                + "  owned_planets_only = yes\n"
                + "  potential = { vivhite_workplace_supported_colony = yes }\n"
                + "  effect = { add_deposit = test_deposit }\n"
                + "}\n";
        var parsedDecision = Parser.parse(decision.getBytes(StandardCharsets.UTF_8));
        var decisionDiagnostics = Validator.validate(parsedDecision,
                "common/decisions/workplace.txt", profile);
        assertTrue(decisionDiagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                decisionDiagnostics::toString);
    }

    @Test
    void unknownOperationInsideRegisteredTriggerDeclarationFailsClosed() {
        String source = "vivhite_workplace_supported_colony = { invented_trigger = yes }\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/vivhite_workplace_triggers.txt", profile);

        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("invented_trigger")),
                diagnostics::toString);
    }

    @Test
    void planetFlagMenuToggleDecisionShapesValidate() {
        String source = "decision_extend_workplace_expand = {\n"
                + "  owned_planets_only = yes\n"
                + "  enactment_time = 0\n"
                + "  potential = {\n"
                + "    NOT = { has_planet_flag = vivhite_workplace_menu_expanded }\n"
                + "  }\n"
                + "  effect = { set_planet_flag = vivhite_workplace_menu_expanded }\n"
                + "}\n"
                + "decision_extend_workplace_collapse = {\n"
                + "  owned_planets_only = yes\n"
                + "  enactment_time = 0\n"
                + "  potential = { has_planet_flag = vivhite_workplace_menu_expanded }\n"
                + "  effect = { remove_planet_flag = vivhite_workplace_menu_expanded }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/decisions/workplace.txt", profile);

        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
    }

    @Test
    void misspelledPlanetFlagOperationFailsClosed() {
        String source = "test_decision = {\n"
                + "  effect = { set_planet_flags = vivhite_workplace_menu_expanded }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/decisions/workplace.txt", profile);

        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("set_planet_flags")),
                diagnostics::toString);
    }
}
