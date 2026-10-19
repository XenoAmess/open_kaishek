#!/usr/bin/env python3
"""Validate a Stellaris Mod package with Kaishek syntax and static contracts.

This check deliberately makes no opcode or runtime-semantic claim for paths not
registered by a game-version profile. A passing report must be paired with a
separate Stellaris runtime test for behavioral acceptance.
"""

from __future__ import annotations

import argparse
from collections import Counter
import hashlib
import json
import math
from pathlib import Path
import re
import subprocess
import sys

if __package__:
    from tools.stellaris_scope_guard import population_scope_preflight, script_value_shape_preflight
else:
    from stellaris_scope_guard import population_scope_preflight, script_value_shape_preflight


ROOT = Path(__file__).resolve().parents[1]
CLI = ROOT / "kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar"
CYGNUS_451_SHA256 = "6fe06709f265e726722dc23f617c5fc4e5557629e2d43fe312016aba547c83e4"
LOCALIZATION = re.compile(r'^ ([^:\s]+):\d* "((?:[^"\\]|\\.)*)"$')
TOKEN = re.compile(r"\$[^$\r\n]+\$|§.|\\.")
ASSET = re.compile(r'"(gfx/[^"\r\n]+\.dds)"', re.IGNORECASE)
VERSION = re.compile(r"\d+\.\d+\.\d+(?:-[0-9A-Za-z.-]+)?")
WEAPON_COLUMNS = tuple((
    "key;cost;power;min_damage;max_damage;hull_damage;shield_damage;shield_penetration;"
    "armor_damage;armor_penetration;min_windup;max_windup;cooldown;range;accuracy;tracking;"
    "size_damage_factor;military_power_multiplier;missile_speed;missile_evasion;"
    "missile_shield;missile_armor;missile_health;missile_retarget_range;end"
).split(";"))
WEAPON_NUMBER = re.compile(r"[+-]?(?:[0-9]+(?:\.[0-9]*)?|\.[0-9]+)")
# This is a declaration projection, not a second P parser. Kaishek still parses P.
COMPONENT_TOKEN = re.compile(r'#[^\r\n]*|"(?:\\.|[^"\\])*"|[{}=]|[^\s{}="#]+')


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def read_localization(path: Path, language: str, errors: list[dict]) -> dict[str, str]:
    data = path.read_bytes()
    if not data.startswith(b"\xef\xbb\xbf"):
        errors.append({"code": "LOCALIZATION_BOM", "path": str(path)})
    try:
        lines = data.decode("utf-8-sig").splitlines()
    except UnicodeDecodeError:
        errors.append({"code": "LOCALIZATION_ENCODING", "path": str(path)})
        return {}
    if not lines or lines[0] != f"l_{language}:":
        errors.append({"code": "LOCALIZATION_HEADER", "path": str(path)})
    entries: dict[str, str] = {}
    for number, line in enumerate(lines[1:], 2):
        if not line.strip() or line.lstrip().startswith("#"):
            continue
        match = LOCALIZATION.fullmatch(line)
        if not match:
            errors.append({"code": "LOCALIZATION_SYNTAX", "path": str(path), "line": number})
            continue
        key, value = match.groups()
        if key in entries:
            errors.append({"code": "LOCALIZATION_DUPLICATE_KEY", "path": str(path), "key": key})
        if not value:
            errors.append({"code": "LOCALIZATION_EMPTY_VALUE", "path": str(path), "key": key})
        if "\ufffd" in value or re.search(r"\?{4,}", value):
            errors.append({"code": "LOCALIZATION_CHARACTER_LOSS", "path": str(path),
                           "key": key, "line": number,
                           "reason": "replacement_character" if "\ufffd" in value else "consecutive_question_marks"})
        entries[key] = value
    return entries


