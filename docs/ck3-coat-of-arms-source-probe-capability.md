# CK3 coat-of-arms source probe compatibility boundary

Date: 2026-09-10 (Asia/Shanghai)

Companion commit `16755189c172772669a0d30456da1f977d6b6de0` adds
the public MCP tool `ck3_probe_coat_of_arms_source_v1` backed by native
capability `game.command.probe-coat-of-arms-source-v1`. The request is exactly
`source`, `expected_revision`, and `apply`; source must be non-empty ASCII and
is bounded to 128 KiB. The response schema is
`coat-of-arms-source-probe-v1` version 1.

Follow-up commit `718a60d538249fb6eecd47284e622fd33d71d260` moves
native hook installation into bridge worker bootstrap, before the gameplay
snapshot-dependent mailbox lifetime. This makes the already-declared
`frontend` binding reachable before a gameplay snapshot exists. Capability
advertisement, MCP request/response schema, statuses and side effects are
unchanged.

open_kaishek records the public envelope as a static compatibility profile. It
does not copy the native hook, start or attach to CK3, manipulate the
clipboard, or add a CLI/service action. It also does not add parser vocabulary:
CK3 sends this input to the dedicated coat-of-arms render-description reader,
not to the effect, trigger, event, GUI-expression, or console evaluators.

## State and binding boundary

The tool supports three exact session bindings:

- `frontend`: `expected_revision=0`, bound to one bridge PID and connection
  generation when no semantic snapshot exists;
- `frontend_snapshot`: a positive public/native revision and date, but no
  played character or episode;
- `gameplay`: a positive public/native revision and date plus episode identity.

Hybrid mode must route the request to the native backend and may not use a
visual fallback. A result must preserve the same source SHA-256, byte count,
bridge PID, connection generation, and applicable snapshot identity throughout
the call.

This tool is deliberately not marked read-only. Detect-only calls overwrite
the OS clipboard and update the in-game paste preview; `apply=true` may also
change the coat-of-arms designer's working state. `applied` does not prove the
upper designer window was finished or that the result was persisted into a
character, dynasty, house, title, or save. The explicit source parameter also
keeps the probe outside the autonomous player's parameterless action set.

## Readiness

The descriptor remains neither native- nor runtime-certified. The companion
has exact-build handshake and native build/test evidence, but its committed
report still leaves the new-DLL per-payload live matrix pending. open_kaishek
therefore records the contract without promoting `mcp-detected`, `mcp-applied`,
product-live, or persistence readiness.

## Frozen provider evidence

| companion input | SHA-256 |
| --- | --- |
| MCP server | `7DD3207AB2C53385FBAEBB4C28B9A30614ED229C05A20B58694EF7B4A355ABC1` |
| Python contract | `031BF5DF84094953910C2AF147B16ED40949C649DFDCF76295F876E7F0F99E5A` |
| native ABI | `EFABF2A04C0A8AD4A61A63B7FA0E6301F2B63AAB96C7D362EBCDA0F61F8FA66B` |
| native implementation | `8E4209801529AB6C9505BFEB5CE0E587DA69D89C4289A4621FFEE122A66290A8` |
| bridge bootstrap | `42296A162E8D1505B3FE8CFA6343C364117C7F3DC9CE207911884F1F124A51C6` |
| service | `336BD52A1E32F62003AB53585401D961A060FBF8F37F40BC54ACD3D68DDBF564` |
| native driver | `82F6D94E22525400801E0885A4D0D77B6D1C4011F30462D124C062D02A9E3092` |
| focused provider test | `09A689D1CAC62DDE550AF8DEECDA1E28C46897220D1D69FF3B2F3CC8A9CFDC70` |

No CK3 process or promotional-video asset is touched by this synchronization.
