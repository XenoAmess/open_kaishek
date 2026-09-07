#!/usr/bin/env python3
"""Run the standalone CLI smoke against the Maven-shaded application JAR."""

from __future__ import annotations

import os
import shlex
import shutil
import subprocess
import sys
from pathlib import Path


def fail(message: str) -> None:
    print(f"cli smoke: FAIL: {message}", file=sys.stderr)
    raise SystemExit(1)


def java_executable() -> str:
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidate = Path(java_home) / "bin" / ("java.exe" if os.name == "nt" else "java")
        if candidate.is_file():
            return str(candidate)
    candidate = shutil.which("java")
    if candidate:
        return candidate
    fail("java is not available through JAVA_HOME or PATH")


def run(root: Path, command: list[str]) -> None:
    print("+ " + shlex.join(command), flush=True)
    subprocess.run(command, cwd=root, check=True)


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    target = root / "kaishek-cli" / "target"
    jars = sorted(target.glob("kaishek-cli-*.jar"))
    if len(jars) != 1:
        fail(f"expected exactly one packaged CLI JAR, found {len(jars)}: {jars}")

    test_classes = target / "test-classes"
    if not test_classes.is_dir():
        fail(f"CLI test classes are missing: {test_classes}")

    java = java_executable()
    cli_jar = jars[0]
    run(
        root,
        [
            java,
            "-ea",
            "-cp",
            os.pathsep.join((str(test_classes), str(cli_jar))),
            "com.xenoamess.kaishek.cli.KaishekCliSmokeTest",
        ],
    )
    run(
        root,
        [
            java,
            "-ea",
            "-jar",
            str(cli_jar),
            "preflight",
            "--fixture",
            "synthetic-361-014",
        ],
    )
    print(f"cli smoke: PASS ({cli_jar.relative_to(root).as_posix()})")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
