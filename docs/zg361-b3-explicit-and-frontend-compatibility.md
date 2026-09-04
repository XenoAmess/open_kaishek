# ZhongGuo B3 explicit-AND and launch-tool compatibility audit

Date: 2026-09-04 (Asia/Shanghai)

Companion range `3ebbb6d0fb5bc8067387a239119d3ac6786844c7..971d1f9cdf0220e964e0064879ed301df4b3fb99`
was compared against open_kaishek `3c4e6982b5d821f1fcdb9c3ced2a581497c9a6eb`.
The compatibility verdict is `NO_CODE_CHANGE / PARSER_GREEN`.

Only one product `.txt/.gui` path changed:

`common/scripted_triggers/zg361_phase2_central_runtime_triggers.txt`

The exact-trigger body was wrapped in one explicit `AND = { ... }`; its
conditions, parameter names, nested trigger call, and external callers were
otherwise retained. The file grew from 16,712 to 16,786 bytes:

| revision | file SHA-256 |
| --- | --- |
| baseline | `ee8d962f5a9aa95ae68098b96ffe26e1b2da2435821617bd51e75fe0fec377d7` |
| current | `bb771a488fecc9fc131a20c562ab621d432414fa864e838c35d7e28520d7e411` |

Explicit `AND` was already present 473 times in the baseline corpus and is a
losslessly parsed structural block, not a new schema token. The current
corpus adds exactly one occurrence. No parser, validator profile, IR,
runtime, CLI, capability descriptor, or G2 metadata change is justified.

Fresh `corpus --require-corpus` results with the same built CLI JAR:

| corpus | files | bytes | errors | corpus SHA-256 |
| --- | ---: | ---: | ---: | --- |
| baseline ZhongGuo | 533 | 23,949,962 | 0 | `b495ff1e75bc622d2fc0b9af818de1c0ff01910ae35a795a4c3bde3262ab0cd7` |
| current ZhongGuo | 533 | 23,950,036 | 0 | `07b5f10093fd7c33415515fb7ddafe03b55dc9ba752620a0746b3a182f21db93` |
| current main mod | 54 | 2,217,794 | 0 | `bf36f9e2e8c1cbf1880f4231029bb66a81e66e97684ca877397056b30b633ab5` |

The open_kaishek code/JAR remains byte-identical at 348,438 bytes and SHA-256
`10dfab8aa729d3b696c518bd532fde7535dc1605150045828b650546864bd9cf`.
The existing reactor result for that exact code is 147 tests with zero
failures, errors, or skips.

The companion frontend-first changes are Python launch choreography and
evidence documentation only; they do not enter the external script corpus.
The recorded long native build-path `C1083` is likewise a filesystem/tooling
boundary, not a Paradox parser or product-file-size failure. This audit did
not start or attach to CK3 and does not promote frontend, gameplay, or live
readiness.
