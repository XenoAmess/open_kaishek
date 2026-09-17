package com.xenoamess.kaishek.profile;

import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Eu5Profile1311Test {
    private final Eu5Profile1311 profile = new Eu5Profile1311();

    @Test
    void exactBuildIdentityAndVanillaEvidenceArePinned() {
        assertEquals("eu5-1.3.11-build-24187685", profile.id());
        assertEquals("1.3.11", profile.gameVersion());
        assertEquals("24187685", Eu5Profile1311.STEAM_BUILD_ID);
        assertEquals(64, profile.executableSha256().length());
        assertEquals(7, Eu5Profile1311.VANILLA_EVIDENCE_SHA256.size());
        assertEquals(8, Eu5Profile1311.VANILLA_EVENT_EVIDENCE_SHA256.size());
        assertEquals(3, Eu5Profile1311.VANILLA_WAR_FIXTURE_EVIDENCE_SHA256.size());
        assertEquals(2, Eu5Profile1311.VANILLA_CREATED_COUNTRY_IO_EVIDENCE_SHA256.size());
        assertTrue(Eu5Profile1311.VANILLA_EVIDENCE_SHA256.values().stream()
                .allMatch(hash -> hash.length() == 64));
        assertTrue(Eu5Profile1311.VANILLA_EVENT_EVIDENCE_SHA256.values().stream()
                .allMatch(hash -> hash.length() == 64));
        assertTrue(Eu5Profile1311.VANILLA_WAR_FIXTURE_EVIDENCE_SHA256.values().stream()
                .allMatch(hash -> hash.length() == 64));
        assertTrue(Eu5Profile1311.VANILLA_CREATED_COUNTRY_IO_EVIDENCE_SHA256.values().stream()
                .allMatch(hash -> hash.length() == 64));
        assertEquals(ScriptDomain.INTERACTIONS,
                profile.domainForPath("in_game/common/country_interactions/xcrt.txt"));
        assertEquals(ScriptDomain.SCRIPTED_TRIGGERS,
                profile.domainForPath("in_game/common/scripted_triggers/xcrt.txt"));
        assertEquals(ScriptDomain.EVENTS,
                profile.domainForPath("in_game/events/xcrt_acceptance.txt"));
    }

    @Test
    void territorialCandidateCapitalAndSnapshotOpcodesAreTyped() {
        assertEquals(OpcodeSpec.Kind.TRIGGER, profile.opcode("exists").kind());
        assertEquals(OpcodeSpec.Kind.TRIGGER, profile.opcode("is_subject_of").kind());
        assertEquals(OpcodeSpec.Kind.TRIGGER, profile.opcode("is_subject_type").kind());
        assertEquals(OpcodeSpec.Kind.TRIGGER, profile.opcode("list_size").kind());
        assertEquals(OpcodeSpec.Kind.EFFECT, profile.opcode("add_to_list").kind());
        assertEquals(OpcodeSpec.Kind.EFFECT, profile.opcode("change_location_owner").kind());
        assertNotNull(profile.opcode("num_locations"));
        assertFalse(profile.allowedStructuralKeys().contains("invented_eu5_operation"));
    }

    @Test
    void tusiSelectorAndFrozenListGuardsValidate() {
        String source = """
                xcrt_is_eligible_direct_subject_target = {
                  NOR = {
                    country_type = building
                    country_type = pop
                    country_type = army
                  }
                  exists = capital
                  is_subject_of = scope:actor
                }
                xcrt_is_transferable_location = {
                  is_ownable = yes
                  NOT = { owner ?= scope:recipient }
                  owner ?= {
                    OR = {
                      this = scope:actor
                      is_subject_or_below_of = scope:actor
                    }
                  }
                }
                xcrt_has_at_most_transferable_locations = {
                  capital.region = {
                    any_location_in_region = {
                      count <= $MAX$
                      xcrt_is_transferable_location = yes
                    }
                  }
                }
                xcrt_recipient_respects_tusi_cap = {
                  trigger_if = {
                    limit = { is_subject_type = tusi }
                    OR = {
                      AND = {
                        num_locations = 14
                        xcrt_has_at_most_transferable_locations = { MAX = 1 }
                      }
                    }
                  }
                }
                xcrt_frozen_transfer_list_respects_tusi_cap = {
                  trigger_if = {
                    limit = { is_subject_type = tusi }
                    OR = {
                      AND = {
                        num_locations = 14
                        list_size = { name = xcrt_transfer_locations value <= 1 }
                      }
                    }
                  }
                }
                """;
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed,
                "in_game/common/scripted_triggers/xcrt.txt", profile);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
    }

    @Test
    void countryInteractionCandidateAndAtomicReplayValidate() {
        String source = """
                xcrt_cleanup_colonial_region = {
                  type = subject
                  category = CATEGORY_SUBJECT_ACTIONS
                  ai_tick = never
                  potential = {
                    scope:actor = {
                      any_subject = {
                        xcrt_is_eligible_direct_subject_target = yes
                      }
                    }
                  }
                  select_trigger = {
                    looking_for_a = country
                    interaction_source_list = {
                      scope:actor = {
                        every_subject = {
                          limit = { xcrt_is_eligible_direct_subject_target = yes }
                          add_to_list = source
                        }
                      }
                    }
                    target_flag = recipient
                    visible = { xcrt_is_eligible_direct_subject_target = yes }
                    enabled = { xcrt_recipient_respects_tusi_cap = yes }
                  }
                  effect = {
                    scope:recipient.capital.region = {
                      every_location_in_region = {
                        limit = { xcrt_is_transferable_location = yes }
                        add_to_list = xcrt_transfer_locations
                      }
                    }
                    if = {
                      limit = {
                        scope:recipient = {
                          xcrt_is_eligible_direct_subject_target = yes
                          xcrt_frozen_transfer_list_respects_tusi_cap = yes
                        }
                        list_size = { name = xcrt_transfer_locations value > 0 }
                      }
                      every_in_list = {
                        list = xcrt_transfer_locations
                        change_location_owner = scope:recipient
                      }
                    }
                  }
                }
                """;
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed,
                "in_game/common/country_interactions/xcrt.txt", profile);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
    }

    @Test
    void unknownEu5OperationFailsClosed() {
        String source = "xcrt_is_eligible_direct_subject_target = { invented_eu5_operation = yes }\n";
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed,
                "in_game/common/scripted_triggers/xcrt.txt", profile);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("invented_eu5_operation")),
                diagnostics::toString);
    }

    @Test
    void countryEventOptionScopeAndMixedCreationBlocksValidate() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.10 = {
                  type = country_event
                  title = xcrt_acceptance.10.title
                  desc = xcrt_acceptance.10.desc
                  outcome = neutral
                  orphan = yes
                  trigger = { always = no }
                  option = {
                    name = xcrt_acceptance.10.a
                    trigger = { tag = LNG c:GYT = { is_subject_of = root } }
                    location:tortuga = {
                      discover_location = root
                      create_country_from_location = {
                        overlord = root
                        subject_type = subject_type:vassal
                        define_unique_country_tag = XCRTT
                        change_country_name = XCRTT
                        change_country_adjective = XCRTT
                      }
                    }
                    location:lisbon = {
                      create_building_country_in_location = {
                        name = { name = XMBNK }
                        reforms = { banking_country }
                        hidden_effect = {
                          define_unique_country_tag = XMBNK
                          make_subject_of = { target = root type = subject_type:state_bank }
                        }
                      }
                    }
                    set_global_variable = { name = xcrt_actor value = root }
                    set_capital = location:porto_santo
                  }
                  option = {
                    name = xcrt_acceptance.10.b
                    trigger = {
                      c:GYT = {
                        capital.region = {
                          any_location_in_region = {
                            count <= 1
                            is_ownable = yes
                            NOT = { owner ?= c:GYT }
                          }
                        }
                      }
                    }
                    custom_tooltip = xcrt_acceptance.10.b.tt
                  }
                }
                """;
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
    }

    @Test
    void countryCapitalRegionEffectScopeValidatesButArbitraryDottedScopeDoesNot() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.11 = {
                  type = country_event
                  trigger = { always = no }
                  option = {
                    name = xcrt_acceptance.11.a
                    c:GYT.capital = { save_scope_as = xcrt_native_capital }
                    c:GYT.capital.region = {
                      every_location_in_region = {
                        limit = {
                          is_ownable = yes
                          NOT = { owner ?= c:GYT }
                          owner ?= { OR = { this = root is_subject_or_below_of = root } }
                        }
                        add_to_list = xcrt_acceptance_extra_donors_to_isolate
                      }
                    }
                    every_in_list = {
                      list = xcrt_acceptance_extra_donors_to_isolate
                      change_location_owner = c:XCRTI
                    }
                  }
                }
                """;
        var diagnostics = Validator.validate(Parser.parse(source.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);

        var invalid = Validator.validate(Parser.parse(source.replace(
                        "c:GYT.capital.region = {", "c:GYT.capital.area = {")
                .getBytes(StandardCharsets.UTF_8)), "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(invalid.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("c:GYT.capital.area")),
                invalid::toString);
    }

    @Test
    void nandAndWarFixtureVocabularyValidateWithSideTransitions() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.6 = {
                  type = country_event
                  trigger = { always = no }
                  option = {
                    name = xcrt_acceptance.6.a
                    trigger = {
                      NAND = {
                        country_exists = c:XCRTI
                        NOT = { country_exists = c:XCRTT }
                      }
                    }
                    declare_war = c:XCRTI
                  }
                }
                xcrt_acceptance.7 = {
                  type = country_event
                  trigger = { always = no }
                  option = {
                    name = xcrt_acceptance.7.a
                    every_current_war = {
                      limit = { any_war_participant = { this = c:XCRTI } }
                      white_peace = this
                    }
                  }
                }
                """;
        var diagnostics = Validator.validate(Parser.parse(source.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
    }

    @Test
    void warFixtureTriggerAndEffectOpcodesRejectWrongSides() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.6 = {
                  type = country_event
                  trigger = { declare_war = c:XCRTI white_peace = this }
                  option = {
                    name = xcrt_acceptance.6.a
                    country_exists = c:XCRTI
                    any_war_participant = { this = c:XCRTI }
                  }
                }
                """;
        var diagnostics = Validator.validate(Parser.parse(source.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertEquals(4, diagnostics.stream().filter(d -> d.code().equals("WRONG_DOMAIN")).count(),
                diagnostics::toString);
    }

    @Test
    void unknownInsideNandAndCurrentWarIteratorStillFailsClosed() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.7 = {
                  type = country_event
                  trigger = { NAND = { invented_nand_trigger = yes } }
                  option = {
                    name = xcrt_acceptance.7.a
                    every_current_war = { invented_war_effect = yes }
                  }
                }
                """;
        var diagnostics = Validator.validate(Parser.parse(source.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertEquals(2, diagnostics.stream().filter(d -> d.code().equals("UNKNOWN_OPCODE")).count(),
                diagnostics::toString);
    }

    @Test
    void createdCountryRankAndInternationalOrganizationVariableValidate() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.33 = {
                  type = country_event
                  trigger = { always = no }
                  option = {
                    name = xcrt_acceptance.33.a
                    location:torres_vedras = {
                      create_country_from_location = {
                        set_country_rank = country_rank:rank_county
                      }
                    }
                  }
                }
                xcrt_acceptance.35 = {
                  type = country_event
                  trigger = { always = no }
                  option = {
                    name = xcrt_acceptance.35.a
                    international_organization:hre = {
                      set_variable = {
                        name = hre_direct_free_cities_subject
                        value = yes
                      }
                    }
                  }
                }
                """;
        var diagnostics = Validator.validate(Parser.parse(source.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
    }

    @Test
    void createdCountryAndInternationalOrganizationVocabularyFailsClosed() {
        String wrongSide = """
                namespace = xcrt_acceptance
                xcrt_acceptance.35 = {
                  type = country_event
                  trigger = {
                    set_country_rank = country_rank:rank_county
                    set_variable = { name = fixture_error value = yes }
                  }
                }
                """;
        var wrongSideDiagnostics = Validator.validate(
                Parser.parse(wrongSide.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertEquals(2, wrongSideDiagnostics.stream()
                        .filter(d -> d.code().equals("WRONG_DOMAIN")).count(),
                wrongSideDiagnostics::toString);

        String badScopes = """
                namespace = xcrt_acceptance
                xcrt_acceptance.36 = {
                  type = country_event
                  option = {
                    name = xcrt_acceptance.36.a
                    international_organization:hre = yes
                    international_organization:hre.leader_country = {
                      set_variable = fixture_error
                    }
                  }
                }
                """;
        var badScopeDiagnostics = Validator.validate(
                Parser.parse(badScopes.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(badScopeDiagnostics.stream().anyMatch(d ->
                        d.code().equals("SCOPE_LINK_REQUIRES_BLOCK")
                                && d.message().contains("international_organization:hre")),
                badScopeDiagnostics::toString);
        assertTrue(badScopeDiagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("international_organization:hre.leader_country")),
                badScopeDiagnostics::toString);
    }

    @Test
    void currentSubjectTypeLockValidatesAsCountryScopeEffect() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.37 = {
                  type = country_event
                  trigger = { always = no }
                  option = {
                    name = xcrt_acceptance.37.a
                    c:HAD = { lock_current_subject_type = yes }
                    c:MEW = {
                      lock_current_subject_type = yes
                      make_subject_of = {
                        target = root
                        type = subject_type:maha_samanta
                      }
                    }
                  }
                }
                """;
        var diagnostics = Validator.validate(Parser.parse(source.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(diagnostics.stream().noneMatch(d ->
                        d.severity() == Diagnostic.Severity.ERROR),
                diagnostics::toString);
    }

    @Test
    void currentSubjectTypeLockRejectsTriggerSideAndUnknownLookalike() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.37 = {
                  type = country_event
                  trigger = { lock_current_subject_type = yes }
                  option = {
                    name = xcrt_acceptance.37.a
                    c:HAD = { lock_subject_type_without_evidence = yes }
                  }
                }
                """;
        var diagnostics = Validator.validate(Parser.parse(source.getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("WRONG_DOMAIN")
                                && d.message().contains("lock_current_subject_type")),
                diagnostics::toString);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("lock_subject_type_without_evidence")),
                diagnostics::toString);
    }

    @Test
    void unknownInsideMixedCreationBlockStillFailsClosed() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.20 = {
                  type = country_event
                  option = {
                    name = xcrt_acceptance.20.a
                    location:lisbon = {
                      create_building_country_in_location = {
                        hidden_effect = { invented_eu5_operation = yes }
                      }
                    }
                  }
                }
                """;
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "in_game/events/xcrt_acceptance.txt", profile);
        assertTrue(diagnostics.stream().anyMatch(d ->
                        d.code().equals("UNKNOWN_OPCODE")
                                && d.message().contains("invented_eu5_operation")),
                diagnostics::toString);
    }

    @Test
    void eventTriggerAndOptionEffectSidesStayDistinct() {
        String source = """
                namespace = xcrt_acceptance
                xcrt_acceptance.11 = {
                  type = country_event
                  trigger = { discover_location = root }
                  option = {
                    name = xcrt_acceptance.11.a
                    has_global_variable = xcrt_ready
                  }
                }
                """;
        var parsed = Parser.parse(source.getBytes(StandardCharsets.UTF_8));
        var diagnostics = Validator.validate(parsed, "in_game/events/xcrt_acceptance.txt", profile);
        assertEquals(2, diagnostics.stream().filter(d -> d.code().equals("WRONG_DOMAIN")).count(),
                diagnostics::toString);
    }

    @Test
    void eventDeclarationsRequireNamespaceBoundedIdAndCountryScope() {
        var noNamespace = Validator.validate(
                Parser.parse("xcrt_acceptance.1 = { type = country_event }".getBytes(StandardCharsets.UTF_8)),
                "in_game/events/xcrt.txt", profile);
        assertTrue(noNamespace.stream().anyMatch(d ->
                d.code().equals("EU5_EVENT_NAMESPACE_REQUIRED")), noNamespace::toString);

        var outOfSlice = Validator.validate(Parser.parse("""
                namespace = xcrt_acceptance
                xcrt_acceptance.10000 = { type = country_event }
                xcrt_acceptance.1 = { type = location_event }
                """.getBytes(StandardCharsets.UTF_8)), "in_game/events/xcrt.txt", profile);
        assertTrue(outOfSlice.stream().anyMatch(d ->
                d.code().equals("EU5_EVENT_ID_INVALID")), outOfSlice::toString);
        assertTrue(outOfSlice.stream().anyMatch(d ->
                d.code().equals("EU5_EVENT_TYPE_OUT_OF_SLICE")), outOfSlice::toString);

        var scalarScopeLink = Validator.validate(Parser.parse("""
                namespace = xcrt_acceptance
                xcrt_acceptance.2 = {
                  type = country_event
                  option = { name = xcrt_acceptance.2.a location:tortuga = yes }
                }
                """.getBytes(StandardCharsets.UTF_8)), "in_game/events/xcrt.txt", profile);
        assertTrue(scalarScopeLink.stream().anyMatch(d ->
                d.code().equals("SCOPE_LINK_REQUIRES_BLOCK")), scalarScopeLink::toString);

        var scalarCapitalRegion = Validator.validate(Parser.parse("""
                namespace = xcrt_acceptance
                xcrt_acceptance.3 = {
                  type = country_event
                  option = { name = xcrt_acceptance.3.a c:GYT.capital.region = yes }
                }
                """.getBytes(StandardCharsets.UTF_8)), "in_game/events/xcrt.txt", profile);
        assertTrue(scalarCapitalRegion.stream().anyMatch(d ->
                d.code().equals("SCOPE_LINK_REQUIRES_BLOCK")), scalarCapitalRegion::toString);
    }
}
