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
