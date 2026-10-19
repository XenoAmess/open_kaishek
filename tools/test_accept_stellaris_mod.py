"""Contract tests for the Stellaris Mod package checker."""

from __future__ import annotations

import hashlib
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

from tools.accept_stellaris_mod import CLI, WEAPON_COLUMNS, check, check_weapon_csv, read_localization


class StellarisModPackageTest(unittest.TestCase):
    def setUp(self) -> None:
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.game = self.root / "game"
        self.mod = self.root / "package"
        self.package = self.mod / "mod"
        self.game.mkdir()
        self.package.mkdir(parents=True)
        (self.game / "stellaris.exe").write_bytes(b"fixture game")
        self.hash = hashlib.sha256(b"fixture game").hexdigest()
        for language in ("simp_chinese", "english"):
            (self.game / "localisation" / language).mkdir(parents=True)
            target = self.package / "localisation" / language
            target.mkdir(parents=True)
            value = "测试 $NAME$ §G一§!" if language == "simp_chinese" else "Test $NAME$ §Gone§!"
            (target / f"fixture_l_{language}.yml").write_text(
                f'\ufeffl_{language}:\n test_key:0 "{value}"\n', encoding="utf-8"
            )
        (self.mod / "VERSION").write_text("0.1.0-rc.1\n", encoding="utf-8")
        (self.package / "descriptor.mod").write_text(
            'name="Fixture"\nversion="0.1.0-rc.1"\nsupported_version="4.5.*"\n', encoding="utf-8"
        )
        script = self.package / "common/traits/fixture.txt"
        script.parent.mkdir(parents=True)
        script.write_text('fixture_trait = { cost = 1 icon = "gfx/interface/fixture.dds" }\n', encoding="utf-8")
        asset = self.package / "gfx/interface/fixture.dds"
        asset.parent.mkdir(parents=True)
        asset.write_bytes(b"DDS " + b"\0" * 124)

    def verify(self) -> dict:
        if not CLI.is_file():
            self.skipTest("Kaishek CLI must be built before package tests")
        return check(self.mod, self.game, self.hash, CLI)

    def test_valid_package(self) -> None:
        result = self.verify()
        self.assertEqual("PASS", result["status"], result["errors"])
        self.assertEqual(2, result["p_parsed"])
        self.assertEqual("not_applicable_no_weapon_csv_files", result["weapon_csv_applicability"])

    def test_weapon_csv_is_included_in_package_acceptance(self) -> None:
        root = self.package / "common/component_templates"
        root.mkdir(parents=True)
        (root / "fixture.txt").write_text(
            'weapon_component_template = { key = "FIXTURE_WEAPON" size = planet_killer }\n', encoding="utf-8")
        table = root / "fixture_weapon.CSV"
        table.write_text(";".join(WEAPON_COLUMNS) + "\n" +
                         ";".join(["FIXTURE_WEAPON", *(["0.00"] * 23), ""]) + "\n", encoding="utf-8")
        result = self.verify()
        self.assertEqual("PASS", result["status"], result["errors"])
        self.assertEqual(1, result["weapon_csv_files"])
        self.assertEqual(1, result["weapon_csv_keys"])
        table.write_text(table.read_text(encoding="utf-8").replace("0.00", "NaN", 1), encoding="utf-8")
        result = self.verify()
        self.assertEqual("FAIL", result["status"])
        self.assertIn("WEAPON_CSV_NUMBER", {error["code"] for error in result["errors"]})

    def gui(self, content: str, suffix: str = ".gui") -> Path:
        path = self.package / "interface" / ("fixture" + suffix)
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")
        return path

    def test_gui_is_discovered_and_parsed_case_insensitively(self) -> None:
        self.gui('guiTypes = { containerWindowType = { name = "fixture" size = { x = 100 y = 40 } } }\n', ".GUI")
        result = self.verify()
        self.assertEqual("PASS", result["status"], result["errors"])
        self.assertEqual(3, result["p_scripts"])
        self.assertEqual(3, result["p_parsed"])

    def test_rejects_malformed_gui(self) -> None:
        path = self.gui('guiTypes = { containerWindowType = { name = "fixture" }\n')
        result = self.verify()
        self.assertEqual("FAIL", result["status"])
        self.assertTrue(any(error["code"] == "P_SCRIPT_PARSE" and error["path"] == str(path)
                            for error in result["errors"]))

    def test_rejects_gui_bom(self) -> None:
        path = self.gui('\ufeffguiTypes = {}\n')
        result = self.verify()
        self.assertEqual("FAIL", result["status"])
        self.assertTrue(any(error["code"] == "P_SCRIPT_BOM" and error["path"] == str(path)
                            for error in result["errors"]))

    def test_asset_script_is_discovered_case_insensitively(self) -> None:
        path = self.package / "gfx/models/fixture.ASSET"
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text('entity = { name = "fixture_entity" clone = "vanilla_entity" }\n', encoding="utf-8")
        result = self.verify()
        self.assertEqual("PASS", result["status"], result["errors"])
        self.assertEqual(3, result["p_scripts"])
        self.assertEqual(3, result["p_parsed"])

    def test_rejects_malformed_asset_and_asset_bom(self) -> None:
        path = self.package / "gfx/models/fixture.asset"
        path.parent.mkdir(parents=True, exist_ok=True)
        for value, code in (('entity = { name = "broken"\n', "P_SCRIPT_PARSE"),
                            ('\ufeffentity = {}\n', "P_SCRIPT_BOM")):
            with self.subTest(code=code):
                path.write_text(value, encoding="utf-8")
                result = self.verify()
                self.assertEqual("FAIL", result["status"])
                self.assertTrue(any(error["code"] == code and error["path"] == str(path)
                                    for error in result["errors"]))

    def test_rejects_gui_missing_asset(self) -> None:
        path = self.gui('guiTypes = { iconType = { name = "fixture" textureFile = "gfx/missing.dds" } }\n')
        result = self.verify()
        self.assertEqual("FAIL", result["status"])
        self.assertTrue(any(error["code"] == "ASSET_REFERENCE_MISSING" and error["path"] == str(path)
                            for error in result["errors"]))

    def test_rejects_metadata_and_asset_drift(self) -> None:
        (self.mod / "VERSION").write_text("0.2.0\n", encoding="utf-8")
        (self.package / "gfx/interface/fixture.dds").unlink()
        codes = {error["code"] for error in self.verify()["errors"]}
        self.assertIn("MOD_VERSION_CONTRACT", codes)
        self.assertIn("ASSET_REFERENCE_MISSING", codes)

    def test_accepts_vanilla_asset_and_rejects_missing_from_both_roots(self) -> None:
        script = self.package / "common/traits/fixture.txt"
        script.write_text('fixture_trait = { cost = 1 icon = "gfx/interface/vanilla.dds" }\n', encoding="utf-8")
        vanilla = self.game / "gfx/interface/vanilla.dds"
        vanilla.parent.mkdir(parents=True)
        vanilla.write_bytes(b"DDS " + b"\0" * 124)
        self.assertEqual("PASS", self.verify()["status"])
        vanilla.unlink()
        codes = {error["code"] for error in self.verify()["errors"]}
        self.assertIn("ASSET_REFERENCE_MISSING", codes)

    def test_rejects_localization_key_and_script_bom(self) -> None:
        (self.package / "localisation/english/fixture_l_english.yml").write_text(
            '\ufeffl_english:\n another_key:0 "Another"\n', encoding="utf-8"
        )
        script = self.package / "common/traits/fixture.txt"
        script.write_bytes(b"\xef\xbb\xbf" + script.read_bytes())
        codes = {error["code"] for error in self.verify()["errors"]}
        self.assertIn("LOCALIZATION_KEY_SET", codes)
        self.assertIn("P_SCRIPT_BOM", codes)

    def test_accepts_flat_localization_and_rejects_missing_language(self) -> None:
        root = self.package / "localisation"
        for language in ("english", "simp_chinese"):
            source = root / language / f"fixture_l_{language}.yml"
            source.rename(root / source.name)
            source.parent.rmdir()
        self.assertEqual("PASS", self.verify()["status"])
        (root / "fixture_l_english.yml").unlink()
        self.assertIn("LOCALIZATION_LANGUAGE_MISSING", {error["code"] for error in self.verify()["errors"]})

    def test_accepts_art_only_package_without_localization(self) -> None:
        for path in (self.package / "localisation").rglob("*.yml"):
            path.unlink()
        result = self.verify()
        self.assertEqual("PASS", result["status"], result["errors"])
        self.assertEqual("not_applicable_no_localization_files", result["localization_applicability"])

    def test_explicit_version_source_is_required_and_validated(self) -> None:
        source = self.root / "VERSION"
        (self.mod / "VERSION").rename(source)
        self.assertIn("MOD_METADATA_MISSING", {error["code"] for error in self.verify()["errors"]})
        self.assertEqual("PASS", check(self.mod, self.game, self.hash, CLI, source)["status"])
        source.write_text("9.0.0\n", encoding="utf-8")
        self.assertIn("MOD_VERSION_CONTRACT", {error["code"] for error in check(self.mod, self.game, self.hash, CLI, source)["errors"]})

    def test_rejects_outdated_supported_version(self) -> None:
        (self.game / "launcher-settings.json").write_text(json.dumps({"rawVersion": "v4.5.1"}), encoding="utf-8")
        self.assertEqual("PASS", self.verify()["status"])
        descriptor = self.package / "descriptor.mod"
        descriptor.write_text(descriptor.read_text(encoding="utf-8").replace("4.5.*", "4.4.*"), encoding="utf-8")
        self.assertIn("MOD_SUPPORTED_VERSION", {error["code"] for error in self.verify()["errors"]})

    def test_localization_optional_revision_retains_syntax_checks(self) -> None:
        path = self.package / "localisation/english/fixture_l_english.yml"
        path.write_text(path.read_text(encoding="utf-8").replace("test_key:0", "test_key:"), encoding="utf-8")
        with path.open("a", encoding="utf-8") as stream:
            stream.write(" \n\t \n")
        self.assertEqual("PASS", self.verify()["status"])
        path.write_text(path.read_text(encoding="utf-8").replace("test_key:", "test_key"), encoding="utf-8")
        self.assertIn("LOCALIZATION_SYNTAX", {error["code"] for error in self.verify()["errors"]})

    def test_rejects_encoding_character_loss_in_localization(self) -> None:
        path = self.package / "localisation/simp_chinese/fixture_l_simp_chinese.yml"
        for value, reason in (("?????", "consecutive_question_marks"),
                              ("正常10????船坞", "consecutive_question_marks"),
                              ("损坏\ufffd文本", "replacement_character")):
            with self.subTest(value=value):
                path.write_text(f'l_simp_chinese:\n test_key:0 "{value}"\n', encoding="utf-8-sig")
                errors = []
                self.assertEqual({"test_key": value}, read_localization(path, "simp_chinese", errors))
                self.assertEqual([{"code": "LOCALIZATION_CHARACTER_LOSS", "path": str(path),
                                   "key": "test_key", "line": 2, "reason": reason}], errors)

    def test_accepts_valid_text_and_normal_question_marks(self) -> None:
        for language, value in (("simp_chinese", "裁决级母舰"),
                                ("english", "Ready? Really?"), ("english", "?")):
            with self.subTest(language=language, value=value):
                path = self.root / f"valid_l_{language}.yml"
                path.write_text(f'l_{language}:\n test_key:0 "{value}"\n', encoding="utf-8-sig")
                errors = []
                self.assertEqual({"test_key": value}, read_localization(path, language, errors))
                self.assertEqual([], errors)

    def test_finite_population_scope_unknown_blocks_package_pass(self) -> None:
        path = self.package / "events/fixture.txt"
        path.parent.mkdir()
        path.write_text("country_event={immediate={every_owned_pop_group={pop_group_size>0}}}", encoding="utf-8")
        result = self.verify()
        self.assertEqual("FAIL", result["status"])
        self.assertEqual("UNKNOWN", result["finite_scope_preflight"]["status"])
        self.assertIn("STELLARIS_SCOPE_UNKNOWN", {error["code"] for error in result["errors"]})

    def test_finite_population_scope_valid_and_invalid_package(self) -> None:
        (self.game / "launcher-settings.json").write_text(json.dumps({"rawVersion": "v4.5.2"}), encoding="utf-8")
        alias = self.game / "common/scripted_triggers/00_scripted_triggers_pop.txt"
        alias.parent.mkdir(parents=True)
        alias.write_text("pop_amount={count_owned_pop_amount={}}", encoding="utf-8")
        path = self.package / "events/fixture.txt"
        path.parent.mkdir()
        path.write_text("country_event={trigger={pop_amount>0} immediate={every_owned_pop_group={pop_group_size>0 prev={pop_amount>0}}}}", encoding="utf-8")
        with patch("tools.stellaris_scope_guard.BUILDS", {("v4.5.2", self.hash)}):
            result = self.verify()
            self.assertEqual("PASS", result["status"], result["errors"])
            self.assertEqual(3, result["finite_scope_preflight"]["checked"])
            path.write_text(path.read_text(encoding="utf-8").replace("pop_group_size", "pop_amount"), encoding="utf-8")
            result = self.verify()
            self.assertEqual("FAIL", result["status"])
            self.assertEqual("FAIL", result["finite_scope_preflight"]["status"])
            self.assertIn("STELLARIS_SCOPE_MISMATCH", {error["code"] for error in result["errors"]})

    def test_referenced_script_value_shape_reaches_package_report(self) -> None:
        (self.game / "launcher-settings.json").write_text(json.dumps({"rawVersion": "v4.5.2"}), encoding="utf-8")
        definition = self.package / "common/script_values/fixture.txt"
        definition.parent.mkdir()
        definition.write_text("coefficient=.05", encoding="utf-8")
        use = self.package / "events/fixture.txt"
        use.parent.mkdir()
        use.write_text("country_event={immediate={set_variable={which=n value=value:coefficient}}}", encoding="utf-8")
        with patch("tools.stellaris_scope_guard.BUILDS", {("v4.5.2", self.hash)}):
            result = self.verify()
            self.assertEqual("FAIL", result["status"])
            self.assertEqual("FAIL", result["finite_script_value_preflight"]["status"])
            self.assertIn("STELLARIS_SCRIPT_VALUE_SCALAR_DEFINITION", {error["code"] for error in result["errors"]})
            definition.write_text("@fixed=.05 coefficient={base=@fixed}", encoding="utf-8")
            self.assertEqual("PASS", self.verify()["status"])


