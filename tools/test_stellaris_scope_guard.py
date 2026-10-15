"""Independent positive/negative cases for the finite population scope guard."""

from __future__ import annotations

import hashlib
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

from tools.stellaris_scope_guard import check_population_scopes, population_scope_preflight, script_value_shape_preflight


class PopulationScopeTest(unittest.TestCase):
    def check(self, source: str, path: str = "events/fixture.txt") -> dict:
        return check_population_scopes(source, path)

    def test_rejects_each_pop_group_iterator(self) -> None:
        for mode in ("every", "any", "random", "count"):
            for owner in ("owned", "species"):
                with self.subTest(mode=mode, owner=owner):
                    result = self.check(f"country_event = {{ immediate = {{ {mode}_{owner}_pop_group = {{ limit = {{ pop_amount > 0 }} }} }} }}")
                    self.assertEqual("FAIL", result["status"])
                    self.assertEqual("pop_group", result["diagnostics"][0]["scope"])
                    self.assertEqual("STELLARIS_SCOPE_MISMATCH", result["diagnostics"][0]["code"])

    def test_rejects_group_export_in_variable_arithmetic(self) -> None:
        result = self.check("country_event = {\n immediate = {\n every_owned_pop_group = {\n set_variable = { which = measured value = trigger:pop_amount }\n } } }")
        error = result["diagnostics"][0]
        self.assertEqual("FAIL", result["status"])
        self.assertEqual((4, 44), (error["line"], error["column"]))
        self.assertEqual("pop_amount", error["opcode"])

    def test_pop_group_size_comparison_and_export(self) -> None:
        result = self.check("country_event={immediate={every_owned_pop_group={limit={pop_group_size>0} set_variable={which=n value=trigger:pop_group_size}}}}")
        self.assertEqual("PASS", result["status"], result)
        self.assertEqual(2, result["checked"])

    def test_wrong_scope_for_group_size_is_rejected(self) -> None:
        self.assertEqual("FAIL", self.check("country_event={trigger={pop_group_size>0}} ")["status"])

    def test_country_and_planet_population_are_legal(self) -> None:
        for event in ("country_event", "planet_event"):
            with self.subTest(event=event):
                result = self.check(f"{event}={{trigger={{pop_amount>0}} immediate={{set_variable={{which=n value=trigger:pop_amount}}}}}}")
                self.assertEqual("PASS", result["status"], result)

    def test_comments_strings_and_variable_names_are_not_opcodes(self) -> None:
        result = self.check('country_event={ # pop_amount > 0\n desc="every_owned_pop_group={pop_amount>0} value=trigger:pop_amount" immediate={set_variable={which=pop_amount value=5} set_variable={which=trigger:pop_amount_count value="trigger:pop_amount"}}}')
        self.assertEqual("NOT_APPLICABLE", result["status"], result)
        self.assertEqual(0, result["checked"])

    def test_prev_and_nested_prev_preserve_scope_history(self) -> None:
        result = self.check("country_event={immediate={every_owned_pop_group={prev={pop_amount>0 prev={pop_group_size>0 prevprevprev={pop_amount>0}}}}}}")
        self.assertEqual("PASS", result["status"], result)
        self.assertEqual(3, result["checked"])
        result = self.check("country_event={immediate={every_owned_pop_group={species={prevprev={pop_amount>0}}}}}")
        self.assertEqual("PASS", result["status"], result)

    def test_prefixed_export_uses_its_referenced_scope(self) -> None:
        result = self.check("country_event={immediate={every_owned_pop_group={prev={set_variable={which=n value=prev.trigger:pop_amount}}}}}")
        self.assertEqual("FAIL", result["status"])
        result = self.check("country_event={immediate={every_owned_pop_group={set_variable={which=n value=prev.trigger:pop_amount} set_variable={which=m value=owner.trigger:pop_amount}}}}")
        self.assertEqual("PASS", result["status"], result)

    def test_prefixed_comparisons_and_dot_scope_blocks(self) -> None:
        result = self.check("country_event={immediate={every_owned_pop_group={prev.pop_amount>0 owner.species={prevprev={pop_group_size>0}}}}}")
        self.assertEqual("PASS", result["status"], result)
        result = self.check("country_event={immediate={every_owned_pop_group={this.pop_amount>0}}}")
        self.assertEqual("FAIL", result["status"])
        result = self.check("country_event={trigger={unregistered.pop_amount>0}}")
        self.assertEqual("UNKNOWN", result["status"])

    def test_unknown_root_prev_and_opaque_containers_are_explicit(self) -> None:
        for source, path in (
            ("generic={pop_amount>0}", "common/scripted_triggers/fixture.txt"),
            ("generic={every_owned_pop_group={prev={pop_amount>0}}}", "common/scripted_effects/fixture.txt"),
            ("country_event={immediate={opaque_scope={pop_amount>0}}}", "events/fixture.txt"),
            ("country_event={immediate={set_variable={which=n value=event_target:untyped.trigger:pop_amount}}}", "events/fixture.txt"),
            ("country_event={immediate={set_variable={which=n value=$SCOPE$.trigger:pop_amount}}}", "events/fixture.txt"),
            ("country_event={immediate={prevprev={pop_amount>0}}}", "events/fixture.txt"),
        ):
            with self.subTest(source=source):
                result = self.check(source, path)
                self.assertEqual("UNKNOWN", result["status"], result)
                self.assertEqual("STELLARIS_SCOPE_UNKNOWN", result["diagnostics"][0]["code"])

    def test_known_iterator_detects_error_inside_polymorphic_effect(self) -> None:
        result = self.check("generic={every_owned_pop_group={pop_amount>0}}", "common/scripted_effects/fixture.txt")
        self.assertEqual("FAIL", result["status"], result)

    def test_scope_chain_does_not_follow_brace_count(self) -> None:
        result = self.check("country_event={immediate={if={limit={always=yes} every_owned_pop_group={if={limit={always=yes} prev={AND={pop_amount>0}}}}}}}")
        self.assertEqual("PASS", result["status"], result)

    def test_unsupported_reader_input_does_not_pass(self) -> None:
        result = self.check("country_event={pop_amount > }")
        self.assertEqual("UNKNOWN", result["status"])

    def test_saved_global_pop_group_type_is_inferred(self) -> None:
        result = self.check("country_event={immediate={random_owned_pop_group={save_global_event_target_as=SavedGroup} event_target:SavedGroup={pop_group_size>0}}}")
        self.assertEqual("PASS", result["status"], result)
        self.assertEqual({"SavedGroup": "pop_group"}, result["saved_target_scopes"])

    def test_local_and_global_targets_support_exports_and_prev(self) -> None:
        result = self.check('country_event={immediate={save_event_target_as="saved_country" every_owned_pop_group={save_global_event_target_as=group} event_target:group={pop_group_size>0 prev={pop_amount>0}} set_variable={which=n value=event_target:group.trigger:pop_group_size} event_target:saved_country={pop_amount>0}}}')
        self.assertEqual("PASS", result["status"], result)
        self.assertEqual(4, result["checked"])

    def test_saved_target_known_wrong_type_is_rejected(self) -> None:
        for body in ("random_owned_pop_group={save_event_target_as=target} event_target:target={pop_amount>0}", "save_global_event_target_as=target event_target:target={pop_group_size>0}"):
            with self.subTest(body=body):
                self.assertEqual("FAIL", self.check(f"country_event={{immediate={{{body}}}}}")["status"])

    def test_same_type_multiple_saves_are_safe(self) -> None:
        result = self.check("country_event={immediate={random_owned_pop_group={save_global_event_target_as=group} every_owned_pop_group={save_global_event_target_as=group} event_target:group={pop_group_size>0}}}")
        self.assertEqual("PASS", result["status"], result)

    def test_conflicting_or_unknown_save_points_remain_unknown(self) -> None:
        for extra in ("save_global_event_target_as=group", "unregistered_scope={save_global_event_target_as=group}"):
            with self.subTest(extra=extra):
                source=f"country_event={{immediate={{random_owned_pop_group={{save_global_event_target_as=group}} {extra} event_target:group={{pop_group_size>0}}}}}}"
                self.assertEqual("UNKNOWN", self.check(source)["status"])

    def test_dynamic_unsaved_and_fake_saved_targets_remain_unknown(self) -> None:
        for save, name in (("save_global_event_target_as=$NAME$", "group"), ("save_global_event_target_as=group@root", "group@root"), ('desc="save_global_event_target_as=group"', "group"), ("# save_global_event_target_as=group\n", "group")):
            with self.subTest(save=save):
                result=self.check(f"country_event={{immediate={{random_owned_pop_group={{{save}}} event_target:{name}={{pop_group_size>0}}}}}}")
                self.assertEqual("UNKNOWN", result["status"], result)

    def test_alias_saved_target_inference_does_not_guess_fixed_points(self) -> None:
        result = self.check("country_event={immediate={save_global_event_target_as=source event_target:source={save_global_event_target_as=derived} event_target:derived={pop_amount>0}}}")
        self.assertEqual("UNKNOWN", result["status"], result)
        self.assertEqual({"source": "country"}, result["saved_target_scopes"])


