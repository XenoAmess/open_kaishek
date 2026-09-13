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
        assertEquals(6, Eu5Profile1311.VANILLA_EVIDENCE_SHA256.size());
        assertTrue(Eu5Profile1311.VANILLA_EVIDENCE_SHA256.values().stream()
                .allMatch(hash -> hash.length() == 64));
        assertEquals(ScriptDomain.INTERACTIONS,
                profile.domainForPath("in_game/common/country_interactions/xcrt.txt"));
        assertEquals(ScriptDomain.SCRIPTED_TRIGGERS,
                profile.domainForPath("in_game/common/scripted_triggers/xcrt.txt"));
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
}