def read_weapon_csv(path: Path, errors: list[dict], *, native: bool = False) -> dict[str, int]:
    """Read the observed unquoted, semicolon-delimited native weapon table."""
    data = path.read_bytes()
    if not native and data.startswith(b"\xef\xbb\xbf"):
        errors.append({"code": "WEAPON_CSV_BOM", "path": str(path)})
    try:
        lines = data.decode("utf-8-sig").splitlines()
    except UnicodeDecodeError:
        errors.append({"code": "WEAPON_CSV_ENCODING", "path": str(path)})
        return {}
    rows = [(number, line.split(";")) for number, line in enumerate(lines, 1)
            if line.strip() and not line.startswith("#")]
    if not rows or tuple(rows[0][1]) != WEAPON_COLUMNS:
        errors.append({"code": "WEAPON_CSV_HEADER", "path": str(path),
                       "line": rows[0][0] if rows else 1})
        return {}
    keys: dict[str, int] = {}
    for number, row in rows[1:]:
        location = {"path": str(path), "line": number}
        if len(row) != len(WEAPON_COLUMNS):
            errors.append({"code": "WEAPON_CSV_COLUMNS", **location,
                           "expected": len(WEAPON_COLUMNS), "actual": len(row)})
            continue
        key = row[0]
        if not key or '"' in key or any(char.isspace() for char in key):
            errors.append({"code": "WEAPON_CSV_KEY", **location, "key": key})
            continue
        if key in keys:
            errors.append({"code": "WEAPON_CSV_DUPLICATE_KEY", **location,
                           "key": key, "first_line": keys[key]})
        keys[key] = number
        if row[-1]:
            errors.append({"code": "WEAPON_CSV_END", **location, "key": key})
        if native:
            continue  # Native tables supply reference keys, not Mod numeric acceptance.
        for column, value in zip(WEAPON_COLUMNS[1:-1], row[1:-1]):
            if value and (not WEAPON_NUMBER.fullmatch(value) or not math.isfinite(float(value))):
                errors.append({"code": "WEAPON_CSV_NUMBER", **location,
                               "key": key, "column": column, "value": value})
    return keys


def weapon_component_keys(path: Path, errors: list[dict]) -> list[tuple[str, int]]:
    """Project direct literal keys from root weapon_component_template blocks."""
    try:
        text = path.read_text(encoding="utf-8-sig")
    except UnicodeDecodeError:
        # The package's existing P encoding check diagnoses Mod scripts separately.
        errors.append({"code": "WEAPON_COMPONENT_ENCODING", "path": str(path)})
        return []
    matches = [match for match in COMPONENT_TOKEN.finditer(text) if not match[0].startswith("#")]
    tokens = [match[0] for match in matches]
    result: list[tuple[str, int]] = []
    depth = 0
    active = False
    keys: list[str | None] = []
    block_line = 1
    for index, token in enumerate(tokens):
        if token == "{":
            if depth == 0 and tokens[max(0, index - 2):index] == ["weapon_component_template", "="]:
                active = True
                keys = []
                block_line = text.count("\n", 0, matches[index].start()) + 1
            depth += 1
        elif token == "}":
            depth -= 1
            if depth == 0 and active:
                if len(keys) != 1 or keys[0] is None:
                    errors.append({"code": "WEAPON_COMPONENT_KEY_UNSUPPORTED", "path": str(path),
                                   "line": block_line,
                                   "reason": "requires one direct literal key; variables are not evaluated"})
                else:
                    result.append((keys[0], block_line))
                active = False
        elif active and depth == 1 and token == "key" and tokens[index + 1:index + 2] == ["="]:
            value = tokens[index + 2] if index + 2 < len(tokens) else ""
            if value.startswith('"') and value.endswith('"') and "\\" not in value:
                value = value[1:-1]
            elif '"' in value or value.startswith("@") or value in {"{", "}", "="}:
                value = ""
            keys.append(value or None)
    return result


def check_weapon_csv(package: Path, game: Path, errors: list[dict]) -> dict:
    """Check Mod tables and literal key associations; do not execute weapon semantics."""
    component_root = Path("common/component_templates")
    def files(root: Path, suffix: str) -> list[Path]:
        return sorted(path for path in (root / component_root).rglob("*")
                      if path.is_file() and path.suffix.lower() == suffix)

    tables = files(package, ".csv")
    summary = {"weapon_csv_files": len(tables), "weapon_csv_keys": 0,
               "weapon_csv_applicability": "checked" if tables else "not_applicable_no_weapon_csv_files"}
    if not tables:
        return summary
    mod_keys: dict[str, dict] = {}
    for path in tables:
        for key, line in read_weapon_csv(path, errors).items():
            location = {"path": str(path), "line": line}
            if key in mod_keys:
                errors.append({"code": "WEAPON_CSV_DUPLICATE_KEY", **location,
                               "key": key, "first_path": mod_keys[key]["path"]})
            mod_keys[key] = location
    summary["weapon_csv_keys"] = len(mod_keys)
    mod_scripts = files(package, ".txt")
    hidden = {path.relative_to(package).as_posix().casefold() for path in [*tables, *mod_scripts]}
    native_keys: set[str] = set()
    native_definitions: set[str] = set()
    native_errors: list[dict] = []
    for path in files(game, ".csv"):
        if path.relative_to(game).as_posix().casefold() not in hidden:
            native_keys.update(read_weapon_csv(path, native_errors, native=True))
    for path in files(game, ".txt"):
        if path.relative_to(game).as_posix().casefold() not in hidden:
            native_definitions.update(key for key, _ in weapon_component_keys(path, native_errors))
    summary["weapon_csv_native_unexpanded_templates"] = sum(
        error["code"] == "WEAPON_COMPONENT_KEY_UNSUPPORTED" for error in native_errors)
    for error in native_errors:
        if error["code"] != "WEAPON_COMPONENT_KEY_UNSUPPORTED":
            errors.append({**error, "code": "WEAPON_CSV_GAME_REFERENCE", "cause": error["code"]})
    mod_definitions: dict[str, dict] = {}
    for path in mod_scripts:
        for key, line in weapon_component_keys(path, errors):
            location = {"path": str(path), "line": line}
            if key in mod_definitions:
                errors.append({"code": "WEAPON_COMPONENT_DUPLICATE_KEY", **location, "key": key})
            mod_definitions[key] = location
    for key in sorted(mod_keys.keys() - mod_definitions.keys() - native_definitions):
        errors.append({"code": "WEAPON_CSV_COMPONENT_MISSING", **mod_keys[key], "key": key})
    for key in sorted(mod_definitions.keys() - mod_keys.keys() - native_keys):
        errors.append({"code": "WEAPON_COMPONENT_STATS_MISSING", **mod_definitions[key], "key": key})
    return summary