class PopulationBuildBindingTest(unittest.TestCase):
    def setUp(self) -> None:
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.game = self.root / "game"
        self.package = self.root / "package"
        self.game.mkdir()
        self.package.mkdir()
        self.sha = hashlib.sha256(b"synthetic build identity").hexdigest()
        (self.game / "launcher-settings.json").write_text(json.dumps({"rawVersion": "v4.5.2"}), encoding="utf-8")
        self.alias = self.game / "common/scripted_triggers/00_scripted_triggers_pop.txt"
        self.alias.parent.mkdir(parents=True)
        self.alias.write_text("pop_amount = { count_owned_pop_amount = { } }", encoding="utf-8")
        self.path = self.package / "events/fixture.txt"
        self.source = "country_event={immediate={every_owned_pop_group={pop_group_size>0}}}"

    def verify(self) -> dict:
        return population_scope_preflight(self.package, self.game, [(self.path, self.source)], self.sha)

    def test_unregistered_build_is_unknown(self) -> None:
        self.assertEqual("UNKNOWN", self.verify()["status"])

    def test_registered_fixture_rule_checks_alias_and_reports_error(self) -> None:
        # Synthetic identity registration is test-local, never a shipping build.
        with patch("tools.stellaris_scope_guard.BUILDS", {("v4.5.2", self.sha)}):
            self.assertEqual("PASS", self.verify()["status"])
            self.source = self.source.replace("pop_group_size", "pop_amount")
            self.assertEqual("FAIL", self.verify()["status"])

    def test_changed_or_missing_alias_is_unknown(self) -> None:
        with patch("tools.stellaris_scope_guard.BUILDS", {("v4.5.2", self.sha)}):
            self.alias.write_text("pop_amount={always=yes}", encoding="utf-8")
            self.assertEqual("UNKNOWN", self.verify()["status"])
            self.alias.unlink()
            self.assertEqual("UNKNOWN", self.verify()["status"])

    def test_mod_alias_override_is_unknown(self) -> None:
        path = self.package / "common/scripted_triggers/replacement.txt"
        with patch("tools.stellaris_scope_guard.BUILDS", {("v4.5.2", self.sha)}):
            result = population_scope_preflight(self.package, self.game, [(self.path, self.source), (path, "pop_amount={always=yes}")], self.sha)
        self.assertEqual("UNKNOWN", result["status"])
        self.assertIn("overrides", result["diagnostics"][0]["reason"])

    def test_mod_replace_path_cannot_bypass_alias_binding(self) -> None:
        path = self.package / "descriptor.mod"
        with patch("tools.stellaris_scope_guard.BUILDS", {("v4.5.2", self.sha)}):
            result = population_scope_preflight(self.package, self.game, [(self.path, self.source), (path, 'replace_path="common/scripted_triggers"')], self.sha)
        self.assertEqual("UNKNOWN", result["status"])
        self.assertIn("replace_path", result["diagnostics"][0]["reason"])

    def test_no_relevant_expressions_do_not_require_population_profile(self) -> None:
        self.source = 'fixture={desc="pop_amount > 0" always=yes}'
        self.assertEqual("NOT_APPLICABLE", self.verify()["status"])

    def test_cross_file_saved_target_redefinitions_are_unknown(self) -> None:
        self.source="country_event={immediate={random_owned_pop_group={save_global_event_target_as=group} event_target:group={pop_group_size>0}}}"
        other=self.package/'events/another.txt'
        with patch("tools.stellaris_scope_guard.BUILDS", {("v4.5.2",self.sha)}):
            result=population_scope_preflight(self.package,self.game,[(self.path,self.source),(other,"country_event={immediate={save_global_event_target_as=group}}")],self.sha)
        self.assertEqual("UNKNOWN",result["status"],result)


