# M0 formal readiness record

Date: 2026-09-01 (Asia/Shanghai)
Repository: 'XenoAmess/open_kaishek'
Decision: **M0 static-ready / complete for the recorded scope**

This record closes the first engineering gate defined by the roadmap: contracts,
version pinning, license, an offline build, and reproducible evidence. It does
not claim that the project is a CK3 implementation, a live MCP adapter, or a
product-live runtime.

## Scope and exact boundary

M0 covers the repository that can be checked without starting CK3:

* lossless source/CST and diagnostic contracts;
* CK3 1.19.0.6 profile identity and conservative opcode/schema metadata;
* validator, strict IR, finite runtime, snapshot/trace and differential-contract
  data types;
* the synthetic 014 vertical slice;
* build/license/dependency provenance and standalone-repository hygiene.

The following are deliberately outside this decision: every one of the 361
production paths, exact-build CK3 execution, paused live artifacts, MCP
differential certification, and the Quarkus service. The parser now has a
structured Phase 1 baseline (lists, bracket expressions, conditional/math
roles, scope chains, and reader directives) plus a bounded property/fuzz seed;
an exhaustive language matrix remains a later expansion.

## Review checklist and evidence

| Gate | Result | Evidence |
|---|---|---|
| Project license | PASS | Root LICENSE is GNU GPLv3; POM declares GPL-3.0-only; migration analysis is in [license-audit.md](../license-audit.md). |
| Dependency boundary | PASS | Core modules have no runtime dependencies; test/build artifacts and their upstream licenses are listed in [THIRD_PARTY_LOCK](../../THIRD_PARTY_LOCK). |
| Version identity | PASS | CK3 profile is 1.19.0.6, EXE SHA-256 2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86; JDK is Temurin 21.0.10+7; Maven host baseline is 3.6.3. |
| Contract instances | PASS | Profile, validator, IR, runtime, diff-contract, and synthetic-pipeline tests instantiate the public records and assert invalid/unsupported paths fail closed. |
| Offline build | PASS | 'mvn -o -ntp "-Dmaven.repo.local=$m2" clean test' completes the reactor successfully with the cached artifacts. |
| Deterministic package | PASS (host baseline) | Parent POM freezes project.build.outputTimestamp=2026-01-01T00:00:00Z; two clean offline packages produced identical module JAR hashes on the verification host. |
| Parser corpus evidence | PASS with external input | The 27-file mod_zhongguo_style corpus round-trips byte-for-byte (2,677,440 bytes, zero diagnostics); the corpus is external and is not redistributed here. |
| Phase 1 syntax baseline | PASS (bounded) | Phase1SyntaxSelfTest covers bracket lists, inline math/conditional/property roles, scope chains, reader directives, malformed brackets, and lexer delimiters; ParserPropertyFuzzSelfTest covers 760 deterministic cases. |
| Synthetic execution | PASS | delivered -> appeal_open -> closed runs through generated BOM source, parser, validator, strict IR, and finite runtime; negative unknown/not-certified paths return unsupported without writes. |
| Standalone hygiene | PASS | Export contains source/docs/resources only; target/, .tmp-*, CK3 installation files, nested repositories, and symlinks are excluded. |
| Host/platform scope | LIMITED | Windows 11 host was exercised. Linux/macOS and a fresh dependency cache were not exercised in this record. |

## Reproduce on Windows

Run from the repository root:

```powershell
$m2 = Join-Path $env:USERPROFILE '.m2\repository'
$env:MAVEN_OPTS = "-Duser.home=$env:USERPROFILE"
mvn -o -ntp "-Dmaven.repo.local=$m2" clean test
mvn -o -ntp "-Dmaven.repo.local=$m2" -DskipTests package
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.Phase1SyntaxSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.ParserPropertyFuzzSelfTest
py -3 -m unittest discover -s kaishek-zg361-profile/tests -v
py -3 kaishek-zg361-profile/tools/validate_domains.py
```

To run the optional corpus check, provide the external source explicitly:

```powershell
$corpus = 'Z:\ck3_mod_rewrite\mod_zhongguo_style'
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' `
  com.xenoamess.kaishek.syntax.ParserCorpusRoundTripSelfTest `
  --root $corpus --require-corpus
```

An unavailable external corpus is a documented 'SKIP', never a passing empty
inventory.

## Readiness matrix after this review

* **M0:** 'static-ready' / complete for the scope above.
* **M1 parser:** `static-ready` for the documented Phase 1 baseline; the
  structured regression and bounded property/fuzz seed pass. Exhaustive grammar
  coverage and larger mutation campaigns remain follow-up work.
* **M2 validator:** static profile/schema fixtures are available; complete
  historical mutation coverage is follow-up work.
* **M3 runtime:** finite primitives and synthetic 014 only.
* **M4 361:** synthetic 014 only; full B2/Workforce exact-build range is not
  certified.
* **M5 CK3 MCP:** 'not-available'; no paused pre/post artifact exists.
* **Quarkus:** shell/scaffold only.

No status above is 'fixture-live', 'production-live', 'differential-certified',
or 'complete' for CK3 semantics. A future gate must attach exact-build
artifacts and rerun the relevant matrix before changing those labels.
