"""Contract tests for the Stellaris Mod package checker."""

from __future__ import annotations

import hashlib
import json
from pathlib import Path
import tempfile
import unittest

from tools.accept_stellaris_mod import CLI, check


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
        self.assertEqual("PASS", self.verify()["status"])
        path.write_text(path.read_text(encoding="utf-8").replace("test_key:", "test_key"), encoding="utf-8")
        self.assertIn("LOCALIZATION_SYNTAX", {error["code"] for error in self.verify()["errors"]})


if __name__ == "__main__":
    unittest.main()
