# ZhongGuo B2 lazy-trigger AST compatibility audit

Date: 2026-09-04 (Asia/Shanghai)

Companion commit `ee4fab944737685b21d7cc1f18ff572cf0238d90`
(base `1f0518fc452cfc40522ed72d7d9666bc95ce4853`) was audited against
open_kaishek `6b9e9c239430c5f364465f5a027d90de14464129`. The compatibility
verdict is `NO_CODE_CHANGE / PARSER_GREEN`.

The companion generator now emits nested `trigger_if = { ... }` and
`trigger_else = { ... }` blocks to guard optional first-use variable reads.
Twenty product scripted-effect files changed. Their outer effect definitions
remain purpose-split: each changed file contains 3--9 top-level effects, so no
file exceeds the project's preferred 1--10 range or its hard 20-effect
exception threshold.

## Existing AST support

No parser vocabulary was added. The lossless parser already represents any
balanced `key = { ... }` spelling as an entry whose value is a nested block;
`trigger_if` is additionally classified with the existing conditional-key
atom role. `trigger_else` uses the same lossless entry/block AST without
requiring an opcode or special parser production. The audited parser source
has SHA-256
`0b5f2bc385a3c1009994254c76dc958f8f3e692d16cdffaee45e994c15287084`.

This is also established vocabulary in the immediately preceding companion
corpus. Assignment-line counts changed as follows:

| key | base | current | delta |
| --- | ---: | ---: | ---: |
| `trigger_if` | 4,326 | 4,391 | +65 |
| `trigger_else` | 4,350 | 4,377 | +27 |

The generated structures therefore exercise an existing AST shape rather
than introducing new syntax. Extending the parser, validator, strict IR, or
runtime would add no capability needed by this change.

## Static verification

The full Maven reactor passed 147 tests with zero failures, errors, or skips.
The dependency-free CLI smoke test also passed. A fresh
`corpus --require-corpus` scan of the current companion ZhongGuo tree produced:

| files | parsed | errors | bytes | corpus SHA-256 |
| ---: | ---: | ---: | ---: | --- |
| 533 | 533 | 0 | 23,955,762 | `8c4d079c0f564442fd80e4abf3565fa998ed2da5c2106f88beeda7591aabe753` |

This audit certifies parse/round-trip compatibility only. It does not certify
the CK3 lazy-evaluation semantics of `trigger_if`/`trigger_else`, add an IR or
runtime handler, change any public capability/API, or promote gameplay/live
readiness. CK3 was not started or attached, and no save or game state was
mutated.
