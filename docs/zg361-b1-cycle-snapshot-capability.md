# Zhongguo B1 cycle snapshot compatibility boundary

Date: 2026-09-10 (Asia/Shanghai)

Companion commit `8f0aa54699b8f79e3695c56dd9f17e7c1203b633` is the
current provider integration for the read-only MCP tool
`ck3_query_zhongguo_b1_cycle_snapshot_v1`, backed by
`game.command.query-zhongguo-b1-cycle-snapshot-v1`. The request accepts only a
nonce and expected snapshot revision. The native provider binds the manager to
the paused played character and reads a fixed allowlist of 38 B1 variables
twice within one frame.

This update makes the parent-owned Python normalizer safely idempotent across
the native-driver/service composition boundary. It does not change the MCP
identity or input, the native wire, the 68 required response leaves, the public
JSON schema, or the certification state.

`ZhongguoB1CycleSnapshotCapabilityProfile` records the public identity,
minimum cycle/roster/processing/quota/closure/pending projection, and the
invariants needed to consume the response safely. The quota projection keeps
targets and recounts separate and exposes `rebuild_generation`, so the real
repeated-rebuild RED can be diagnosed without a generic variable reader.

## Hash-bound companion inputs

| companion input | SHA-256 / identity |
| --- | --- |
| provider integration commit | `8f0aa54699b8f79e3695c56dd9f17e7c1203b633` |
| ABI ledger | `15c9ea3943ec521b84c5ce693e90fea7a81a526deeeca9061eb66ff4d93145b0` |
| source contract | `4093ab9db2a64c408a479f5dd96cee210253cc29f8aa0755e77e868c4447e1a1` |
| public JSON schema | `42e5692943123ac71c8629c2a8a6c0656f6aab20ea2a8546a193707ba932c23b` |
| Python query/normalization contract | `247729b653d2ecca49a894024c9ab6e6450fb74fe17bd559174eca166291f06d` |
| exact game build | CK3 `1.19.0.6` / EXE `2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86` |

## Readiness boundary

This is an additive compatibility descriptor, not a second provider. It does
not copy the native implementation or schema, add a Paradox opcode, expose a
write action, start CK3, or certify the static contract as live. The descriptor
therefore remains `nativeCertified=false`, `runtimeCertified=false`, and
`certified=false` until the companion produces paused live evidence.
