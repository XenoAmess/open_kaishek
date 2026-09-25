package com.xenoamess.kaishek.profile;

import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
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
        assertEquals(ScriptDomain.PORTRAITS,
                profile.domainForPath("mod\\gfx\\portraits\\portraits\\21_portraits_cybernetics_synthqueen.txt"));
        assertEquals(ScriptDomain.UNKNOWN,
                profile.domainForPath("gfx/portraits/asset_selectors/room_textures.txt"));
    }

    @Test
    void cetanaStaticPortraitShapeValidatesAndBadFieldsFailClosed() {
        String path = "gfx/portraits/portraits/21_portraits_cybernetics_synthqueen.txt";
        StringBuilder source = new StringBuilder("portraits = {\n");
        for (String key : List.of("synth_queen", "cetana_mammalian", "cetana_reptilian",
                "cetana_aquatic", "cetana_lithoid", "cetana_plantoid",
                "cetana_molluscoid", "cetana_avian", "cetana_empty", "cetana_robot")) {
            source.append(key).append(" = { texturefile = ")
                    .append("\"gfx/models/portraits/xenoamess_cetana_portrait.dds\" ")
                    .append("greeting_sound = \"tox_portrait_01\" }\n");
        }
        String valid = source.append("}\n").toString();
        var accepted = Validator.validate(Parser.parse(valid.getBytes(StandardCharsets.UTF_8)),
                path, profile);
        assertTrue(accepted.stream().noneMatch(d -> d.severity() == Diagnostic.Severity.ERROR),
                accepted::toString);

        String invalid = valid.replace("cetana_mammalian", "cetana_mamalian")
                .replaceFirst("texturefile", "texturfile")
                .replace("gfx/models/portraits/xenoamess_cetana_portrait.dds\" greeting_sound",
                        "gfx/event_pictures/not_a_portrait.dds\" greeting_sound")
                .replace("cetana_robot = { texturefile", "cetana_robot = { greeting_sound = \"tox_portrait_01\" texturefile");
        var rejected = Validator.validate(Parser.parse(invalid.getBytes(StandardCharsets.UTF_8)),
                path, profile);
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("UNKNOWN_OPCODE")
                && d.message().contains("cetana_mamalian")), rejected::toString);
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("UNKNOWN_OPCODE")
                && d.message().contains("texturfile")), rejected::toString);
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("STELLARIS_PORTRAIT_REQUIRED")),
                rejected::toString);
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("STELLARIS_PORTRAIT_FIELD_REQUIRED")),
                rejected::toString);
        assertTrue(rejected.stream().anyMatch(d -> d.code().equals("INVALID_SCALAR_VALUE")
                && d.path().contains("texturefile")), rejected::toString);
        String duplicate = valid.replace("synth_queen = {", "synth_queen = { greeting_sound = \"tox_portrait_01\"");
        var duplicates = Validator.validate(Parser.parse(duplicate.getBytes(StandardCharsets.UTF_8)),
                path, profile);
        assertTrue(duplicates.stream().anyMatch(d -> d.code().equals("DUPLICATE_KEY")
                && d.path().contains("synth_queen.greeting_sound")), duplicates::toString);

        var wrongDomain = Validator.validate(Parser.parse(valid.getBytes(StandardCharsets.UTF_8)),
                "common/decisions/21_portraits_cybernetics_synthqueen.txt", profile);
        assertTrue(wrongDomain.stream().anyMatch(d -> d.code().equals("WRONG_DOMAIN")
                && d.path().contains("portraits")), wrongDomain::toString);
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

    @Test
    void habitatDistrictSetTriggerValidatesAndMalformedFormsFailClosed() {
        String valid = "test_deposit = {\n"
                + "  triggered_planet_modifier = {\n"
                + "    potential = { uses_district_set = habitat }\n"
                + "    modifier = { district_hab_science_max_add = 2 }\n"
                + "  }\n"
                + "}\n";
        var parsedValid = Parser.parse(valid.getBytes(StandardCharsets.UTF_8));
        var validDiagnostics = Validator.validate(parsedValid,
                "common/deposits/extend_workplace.txt", profile);

        assertTrue(validDiagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                validDiagnostics::toString);
        assertEquals(OpcodeSpec.Kind.TRIGGER, profile.opcode("uses_district_set").kind());
        assertEquals(Set.of("PLANET", "planet", "SHIP", "ship"),
                profile.opcode("uses_district_set").allowedScopes());

        String invalid = "test_deposit = { triggered_planet_modifier = { potential = {\n"
                + "  uses_district_sets = habitat\n"
                + "  uses_district_set = { scope = COUNTRY value = habitat }\n"
                + "} modifier = { district_hab_science_max_add = 2 } } }\n";
        var parsedInvalid = Parser.parse(invalid.getBytes(StandardCharsets.UTF_8));
        var invalidDiagnostics = Validator.validate(parsedInvalid,
                "common/deposits/extend_workplace.txt", profile);

        assertTrue(invalidDiagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("uses_district_sets")),
                invalidDiagnostics::toString);
        assertTrue(invalidDiagnostics.stream().anyMatch(d ->
                        d.code().equals("INVALID_SCOPE")
                                && d.message().contains("COUNTRY")),
                invalidDiagnostics::toString);
        assertTrue(invalidDiagnostics.stream().anyMatch(d ->
                        d.code().equals("INVALID_PARAMETERS")
                                && d.message().contains("uses_district_set")),
                invalidDiagnostics::toString);
    }

    @Test
    void grayWindCompatibilityEventAndMonthlyOnActionValidate() {
        String event = "namespace = xenoamess_gray_wind\n"
                + "country_event = {\n"
                + "  id = xenoamess_gray_wind.100\n"
                + "  hide_window = yes\n"
                + "  is_triggered_only = yes\n"
                + "  trigger = { is_ai = no OR = {\n"
                + "    has_country_flag = gray_official_active\n"
                + "    any_owned_leader = { has_leader_flag = gray_leader }\n"
                + "  } }\n"
                + "  immediate = {\n"
                + "    every_owned_leader = { limit = { has_leader_flag = gray_leader }\n"
                + "      change_leader_portrait = xenoamess_gray_wind_portrait }\n"
                + "    if = { limit = { exists = event_target:gray_official }\n"
                + "      event_target:gray_official = {\n"
                + "        change_leader_portrait = xenoamess_gray_wind_portrait } }\n"
                + "    if = { limit = { exists = event_target:gray_country }\n"
                + "      event_target:gray_country = { ruler = {\n"
                + "        change_leader_portrait = xenoamess_gray_wind_portrait } } }\n"
                + "  }\n"
                + "}\n";
        var parsedEvent = Parser.parse(event.getBytes(StandardCharsets.UTF_8));
        var eventDiagnostics = Validator.validate(parsedEvent,
                "events/xenoamess_gray_wind_events.txt", profile);
        assertTrue(eventDiagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                eventDiagnostics::toString);

        String onAction = "on_monthly_pulse_country = {\n"
                + "  events = { xenoamess_gray_wind.100 }\n"
                + "}\n";
        var parsedOnAction = Parser.parse(onAction.getBytes(StandardCharsets.UTF_8));
        var onActionDiagnostics = Validator.validate(parsedOnAction,
                "common/on_actions/xenoamess_gray_wind_on_actions.txt", profile);
        assertTrue(onActionDiagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                onActionDiagnostics::toString);
        assertEquals(OpcodeSpec.Kind.EFFECT,
                profile.opcode("change_leader_portrait").kind());
        assertEquals(OpcodeSpec.Kind.STRUCTURAL,
                profile.opcode("event_target:gray_country").kind());
    }

    @Test
    void grayWindTyposRemainFailClosed() {
        String invalid = "country_event = { id = test.1 hide_window = yes\n"
                + "  trigger = { has_leaders_flag = gray_leader }\n"
                + "  immediate = { event_target:gray_countri = {\n"
                + "    change_leader_portraits = xenoamess_gray_wind_portrait } }\n"
                + "}\n";
        var parsed = Parser.parse(invalid.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed,
                "events/xenoamess_gray_wind_events.txt", profile);

        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("has_leaders_flag")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("event_target:gray_countri")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("change_leader_portraits")),
                diagnostics::toString);
    }
}
