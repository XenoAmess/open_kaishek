# Zhongguo B7 promotion source transport boundary

Date: 2026-09-04 (Asia/Shanghai)

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

The query reads a fixed five-widget allowlist twice on one paused
application-main frame. Missing or inconsistent data is typed unavailable.
The action is fixed to the product's review-now semantics and is bound to the
prior progress query. An accepted action ACK remains verification-pending; a
separately nonced later progress query must prove B1 entry.

| companion input | SHA-256 |
| --- | --- |
| source contract | `a167bfe43cb1b0254e124abebef954a5fb8b2164afee31b16be8badc5e8fa786` |
| ABI ledger | `10fb508a960ee8819166d34dd2b303bfede43e3fff30ae64230913fdfe34fac6` |
| Python contract | `5cfa9fdea255b180612cace27687e9b3c89fa884f2a9fa92ac2c268c19876aea` |

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