class WeaponCsvContractTest(unittest.TestCase):
    """Isolated CSV association tests do not need the Java CLI or native game."""

    def setUp(self) -> None:
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        root = Path(self.temp.name)
        self.mod = root / "mod"
        self.game = root / "game"
        self.component = Path("common/component_templates")
        self.header = ";".join(WEAPON_COLUMNS)
        self.script(self.mod, 'weapon_component_template = { key = "MOD_WEAPON" }')
        self.table(self.mod)

    def script(self, root: Path, text: str, name: str = "fixture.txt") -> Path:
        path = root / self.component / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(text, encoding="utf-8")
        return path

    def row(self, key: str = "MOD_WEAPON", **values: str) -> str:
        row = dict.fromkeys(WEAPON_COLUMNS, "0.00")
        row.update(key=key, end="")
        row.update(values)
        return ";".join(row[column] for column in WEAPON_COLUMNS)

    def table(self, root: Path, rows: str | None = None, name: str = "fixture_weapon.csv") -> Path:
        path = root / self.component / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(self.header + "\n" + (self.row() if rows is None else rows) + "\n", encoding="utf-8")
        return path

    def verify(self) -> tuple[dict, list[dict]]:
        errors: list[dict] = []
        summary = check_weapon_csv(self.mod, self.game, errors)
        return summary, errors

    def test_accepts_native_comments_empty_numeric_cells_and_negative_power(self) -> None:
        path = self.table(self.mod, self.row(power="-7500.00", missile_speed="", min_windup="90.00", cooldown="30.00"))
        path.write_text("# single-column comment\n# comment;with;columns\n\n" +
                        path.read_text(encoding="utf-8") + "# trailing comment\n", encoding="utf-8")
        summary, errors = self.verify()
        self.assertEqual([], errors)
        self.assertEqual(1, summary["weapon_csv_keys"])

    def test_rejects_invalid_headers_columns_keys_and_end(self) -> None:
        cases = ((self.header.replace(";", ",") + "\n" + self.row(), "WEAPON_CSV_HEADER"),
                 (self.header.replace("cooldown", "delay") + "\n" + self.row(), "WEAPON_CSV_HEADER"),
                 (self.header + "\n" + self.row().removesuffix(";"), "WEAPON_CSV_COLUMNS"),
                 (self.header + "\n" + self.row() + ";", "WEAPON_CSV_COLUMNS"),
                 (self.header + "\n" + self.row(key=""), "WEAPON_CSV_KEY"),
                 (self.header + "\n" + self.row(key='"MOD_WEAPON"'), "WEAPON_CSV_KEY"),
                 (self.header + "\n" + self.row(end="0"), "WEAPON_CSV_END"))
        path = self.mod / self.component / "fixture_weapon.csv"
        for text, code in cases:
            with self.subTest(code=code, text=text[:40]):
                path.write_text(text, encoding="utf-8")
                _, errors = self.verify()
                self.assertTrue(any(error["code"] == code and error["path"] == str(path)
                                    for error in errors), errors)

    def test_rejects_nonfinite_nonnumeric_and_unsupported_numeric_notation(self) -> None:
        for value in ("NaN", "Infinity", "-inf", "word", "1_000", "1e3", "9" * 400):
            with self.subTest(value=value):
                path = self.table(self.mod, self.row(min_windup=value))
                _, errors = self.verify()
                self.assertIn({"code": "WEAPON_CSV_NUMBER", "path": str(path), "line": 2,
                               "key": "MOD_WEAPON", "column": "min_windup", "value": value}, errors)

    def test_rejects_invalid_utf8_and_bom(self) -> None:
        path = self.mod / self.component / "fixture_weapon.csv"
        valid = path.read_bytes()
        for data, code in ((b"\xef\xbb\xbf" + valid, "WEAPON_CSV_BOM"),
                           (valid + b"\xff", "WEAPON_CSV_ENCODING")):
            with self.subTest(code=code):
                path.write_bytes(data)
                _, errors = self.verify()
                self.assertIn({"code": code, "path": str(path)}, errors)

    def test_rejects_duplicate_keys_within_and_across_mod_tables(self) -> None:
        self.table(self.mod, self.row() + "\n" + self.row())
        _, errors = self.verify()
        self.assertIn("WEAPON_CSV_DUPLICATE_KEY", {error["code"] for error in errors})
        self.table(self.mod)
        second = self.table(self.mod, name="other_weapon.csv")
        _, errors = self.verify()
        self.assertTrue(any(error["code"] == "WEAPON_CSV_DUPLICATE_KEY" and
                            error["path"] == str(second) and "first_path" in error for error in errors))

    def test_rejects_missing_definition_and_missing_statistics(self) -> None:
        self.table(self.mod, self.row("WRONG_KEY"))
        _, errors = self.verify()
        self.assertEqual({"WEAPON_CSV_COMPONENT_MISSING", "WEAPON_COMPONENT_STATS_MISSING"},
                         {error["code"] for error in errors})

    def test_accepts_native_definition_and_native_statistics_without_copying_keys(self) -> None:
        self.script(self.game, 'weapon_component_template = { key = "NATIVE_WEAPON" }', "native.txt")
        self.table(self.game, self.row("NATIVE_WEAPON"), "native_weapon.csv")
        # Mod overrides native statistics without needing to copy the native definition.
        self.table(self.mod, self.row() + "\n" + self.row("NATIVE_WEAPON"))
        self.assertEqual([], self.verify()[1])
        # Mod overrides a native definition while using native statistics.
        self.table(self.mod)
        self.script(self.mod, 'weapon_component_template = { key = "NATIVE_WEAPON" }', "override.txt")
        self.assertEqual([], self.verify()[1])

    def test_native_same_path_sources_are_hidden_by_mod_files(self) -> None:
        self.script(self.game, 'weapon_component_template = { key = "NATIVE_WEAPON" }')
        self.table(self.mod, self.row() + "\n" + self.row("NATIVE_WEAPON"))
        self.assertIn("WEAPON_CSV_COMPONENT_MISSING", {error["code"] for error in self.verify()[1]})
        self.script(self.mod, 'weapon_component_template = { key = "MOD_WEAPON" }\n'
                    'weapon_component_template = { key = "NATIVE_WEAPON" }')
        self.table(self.mod)
        self.table(self.game, self.row("NATIVE_WEAPON"))
        self.assertIn("WEAPON_COMPONENT_STATS_MISSING", {error["code"] for error in self.verify()[1]})

    def test_ignores_comment_nested_and_quoted_fake_definitions(self) -> None:
        self.script(self.mod, '# weapon_component_template = { key = "FAKE" }\n'
                    'utility_component_template = { key = "UTILITY" }\n'
                    'unrelated = { weapon_component_template = { key = "NESTED" } }\n'
                    'text = "weapon_component_template = { key = fake }"\n'
                    'weapon_component_template = { # inline comment\n'
                    'key = "MOD_WEAPON" nested = { key = "NESTED" } }')
        self.assertEqual([], self.verify()[1])

    def test_rejects_unresolved_or_ambiguous_mod_component_keys(self) -> None:
        for content in ('weapon_component_template = { key = @name }',
                        'weapon_component_template = { inline_script = { script = fixture } }',
                        'weapon_component_template = { key = "MOD_WEAPON" key = "OTHER" }'):
            with self.subTest(content=content):
                self.script(self.mod, content)
                self.assertIn("WEAPON_COMPONENT_KEY_UNSUPPORTED", {error["code"] for error in self.verify()[1]})
        self.script(self.mod, 'weapon_component_template = { key = MOD_WEAPON }\n'
                    'weapon_component_template = { key = MOD_WEAPON }')
        self.assertIn("WEAPON_COMPONENT_DUPLICATE_KEY", {error["code"] for error in self.verify()[1]})

    def test_unrelated_native_inline_templates_do_not_fail_literal_associations(self) -> None:
        self.script(self.game, 'weapon_component_template = { inline_script = { script = fixture } }', "native_inline.txt")
        summary, errors = self.verify()
        self.assertEqual([], errors)
        self.assertEqual(1, summary["weapon_csv_native_unexpanded_templates"])

    def test_no_csv_is_not_applicable_and_other_directories_are_ignored(self) -> None:
        (self.mod / self.component / "fixture_weapon.csv").unlink()
        self.script(self.mod, 'weapon_component_template = { key = "NO_STATS" }')
        (self.mod / "other.csv").write_bytes(b"not a weapon table")
        summary, errors = self.verify()
        self.assertEqual([], errors)
        self.assertEqual(0, summary["weapon_csv_files"])
        self.assertEqual("not_applicable_no_weapon_csv_files", summary["weapon_csv_applicability"])


if __name__ == "__main__":
    unittest.main()
