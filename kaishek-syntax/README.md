# kaishek-syntax

Dependency-free Java 21 lossless lexer and concrete syntax tree for Paradox
script files. `Parser.parse(byte[])` copies the input, retains byte offsets,
comments, whitespace/newline style, duplicate keys and source order, and emits
the exact original bytes through `ParseResult.emit()`. Parsing is deliberately
syntax-only; profile validation belongs to the validator module.

Malformed strings, unmatched braces and missing operators/values are recovered
in-place and reported as `Diagnostic` values. A block's children include its
brace tokens and trivia, while list items without an operator are represented by
`SyntaxKind.LIST_ITEM`.

## Phase 1 structured forms

Square brackets are retained as first-class `LBRACKET`/`RBRACKET` tokens and
produce a lossless `BracketNode`.  The parser conservatively exposes an
`ListNode` for comma- or multi-term bracket lists and an `ExpressionNode` for
inline property, conditional, or arithmetic forms (`BracketRole`).  Nested
brackets, comments, whitespace, and delimiters remain in source order, so
`ParseResult.emit()` is still byte-for-byte identical to the input.

Leaf atoms retain the Phase 0 `SyntaxKind` values for downstream compatibility;
`AtomNode.role()` supplies the Phase 1 typing for scripted variables (`@x`),
parameters (`$NAME$`), interpolated parameters, scope/reference chains, reader
directives, and conditional keys.  Arithmetic and conditional operators inside
brackets are separately tokenized by `Lexer`.

This is intentionally a syntax/CST layer, not an evaluator.  Quoted GUI
expressions, the complete parameter-substitution grammar, anonymous GUI blocks,
and profile-specific list semantics remain explicit follow-up work; unknown
forms are retained and fail closed with diagnostics rather than being rewritten.

Run the dependency-free smoke test after compiling with JDK 21:

```text
javac -d target/classes $(find src/main/java src/test/java -name '*.java')
java -cp target/classes com.xenoamess.kaishek.syntax.ParserSelfTest
```

After the Maven test classes have been compiled from the standalone repository
root, run the deterministic property/fuzz smoke as well:

```powershell
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' `
  com.xenoamess.kaishek.syntax.ParserPropertyFuzzSelfTest
```

Run the structured Phase 1 syntax regression alongside it:

```powershell
java -ea -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' `
  com.xenoamess.kaishek.syntax.Phase1SyntaxSelfTest
```

The default run covers 760 generated cases with seed
`0x4b41495348454b31`. This is reproducible static evidence, not a claim of
exhaustive fuzzing or CK3 semantic equivalence.

To exercise the CK3 mod corpus (when the separately supplied corpus is
available), run the byte-preserving corpus smoke test from the standalone
repository root. The corpus is intentionally not checked into this repository;
point `--root` at the local `mod_zhongguo_style` directory (the path below is
the original workspace location):

```powershell
$corpus = 'Z:\ck3_mod_rewrite\mod_zhongguo_style'  # replace with your local corpus path
java -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' `
  com.xenoamess.kaishek.syntax.ParserCorpusRoundTripSelfTest --root "$corpus"
```

The test also covers CK3 GUI declaration forms and expected recovery
diagnostics. An absent corpus is reported as `SKIP`; use `--require-corpus`
when the external corpus is a required input for a verification run. The
round-trip result contributes to the `static-ready` M0 evidence documented in
[`docs/phase0-verification.md`](../docs/phase0-verification.md), but it does
not establish CK3 live or MCP differential readiness.

## Deterministic property/fuzz seeds

`ParserPropertyFuzzSelfTest` is a dependency-free, offline harness. It runs
fixed syntax and arbitrary-byte seeds, deterministic byte mutations, and a
seeded random stream. For every case it checks that parsing is total, emitted
bytes equal the original bytes, source spans stay in bounds, and diagnostics
are stable across two parses. Valid UTF-8 may still have ordinary syntax
diagnostics; malformed UTF-8 is treated as opaque input and must include an
`INVALID_BYTE` diagnostic. No generated input is written to disk.

After compiling the module, run the default 760-case set from the standalone
repository root:

```powershell
mvn -o -ntp -pl kaishek-syntax -am -DskipTests test
java -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' `
  com.xenoamess.kaishek.syntax.ParserPropertyFuzzSelfTest
```

The run is reproducible. Pass `--seed`, `--cases`, and `--max-bytes` to replay
or scale it, for example:

```powershell
java -cp 'kaishek-syntax/target/classes;kaishek-syntax/target/test-classes' `
  com.xenoamess.kaishek.syntax.ParserPropertyFuzzSelfTest `
  --seed 0x4b41495348454b31 --cases 2000 --max-bytes 1024
```
