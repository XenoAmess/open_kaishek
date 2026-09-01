# M1 / Phase 1 formal readiness record

Date: 2026-09-01 (Asia/Shanghai)
Repository: `XenoAmess/open_kaishek`
Decision: **M1 `static-ready` / complete for the frozen corpus and bounded
property campaign**

This record closes the Phase 1 (Lossless Parser) exit gate from the roadmap.
It is a static, offline decision: it does not certify CK3 execution, native
semantics, MCP differential behavior, or a production runtime.

## Scope that is closed

The released baseline provides:

* byte-oriented UTF-8-aware lexing with BOM, mixed newline, comment, string,
  number, variable, operator, and malformed-byte recovery;
* an ordered, lossless CST that retains duplicate keys, source order, trivia,
  source spans, and original bytes;
* Paradox attributes, comparisons, blocks, bare values/lists, scripted
  variables, parameters, conditional blocks, and conservative Phase 1 roles
  for bracket lists, inline property/conditional/math expressions, scope
  chains, and reader directives;
* explicit parser, corpus, malformed-input, duplicate-key, and deterministic
  property/fuzz self-tests.

The target `mod_zhongguo_style` files are an external read-only corpus. Only
its relative-path manifest and hashes are published; CK3 or mod source bytes
are not redistributed by this repository.

## Exit-gate evidence

| Gate | Result | Evidence |
|---|---|---|
| Raw-byte input and emission | PASS | `ParserSelfTest`, `Phase1SyntaxSelfTest`, and corpus checks assert byte-for-byte `parse → emit`. |
| Required Phase 1 forms | PASS (documented baseline) | Structured regression covers lists, bracket expressions, comparison/math operators, conditional blocks, parameters, scope chains, and reader directives. |
| Frozen target corpus | PASS | 27/27 `.txt`/`.gui` files parsed; 2,677,440 bytes; zero error diagnostics; zero round-trip differences. |
| Duplicate/order preservation | PASS | `DuplicateKeyRoundTripSelfTest` and ordered CST entries retain repeated declarations without a `Map`. |
| Malformed recovery | PASS | Unclosed blocks/brackets, stray delimiters, unterminated strings, and invalid UTF-8 produce stable diagnostics and preserve bytes. |
| Property/fuzz seed | PASS (bounded) | Seed `0x4b41495348454b31`, 760 cases, 199 valid-UTF-8 and 561 arbitrary-byte cases; no parser exceptions or byte changes. |
| Offline build | PASS | Maven reactor `clean test` succeeds with the pinned local cache; Python schema checks report 8/8 passing. |
| Packaging reproducibility | PASS (host baseline) | Two clean packages under UTC and Asia/Shanghai produced identical SHA-256 for all 11 module JARs. |

## Reproduce

From `Z:\workspace\open_kaishek` on the frozen Windows host:

```powershell
$m2 = Join-Path $env:USERPROFILE '.m2\repository'
$corpus = 'Z:\ck3_mod_rewrite\mod_zhongguo_style'
mvn -o -ntp "-Dmaven.repo.local=$m2" clean test
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.ParserSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.DuplicateKeyRoundTripSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.Phase1SyntaxSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.ParserPropertyFuzzSelfTest
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' com.xenoamess.kaishek.syntax.ParserCorpusRoundTripSelfTest --root $corpus --require-corpus
py -3 -m unittest discover -s kaishek-zg361-profile/tests -v
py -3 kaishek-zg361-profile/tools/validate_domains.py
```

The CLI corpus command is an additional packaged check:

```powershell
mvn -o -ntp "-Dmaven.repo.local=$m2" -DskipTests package
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar corpus $corpus
```

If the external corpus is absent, the parser self-test may report `SKIP`; that
condition is not a passing M1 corpus result.

## Readiness boundary and follow-up

M1 is `static-ready` for the frozen corpus and the bounded campaign above. It
is not a claim of exhaustive Paradox grammar coverage. The following remain
explicit follow-up work:

* a larger mutation/fuzz matrix and full language-construct inventory;
* schema-aware validation of all 361 business paths (M2/M4);
* exact-build CK3 paused artifacts and MCP differential certification (M5);
* native/runtime and Quarkus production integration.

Until those artifacts exist, no result from this record may be labelled
`fixture-live`, `differential-certified`, `production-live`, or equivalent.
