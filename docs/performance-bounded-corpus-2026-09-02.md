# Bounded corpus performance slice (2026-09-02)

This note records one small, evidence-driven optimization for the offline
parser/validator preflight.  It does not change the schema, diagnostic codes,
or CK3 runtime claims.

## Evidence and boundary

The measurement corpus is the external `mod_zhongguo_style` tree used by the
CK3 phase-two gate.  The frozen scan contained 75 `.txt`/`.gui` files,
19,238,232 bytes, parser diagnostics `0`, validator diagnostics `174,870`,
and root scan SHA-256
`6c5aefc12a2c542e499d68ee44dfd1e233e761957b316096f1fafeacb0b1837f`.
The validator result is intentionally still RED at the current schema-only
boundary; the optimization must not hide or cap those diagnostics.

Before this slice, a JFR profile of the same run showed
`java.util.regex.Pattern.*` compilation/matching as the hottest sampled path.
The parser used `String.matches(...)` in per-atom role classification and
bracket-expression classification, which recompiles a regular expression for
each call.

## Change

`Parser` now keeps the six recurring role/classification expressions as static
`Pattern` instances and calls `matcher(...).matches()`.  The expressions and
full-match semantics are unchanged; only pattern compilation is moved out of
the per-token path.  No diagnostic budget, source traversal, or profile rule
was relaxed.

## Reproducible result

On the Windows development host, with the same shaded CLI and corpus:

| Run | Before (`1c320ad`) | After this slice |
| --- | ---: | ---: |
| Warm parser-only pass in one JVM | ~1,066 ms | ~421 ms |
| Warm parse + validator pass in one JVM | ~1,240–1,300 ms | ~574–610 ms |
| New-process `preflight --fixture none` | ~2.65–3.10 s | ~1.43–1.49 s |

All runs reported the same 75 files, 19,238,232 bytes, parser diagnostics
`0`, and validator diagnostics `174,870`.  The new-process command remains
non-zero because the schema-only validator boundary is still RED; this is
expected and is not converted into a false GREEN by the optimization.

The JFR recordings used for diagnosis are local temporary artifacts and are
not part of the repository release surface.

## Verification

The offline Maven reactor test suite, parser self-tests, Phase 1 syntax smoke,
and seeded parser property/fuzz smoke all pass after the change.  No CK3
process, bridge, MCP session, save, or network service was started for this
slice.