def check(mod: Path, game: Path, expected_sha: str, cli: Path, version_source: Path | None = None) -> dict:
    errors: list[dict] = []
    package = mod / "mod" if (mod / "mod/descriptor.mod").is_file() else mod
    descriptor = package / "descriptor.mod"
    exe = game / "stellaris.exe"
    game_sha = sha256(exe) if exe.is_file() else None
    if game_sha is None or game_sha.lower() != expected_sha.lower():
        errors.append({"code": "GAME_EXE_IDENTITY", "path": str(exe)})
    if not cli.is_file():
        errors.append({"code": "KAISHEK_CLI_MISSING", "path": str(cli)})
    version_file = version_source or (mod / "VERSION" if (mod / "VERSION").is_file() else package / "VERSION")
    if not version_file.is_file() or not descriptor.is_file():
        errors.append({"code": "MOD_METADATA_MISSING", "path": str(mod)})
    else:
        version = version_file.read_text(encoding="utf-8-sig").strip()
        text = descriptor.read_text(encoding="utf-8-sig")
        match = re.search(r'^version\s*=\s*"([^"]+)"', text, re.MULTILINE)
        supported = re.search(r'^supported_version\s*=\s*"([^"]+)"', text, re.MULTILINE)
        if not VERSION.fullmatch(version) or not match or match.group(1) != version or not supported:
            errors.append({"code": "MOD_VERSION_CONTRACT", "path": str(descriptor)})
        settings_file = game / "launcher-settings.json"
        if supported and settings_file.is_file():
            settings = json.loads(settings_file.read_text(encoding="utf-8-sig"))
            actual = settings.get("rawVersion", "").removeprefix("v").split(".")
            declared = supported.group(1).split(".")
            if not actual or not all(part == "*" or (index < len(actual) and part == actual[index])
                                     for index, part in enumerate(declared)):
                errors.append({"code": "MOD_SUPPORTED_VERSION", "path": str(descriptor),
                               "declared": supported.group(1), "game": settings.get("rawVersion")})
    script_files = sorted(
        path for path in package.rglob("*")
        if path.is_file() and (path.suffix.lower() in {".txt", ".gfx", ".gui", ".asset"} or path.name == "descriptor.mod")
    )
    parsed = 0
    scope_sources: list[tuple[Path, str]] = []
    if not script_files:
        errors.append({"code": "NO_P_SCRIPTS", "path": str(package)})
    for path in script_files:
        data = path.read_bytes()
        if data.startswith(b"\xef\xbb\xbf"):
            errors.append({"code": "P_SCRIPT_BOM", "path": str(path)})
            continue
        try:
            source = data.decode("utf-8")
        except UnicodeDecodeError:
            errors.append({"code": "P_SCRIPT_ENCODING", "path": str(path)})
            continue
        scope_sources.append((path, source))
        if cli.is_file():
            command = ["java", "-jar", str(cli), "parse", "--file", str(path)]
            result = subprocess.run(command, capture_output=True, text=True, encoding="utf-8", timeout=60)
            try:
                payload = json.loads(result.stdout)
            except json.JSONDecodeError:
                errors.append({"code": "KAISHEK_RESPONSE", "path": str(path)})
                continue
            if result.returncode or payload.get("status") != "PARSED" or payload.get("diagnostics") or not payload.get("roundTrip"):
                errors.append({"code": "P_SCRIPT_PARSE", "path": str(path), "diagnostics": payload.get("diagnostics", [])})
            else:
                parsed += 1
        for asset in ASSET.findall(source):
            relative = Path(asset)
            if not (package / relative).is_file() and not (game / relative).is_file():
                errors.append({"code": "ASSET_REFERENCE_MISSING", "path": str(path), "asset": asset})
    finite_scope = population_scope_preflight(package, game, scope_sources, game_sha)
    errors.extend(finite_scope["diagnostics"])
    finite_values = script_value_shape_preflight(package, game, scope_sources, game_sha)
    errors.extend(finite_values["diagnostics"])
    dds = sorted(package.rglob("*.dds"))
    for path in dds:
        if len(path.read_bytes()) < 128 or not path.read_bytes().startswith(b"DDS "):
            errors.append({"code": "DDS_HEADER", "path": str(path)})
    game_languages = sorted(path.name for path in (game / "localisation").iterdir() if path.is_dir()) if (game / "localisation").is_dir() else []
    if not game_languages:
        errors.append({"code": "GAME_LANGUAGES_MISSING", "path": str(game / "localisation")})
    locales: dict[str, dict[str, str]] = {}
    localization_root = package / "localisation"
    has_localization = any(localization_root.rglob("*.yml"))
    for language in game_languages if has_localization else []:
        files = sorted(localization_root.rglob(f"*_l_{language}.yml"))
        if not files:
            errors.append({"code": "LOCALIZATION_LANGUAGE_MISSING", "language": language})
            continue
        values: dict[str, str] = {}
        for path in files:
            for key, value in read_localization(path, language, errors).items():
                if key in values:
                    errors.append({"code": "LOCALIZATION_DUPLICATE_KEY", "language": language, "key": key})
                values[key] = value
        locales[language] = values
    if "simp_chinese" in locales:
        reference = locales["simp_chinese"]
        for language, values in locales.items():
            missing = sorted(set(reference) - set(values))
            extra = sorted(set(values) - set(reference))
            if missing or extra:
                errors.append({"code": "LOCALIZATION_KEY_SET", "language": language, "missing": missing, "extra": extra})
            for key in set(values) & set(reference):
                if Counter(TOKEN.findall(values[key])) != Counter(TOKEN.findall(reference[key])):
                    errors.append({"code": "LOCALIZATION_TOKEN_DRIFT", "language": language, "key": key})
                if language != "simp_chinese" and values[key] == reference[key] and re.search(r"[\u4e00-\u9fff]", values[key]):
                    errors.append({"code": "LOCALIZATION_CHINESE_PLACEHOLDER", "language": language, "key": key})
    weapon_csv = check_weapon_csv(package, game, errors)
    return {
        "status": "PASS" if not errors else "FAIL",
        "game_exe_sha256": game_sha,
        "p_scripts": len(script_files), "p_parsed": parsed, "dds": len(dds),
        "languages": game_languages,
        "localization_keys": len(locales.get("simp_chinese", {})),
        "localization_applicability": "checked" if has_localization else "not_applicable_no_localization_files",
        **weapon_csv,
        "finite_scope_preflight": finite_scope,
        "finite_script_value_preflight": finite_values,
        "semantic_scope": "syntax/package including weapon CSV literal-key associations plus explicitly registered finite population scopes and referenced script-value shapes only; runtime, recursive expansion and other opcodes require Stellaris tests",
        "errors": errors,
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--mod", required=True, type=Path)
    parser.add_argument("--game", required=True, type=Path)
    parser.add_argument("--report", required=True, type=Path)
    parser.add_argument("--expected-exe-sha256", default=CYGNUS_451_SHA256)
    parser.add_argument("--cli", type=Path, default=CLI)
    parser.add_argument("--version-file", type=Path, help="explicit VERSION source for a package in a multi-Mod repository")
    args = parser.parse_args()
    result = check(args.mod.resolve(), args.game.resolve(), args.expected_exe_sha256, args.cli.resolve(),
                   args.version_file.resolve() if args.version_file else None)
    args.report.parent.mkdir(parents=True, exist_ok=True)
    args.report.write_text(json.dumps(result, ensure_ascii=False, indent=2), encoding="utf-8")
    print(json.dumps({key: result[key] for key in ("status", "p_scripts", "p_parsed", "dds", "localization_keys")}, ensure_ascii=False))
    return 0 if result["status"] == "PASS" else 1


if __name__ == "__main__":
    raise SystemExit(main())
