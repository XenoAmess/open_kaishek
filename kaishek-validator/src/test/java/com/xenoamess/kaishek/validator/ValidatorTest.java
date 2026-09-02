package com.xenoamess.kaishek.validator;

import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.syntax.Parser;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {
    private static final Ck3Profile11906 PROFILE = new Ck3Profile11906();
    @Test void validEffectHasNoSchemaErrors() throws Exception {
        var p = Parser.parse(Files.readString(Path.of("src/test/resources/fixtures/valid_effects.txt")));
        var ds = Validator.validate(p, "common/scripted_effects/xar.txt", PROFILE);
        assertTrue(ds.stream().noneMatch(d -> d.severity() == Diagnostic.Severity.ERROR), ds::toString);
    }
    @Test void mutationProducesStrictDiagnostics() throws Exception {
        var p = Parser.parse(Files.readString(Path.of("src/test/resources/fixtures/invalid_effects.txt")));
        var codes = Validator.validate(p, "common/scripted_effects/xar.txt", PROFILE).stream().map(Diagnostic::code).toList();
        assertTrue(codes.contains("DUPLICATE_KEY"));
        assertTrue(codes.contains("UNKNOWN_OPCODE"));
        assertTrue(codes.contains("WRONG_DOMAIN"));
    }

    @Test void profileApiOverloadPreservesParserDiagnostics() {
        var parsed = Parser.parse("broken = { value = 1".getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/scripted_effects/xar.txt", PROFILE.gameProfile());
        assertTrue(diagnostics.stream().anyMatch(d -> d.code().equals("UNCLOSED_BLOCK")), diagnostics::toString);
    }

    @Test void guiDeclarationHeadersDoNotBecomeUnknownOpcodes() {
        String source = "types ZG361ScoreboardTypes {\n"
                + "  blockoverride \"header_text\" { text = \"title\" }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed, "gui/zg361_scoreboard.gui", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d -> d.code().equals("UNKNOWN_OPCODE")), diagnostics::toString);
    }

    @Test void gameProfileAdapterKeepsEventEffectDomain() {
        String source = "trigger_event = { id = test_event days = 1 }\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/scripted_triggers/xar.txt",
                PROFILE.gameProfile());
        assertTrue(diagnostics.stream().anyMatch(d -> d.code().equals("WRONG_DOMAIN")),
                diagnostics::toString);
    }

    @Test void observedHasVariableScalarTriggerIsSchemaKnown() {
        String source = "fixture = {\n"
                + "  limit = { has_variable = zg361_case_state }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_variable")),
                diagnostics::toString);
    }

    @Test void observedHasGameRuleScalarTriggerIsSchemaKnown() {
        String source = "fixture = {\n"
                + "  limit = { has_game_rule = zg361_on }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_game_rule")),
                diagnostics::toString);
    }

    @Test void observedHasDlcFeatureScalarTriggerIsSchemaKnown() {
        String source = "fixture = {\n"
                + "  limit = { has_dlc_feature = royal_court }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_dlc_feature")),
                diagnostics::toString);
    }

    @Test void observedGovernmentHasFlagScalarTriggerIsSchemaKnown() {
        String source = "fixture = {\n"
                + "  limit = { government_has_flag = government_has_merit }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("government_has_flag")),
                diagnostics::toString);
    }

    @Test void observedHasCharacterModifierScalarTriggerIsSchemaKnown() {
        String source = "fixture = {\n"
                + "  limit = { has_character_modifier = ai_extreme_conqueror_modifier }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_character_modifier")),
                diagnostics::toString);
    }

    @Test void observedHasPerkScalarTriggerIsSchemaKnown() {
        String source = "fixture = {\n"
                + "  limit = { has_perk = stalwart_leader_perk }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_perk")),
                diagnostics::toString);
    }

    @Test void observedHasDynastyPerkScalarTriggerIsSchemaKnown() {
        String source = "fixture = {\n"
                + "  limit = { has_dynasty_perk = warfare_legacy_3 }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_dynasty_perk")),
                diagnostics::toString);
    }

    @Test void observedHasCourtPositionScalarTriggerIsSchemaKnown() {
        String source = "fixture = {\n"
                + "  limit = { has_court_position = garuda_court_position }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_court_position")),
                diagnostics::toString);
    }

    @Test void observedWarDaysScalarIntegerTriggerIsSchemaKnown() throws Exception {
        Path fixture = Path.of("src/test/resources/fixtures/war_days_trigger.txt");
        var parsed = Parser.parse(Files.readString(fixture));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/war_days_trigger.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("war_days")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("WRONG_DOMAIN")
                                && d.message().contains("war_days")),
                diagnostics::toString);
    }

    @Test void observedHasInnovationScalarTriggerIsSchemaKnown() throws Exception {
        Path fixture = Path.of("src/test/resources/fixtures/has_innovation_trigger.txt");
        var parsed = Parser.parse(Files.readString(fixture));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/has_innovation_trigger.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_innovation")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("WRONG_DOMAIN")
                                && d.message().contains("has_innovation")),
                diagnostics::toString);
    }

    @Test void observedHasCulturalPillarScalarTriggerIsSchemaKnown() throws Exception {
        Path fixture = Path.of("src/test/resources/fixtures/has_cultural_pillar_trigger.txt");
        var parsed = Parser.parse(Files.readString(fixture));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/has_cultural_pillar_trigger.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_cultural_pillar")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("WRONG_DOMAIN")
                                && d.message().contains("has_cultural_pillar")),
                diagnostics::toString);
    }

    @Test void observedHasCulturalTraditionScalarTriggerIsSchemaKnown() throws Exception {
        Path fixture = Path.of("src/test/resources/fixtures/has_cultural_tradition_trigger.txt");
        var parsed = Parser.parse(Files.readString(fixture));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/has_cultural_tradition_trigger.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_cultural_tradition")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("WRONG_DOMAIN")
                                && d.message().contains("has_cultural_tradition")),
                diagnostics::toString);
    }

    @Test void effectConditionContainerAllowsRegisteredTriggersOnlyThere() {
        String source = "fixture = {\n"
                + "  limit = { is_ai = no has_variable = zg361_case_state }\n"
                + "  has_variable = direct_effect_side\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_effects/zg361_phase2.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals("WRONG_DOMAIN") && d.path().contains("limit")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("WRONG_DOMAIN") && d.path().endsWith("has_variable")),
                diagnostics::toString);
    }

    @Test void gameProfileAdapterKeepsOptionalParameterArity() {
        String source = "set_variable = { name = sample }\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/scripted_effects/xar.txt",
                PROFILE.gameProfile());
        assertTrue(diagnostics.stream().noneMatch(d -> d.code().equals("INVALID_PARAMETERS")),
                diagnostics::toString);
    }

    @Test void registeredOpcodeChecksDeclaredScope() {
        String source = "set_character_flag = { scope = TITLE flag = sample }\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/scripted_effects/xar.txt", PROFILE);
        assertTrue(diagnostics.stream().anyMatch(d -> d.code().equals("INVALID_SCOPE")),
                diagnostics::toString);
    }

    @Test void registeredOpcodeRejectsUnknownParameterName() {
        String source = "set_variable = { bogus = sample }\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/scripted_effects/xar.txt", PROFILE);
        assertTrue(diagnostics.stream().anyMatch(d -> d.code().equals("INVALID_PARAMETERS")),
                diagnostics::toString);
    }

    @Test void observedCk3ParameterFormsAreNotRejected() {
        String source = "change_variable = { name = sample add = 1 }\n"
                + "trigger_event = { id = test_event days = 1 }\n"
                + "add_prestige = { value = 0 subtract = 100 }\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/scripted_effects/xar.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d -> d.code().equals("INVALID_PARAMETERS")),
                diagnostics::toString);
    }

    @Test void repeatedCk3ParameterKeysRemainOrderedInput() {
        String source = "set_variable = { name = sample value = 1 value = 2 }\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/scripted_effects/xar.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d -> d.code().equals("DUPLICATE_KEY")),
                diagnostics::toString);
    }

    @Test void repeatedExecutableOpcodesRemainOrderedInput() {
        String source = "wrapper = {\n"
                + "  set_variable = { name = first value = 1 }\n"
                + "  set_variable = { name = second value = 2 }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "common/scripted_effects/xar.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d -> d.code().equals("DUPLICATE_KEY")),
                diagnostics::toString);
    }

    @Test void calculatedValueDiagnosticIsTriggerEqualityOnly() {
        String source = "fixture = {\n"
                + "  limit = {\n"
                + "    var:x >= { value = var:base add = 1 }\n"
                + "    var:x <= { value = var:base subtract = 1 }\n"
                + "    var:x = { value = var:base add = 1 subtract = 2 }\n"
                + "  }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_triggers/zg361_calculated_value_fixture.txt", PROFILE);
        var targeted = diagnostics.stream()
                .filter(d -> d.code().equals(Validator.CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED))
                .toList();
        assertEquals(1, targeted.size(), diagnostics::toString);
        assertTrue(targeted.get(0).path().contains("var:x"), targeted::toString);
        assertTrue(diagnostics.stream().noneMatch(d -> d.code().equals("UNKNOWN_OPCODE")),
                diagnostics::toString);
    }

    @Test void calculatedValueDiagnosticDoesNotLeakIntoEffectSideBlocks() {
        String source = "fixture = {\n"
                + "  limit = {\n"
                + "    save_scope_value_as = { name = saved value = { value = 1 add = 1 } }\n"
                + "  }\n"
                + "  set_variable = { name = x value = { value = 1 add = 1 } }\n"
                + "  change_variable = { name = x add = { value = 1 subtract = 1 } }\n"
                + "  save_scope_as = { name = { value = 1 add = 1 } }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_effects/zg361_calculated_value_effects.txt", PROFILE);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.code().equals(Validator.CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED)),
                diagnostics::toString);
    }

    @Test void calculatedValueDiagnosticFindsTriggerBlockInsideEffectFile() {
        String source = "fixture = {\n"
                + "  limit = {\n"
                + "    var:x >= { value = var:base add = 1 }\n"
                + "    var:x <= { value = var:base subtract = 1 }\n"
                + "    var:x = { value = var:base add = 1 subtract = 2 }\n"
                + "  }\n"
                + "  set_variable = { name = x value = 1 }\n"
                + "}\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        assertTrue(parsed.diagnostics().isEmpty(), () -> parsed.diagnostics().toString());
        var diagnostics = Validator.validate(parsed,
                "common/scripted_effects/zg361_calculated_value_effects.txt", PROFILE);
        assertEquals(1, diagnostics.stream()
                .filter(d -> d.code().equals(Validator.CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED))
                .count(), diagnostics::toString);
        assertTrue(diagnostics.stream().noneMatch(d -> d.code().equals("UNKNOWN_OPCODE")),
                diagnostics::toString);
    }

}