class ReferencedScriptValueShapeTest(unittest.TestCase):
    def setUp(self) -> None:
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.game = self.root / "game"
        self.package = self.root / "package"
        self.game.mkdir()
        self.package.mkdir()
        self.sha = hashlib.sha256(b"synthetic value-shape build").hexdigest()
        (self.game / "launcher-settings.json").write_text(json.dumps({"rawVersion": "v4.5.2"}), encoding="utf-8")
        self.value_path = self.package / "common/script_values/fixture.txt"
        self.use_path = self.package / "events/fixture.txt"

    def verify(self, definition: str, use: str, registered: bool = True) -> dict:
        with patch("tools.stellaris_scope_guard.BUILDS", {("v4.5.2", self.sha)} if registered else set()):
            return script_value_shape_preflight(self.package, self.game, [(self.value_path, definition), (self.use_path, use)], self.sha)

    def test_referenced_numeric_scalar_definition_is_rejected(self) -> None:
        result = self.verify("coefficient = 0.05", "effect={set_variable={which=n value=value:coefficient}}")
        self.assertEqual("FAIL", result["status"], result)
        error = result["diagnostics"][0]
        self.assertEqual("STELLARIS_SCRIPT_VALUE_SCALAR_DEFINITION", error["code"])
        self.assertEqual("coefficient", error["name"])
        self.assertEqual(str(self.value_path), error["path"])
        self.assertEqual(str(self.use_path), error["references"][0]["path"])

    def test_signed_decimal_exponent_and_scoped_parameterized_reference(self) -> None:
        for literal in (".05", "-0.05", "+5e-2"):
            with self.subTest(literal=literal):
                self.assertEqual("FAIL", self.verify(f"coefficient={literal}", "effect={add=owner.value:coefficient|ARG|1|}")["status"])

    def test_block_and_at_constant_are_not_rejected(self) -> None:
        result = self.verify("@fixed=0.05 coefficient={base=@fixed}", "effect={add=value:coefficient mult=@fixed}")
        self.assertEqual("NOT_APPLICABLE", result["status"], result)

    def test_strings_comments_unused_literals_and_other_directories_are_ignored(self) -> None:
        result = self.verify('coefficient=0.05 # value:coefficient\n', 'effect={desc="value:coefficient" # add=value:coefficient\nadd=1}')
        self.assertEqual("NOT_APPLICABLE", result["status"], result)
        self.value_path = self.package / "common/scripted_variables/fixture.txt"
        self.assertEqual("NOT_APPLICABLE", self.verify("coefficient=.05", "effect={add=value:coefficient}")["status"])

    def test_unknown_build_symbolic_form_and_duplicate_definitions_are_explicit(self) -> None:
        for definition, registered in (("coefficient=.05", False), ("coefficient=@fixed", True), ('coefficient="0.05"', True), ("coefficient=.05 coefficient={base=.05}", True)):
            with self.subTest(definition=definition, registered=registered):
                result = self.verify(definition, "effect={add=value:coefficient}", registered)
                self.assertEqual("UNKNOWN", result["status"], result)
                self.assertEqual("STELLARIS_SCRIPT_VALUE_UNKNOWN", result["diagnostics"][0]["code"])

    def test_unresolved_external_name_is_not_claimed_as_certified(self) -> None:
        result = self.verify("local={base=.05}", "effect={add=value:third_party_value}")
        self.assertEqual("NOT_APPLICABLE", result["status"])


if __name__ == "__main__":
    unittest.main()
