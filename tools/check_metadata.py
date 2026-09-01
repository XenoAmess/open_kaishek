#!/usr/bin/env python3
"""Offline project metadata and license gate for the standalone repository."""

from __future__ import annotations

import hashlib
import re
import sys
from pathlib import Path

EXPECTED_LICENSE_SHA256 = "3972DC9744F6499F0F9B2DBF76696F2AE7AD8AF9B23DDE66D6AF86C9DFB36986"
EXPECTED_REPOSITORY_URL = "https://github.com/XenoAmess/open_kaishek"
EXPECTED_LICENSE_URL = "https://www.gnu.org/licenses/gpl-3.0.txt"


def fail(message: str) -> None:
    print(f"metadata: FAIL: {message}", file=sys.stderr)
    raise SystemExit(1)


def main() -> int:
    root = Path(__file__).resolve().parents[1]
    license_path = root / "LICENSE"
    pom_path = root / "pom.xml"
    if not license_path.is_file():
        fail("root LICENSE is missing")
    if not pom_path.is_file():
        fail("root pom.xml is missing")

    license_bytes = license_path.read_bytes()
    digest = hashlib.sha256(license_bytes).hexdigest().upper()
    if digest != EXPECTED_LICENSE_SHA256:
        fail(f"LICENSE SHA-256 {digest} does not match the standard GPLv3 text")
    if not license_bytes.startswith(b"                    GNU GENERAL PUBLIC LICENSE\n                       Version 3,"):
        fail("LICENSE is not GNU GPL version 3")

    pom = pom_path.read_text(encoding="utf-8")
    if "GPL-3.0-only" not in pom:
        fail("pom.xml does not declare GPL-3.0-only")
    if EXPECTED_LICENSE_URL not in pom:
        fail("pom.xml does not point at the GPLv3 license URL")
    if EXPECTED_REPOSITORY_URL not in pom:
        fail("pom.xml still points at the parent repository")
    if re.search(r"<name>\s*Apache-2\.0\s*</name>", pom, re.IGNORECASE):
        fail("pom.xml contains an Apache project license marker")

    print("metadata: PASS (GPL-3.0-only, repository URL, and canonical license hash)")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
