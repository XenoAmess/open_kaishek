package com.xenoamess.kaishek.profile;

import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Set;

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
        assertTrue(profile.opcode("is_planet_class").allowedScopes().contains("SHIP"));
        assertTrue(profile.opcode("add_deposit").allowedScopes().contains("SHIP"));
        assertTrue(profile.opcode("vivhite_workplace_supported_colony")
                .allowedScopes().contains("SHIP"));
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
    void carrierFlagMenuToggleDecisionShapesValidateForPlanetAndShip() {
        String source = "decision_extend_workplace_expand = {\n"
                + "  owned_planets_only = yes\n"
                + "  enactment_time = 0\n"
                + "  potential = {\n"
                + "    NOT = { has_carrier_flag = vivhite_workplace_menu_expanded }\n"
                + "  }\n"
                + "  effect = { set_carrier_flag = vivhite_workplace_menu_expanded }\n"
                + "}\n"
                + "decision_extend_workplace_collapse = {\n"
                + "  owned_planets_only = yes\n"
                + "  enactment_time = 0\n"
                + "  potential = { has_carrier_flag = vivhite_workplace_menu_expanded }\n"
                + "  effect = { remove_carrier_flag = vivhite_workplace_menu_expanded }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/decisions/workplace.txt", profile);

        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
        assertEquals(Set.of("PLANET", "planet", "SHIP", "ship"),
                profile.opcode("has_carrier_flag").allowedScopes());
    }

    @Test
    void planetOnlyFlagSchemeAndMisspelledCarrierOperationFailClosed() {
        String source = "test_decision = {\n"
                + "  potential = { has_planet_flag = vivhite_workplace_menu_expanded }\n"
                + "  effect = { set_planet_flag = vivhite_workplace_menu_expanded\n"
                + "    remove_carrier_flags = vivhite_workplace_menu_expanded }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/decisions/workplace.txt", profile);

        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_planet_flag")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("set_planet_flag")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("remove_carrier_flags")),
                diagnostics::toString);
    }

    @Test
    void rogueServitorCivicGuardValidatesAndMalformedFormsFailClosed() {
        String valid = "test_decision = {\n"
                + "  potential = { owner = { has_valid_civic = civic_machine_servitor } }\n"
                + "  effect = { add_deposit = mod_extend_bio_trophy_workplace }\n"
                + "}\n";
        var parsedValid = Parser.parse(valid.getBytes(StandardCharsets.UTF_8));
        var validDiagnostics = Validator.validate(parsedValid,
                "common/decisions/workplace.txt", profile);

        assertTrue(validDiagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                validDiagnostics::toString);
        assertEquals(OpcodeSpec.Kind.TRIGGER, profile.opcode("has_valid_civic").kind());
        assertEquals(Set.of("COUNTRY", "country"),
                profile.opcode("has_valid_civic").allowedScopes());

        String invalid = "test_decision = { potential = { owner = {\n"
                + "  has_valid_civics = civic_machine_servitor\n"
                + "  has_valid_civic = { scope = PLANET value = civic_machine_servitor }\n"
                + "} } }\n";
        var parsedInvalid = Parser.parse(invalid.getBytes(StandardCharsets.UTF_8));
        var invalidDiagnostics = Validator.validate(parsedInvalid,
                "common/decisions/workplace.txt", profile);

        assertTrue(invalidDiagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_valid_civics")),
                invalidDiagnostics::toString);
        assertTrue(invalidDiagnostics.stream().anyMatch(d ->
                        d.code().equals("INVALID_SCOPE")
                                && d.message().contains("PLANET")),
                invalidDiagnostics::toString);
        assertTrue(invalidDiagnostics.stream().anyMatch(d ->
                        d.code().equals("INVALID_PARAMETERS")
                                && d.message().contains("has_valid_civic")),
                invalidDiagnostics::toString);
    }
}
