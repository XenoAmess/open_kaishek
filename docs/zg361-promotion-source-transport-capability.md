# Zhongguo B7 promotion source transport boundary

Date: 2026-09-04; synchronized 2026-09-09 (Asia/Shanghai)

Companion commit `d53befaa4872662562f5db5d31757ca731e799e0`
adds two advertised fail-closed transport capabilities:

* `game.contract.zhongguo-promotion-source-progress-v1-fail-closed`;
* `game.contract.zhongguo-review-now-action-v1-fail-closed`.

The product identities remain
`game.command.query-zhongguo-promotion-source-progress-v1` and
`game.command.activate-zhongguo-review-now-v1`, but both production capability
flags are false. `ZhongguoPromotionSourceTransportCapabilityProfile` therefore
records only the two transports as descriptors. It does not promote either
product capability, implement a native query/action, or add parser vocabulary,
IR/runtime handlers, opcodes, or CLI commands.

The query reads a fixed twenty-nine-widget allowlist twice on one paused
application-main frame. Missing or inconsistent data is typed unavailable.
The action is fixed to the product's review-now semantics and is bound to the
prior progress query. An accepted action ACK remains verification-pending; a
separately nonced later progress query must prove B1 entry.

| companion input | SHA-256 |
| --- | --- |
| source contract | `4ebeb1463d421d2278d69b68ab9a070de776a3d38a7e03e54719b0401d6817b5` |
| ABI ledger | `f489cae78e8a15da7b284b93eb33a6ad3fe6fd5735899f797a4a0e690e75f400` |
| Python contract | `9f8acf825ed2df8410484f48a5da979f0fb290056cf5080936e1c52409d21094` |

Companion commit `f730aeb677066e39aa7f19e53c66e2a84b842f88`
changes only the B3 localization/freezer projection. It adds no public
capability, schema, ABI, parser vocabulary, IR/runtime handler, or action, so
no open_kaishek contract change is required for that commit.

## 2026-09-05 companion ABI synchronization

The source pin now advances to companion
`d077bcf0114f227d319d8f23f64385ba6950238b`, ABI SHA-256
`eb22c5339a483614e75cd5135b896742ac9e0040166ac9689fb8af3070c94068`.
The source-contract and Python-contract hashes above remain unchanged. The
new ledger distinguishes direct-child lookup from the fixed-name descendant
fallback exercised by the private promotion candidate. It records control-flow
evidence, not a completed promotion source/action loop. Transport IDs, fields,
invariants and certification flags are unchanged; no runtime handler is added.

The companion's separate current-event named Character identity can now be
typed unavailable when its saved token no longer resolves. That event-window
wire is not consumed by this profile, so its decoder synchronization is
`not-applicable` here. The root Character remains strictly resolved in the
companion; open_kaishek does not substitute a Character identity or infer a
valid event action.

## 2026-09-05 old-save manager witnessing synchronization

The source pin advances again to companion
`a05b94e545fc6074fa2ffae2ffa76e34d9990d62`. Its B1 active scripted GUI keeps
the existing fixed widget and wire field, but recognizes a pre-split manager
loaded directly from an old save through a manager-only review witness when the
new manager cycle serial is absent. This is a producer-side truth-condition
change for `widgets.effective_visible`, so the exact source provenance is
updated even though the public ABI is unchanged.

The profile ID, fixed five-widget allowlist, source-contract hash, ABI hash,
Python-contract hash, required fields, invariants and readiness/certification
bits remain unchanged. The new GUI branch uses existing Paradox logical blocks,
`has_variable`, and scalar `var:*` comparison shapes. It adds no parser
vocabulary, opcode, IR lowering, runtime handler or native decoder. Parser
round-trip evidence can establish syntax preservation only; the companion's
next CK3 old-save run remains authoritative for the gameplay semantics.

## 2026-09-09 Central diagnostic projection synchronization

The source pin advances to companion
`4974324fece6ba152bada6069f8a12718e4da8f6`. The fixed widget set expands
from five to twenty-nine entries: the original five product/source widgets,
eleven Central stage indicators, six Central status indicators, and seven
stage-nine liveness predicates. The source-contract, ABI, and Python-contract
hashes in the table above bind that exact set and order.

This is a read-only diagnostic projection on the existing v1 query transport.
The query and action capability IDs, request fields, per-widget response shape,
same-frame rule, typed-unavailability behavior, action semantics, and all
production certification flags remain unchanged. It adds no parser vocabulary,
IR lowering, runtime handler, opcode, CLI command, or new action authority.

## 2026-09-12 exact-build generic value-scope synchronization

The source pin advances to companion
`2c059aa4becf46ea26415cc0973b1d0fd9e2b9cb`. A real paused
`zg361we.356` frame on CK3 `1.19.0.6` established that the saved scalar scopes
`zg361_we_al_cycle` and `zg361_we_al_case` use `raw_type_index = 1` while
retaining `type_key = value`, `subtype = 0`, and an intentionally unavailable
generic payload identity. The former capture-side assumption that all such
value scopes used raw index `9` was therefore a Python consumer contract bug.

The repaired source/provider contract preserves the exact positive raw index
reported by the bound build and excludes the independently established
Character index `4`; semantic routing continues to use `type_key = value`.
This changes no bridge wire field, capability ID, schema version, parser
vocabulary, IR/runtime handler, opcode, CLI command, or action authority.
open_kaishek consumers must treat `raw_type_index` as exact-build diagnostic
metadata and must not infer a stable cross-type enum from this observation.

## 2026-09-12 pre-terminal Workforce source synchronization

The source pin advances to companion
`e168ad0c352e3b41eabdd387f27a96affa5a96ab`. Real R508 evidence at the
owner-facing `zg361we.356` source bound the active AL tuple to cycle `5`, case
`1`, state `1`, while the terminal-history group was honestly empty. The
terminal history variables are created only after the portfolio closes, so a
source-checkpoint consumer may now receive either an empty pre-terminal history
shape or the existing partial-two closed-history shape. In both cases the
current provider-bound cycle must be at least three and the owner, subject,
case, event scopes and same-frame transport binding remain strict.

This is a consumer data-contract correction. It changes no native wire field,
capability ID, bridge ABI, parser vocabulary, IR/runtime handler, opcode, CLI
command or action authority. open_kaishek must preserve the observed history
status and must not infer terminal success, `.361`, debt, charter adoption or
next-cycle effect from the source checkpoint.