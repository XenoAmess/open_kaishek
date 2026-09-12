# Project delivery-state compatibility boundary

Date: 2026-09-12 (Asia/Shanghai)

Root commit `97d41ce112f4d45f16b98e6376bad899254d4320` adds the
machine-readable `xar.project-delivery-state` v1 projection, the
`xar.phase2-promo-stage-ledger` v1 ledger, `xar.compact-red-index` v1, and
`xar.work-package-resource-declaration` v1. These schemas report delivery and
operator workflow state. They do not change CK3 script syntax, native ABI,
MCP tools, Java runtime payloads, or ZhongGuo product data.

The compatibility decision is `NO_PUBLIC_DELTA`. `open_kaishek` does not copy
or regenerate the root repository's canonical state, P2 ledger, or RED
indexes. Consumers that need delivery state read the root projection by its
published schema/version and continue to obtain live CK3, RED, and Git state
from the projection's named sources. This keeps the companion independent of
an operator account, machine path, CK3 round, or runtime artifact location.

The v1 boundary relevant to companion consumers is:

- stable delivery state and live state are distinct;
- cached state never claims a current PID, round, pipe, or active RED;
- P2 source `4`, raw footage `8`, and each cut's six stages have separate
  fixed denominators;
- RED uses independent business, harness, and lifecycle results while total
  severity remains RED;
- `open_kaishek` remains a compatibility consumer and is not a second
  canonical event or delivery-state registry.

No Maven or Python suite is repeated for this documentation-only decision.
The root generator's own normal and optimized focused tests, artifact
verification, and resource-declaration validation are the producing evidence.
