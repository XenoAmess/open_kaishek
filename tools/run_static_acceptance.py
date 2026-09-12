#!/usr/bin/env python3
"""Run the repository-owned offline acceptance checks from one entry point."""

from __future__ import annotations

import argparse
import os
import shlex
import shutil
import subprocess
import sys
from pathlib import Path


PARSER_SMOKES = (
    "ParserSelfTest",
    "DuplicateKeyRoundTripSelfTest",
    "Phase1SyntaxSelfTest",
    "ParserPropertyFuzzSelfTest",
)


def executable(name: str) -> str:
    candidate = shutil.which(name)
    if candidate:
        return candidate
    raise SystemExit(f"static acceptance: FAIL: {name} is not available on PATH")


def java_executable() -> str:
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidate = Path(java_home) / "bin" / ("java.exe" if os.name == "nt" else "java")
        if candidate.is_file():
            return str(candidate)
    return executable("java")


def run(root: Path, command: list[str]) -> None:
    print("+ " + shlex.join(command), flush=True)
    subprocess.run(command, cwd=root, check=True)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Run repository-owned static checks without CK3 or an external corpus.")
    parser.add_argument(
        "--online",
        action="store_true",
        help="allow Maven dependency resolution instead of passing -o",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    root = Path(__file__).resolve().parents[1]
    python = sys.executable
    maven = executable("mvn")
    java = java_executable()

    run(root, [python, "tools/check_metadata.py"])
    maven_command = [maven]
    if not args.online:
        maven_command.append("-o")
    run(root, [*maven_command, "-ntp", "clean", "package"])
    run(
        root,
        [python, "-m", "unittest", "discover", "-s", "kaishek-zg361-profile/tests", "-v"],
    )
    run(root, [python, "kaishek-zg361-profile/tools/validate_domains.py"])

    syntax_classpath = os.pathsep.join(
        (
            str(root / "kaishek-syntax" / "target" / "classes"),
            str(root / "kaishek-syntax" / "target" / "test-classes"),
        )
    )
    for smoke in PARSER_SMOKES:
        run(
            root,
            [
                java,
                "-ea",
                "-cp",
                syntax_classpath,
                f"com.xenoamess.kaishek.syntax.{smoke}",
            ],
        )
    run(root, [python, "tools/run_cli_smoke.py"])
    print("static acceptance: PASS (repository-owned offline checks; CK3/external corpus not run)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
