# G2 postwar cleanup/expiry adapter boundary

Date: 2026-09-04 (Asia/Shanghai)

Companion integration `ff89dcdbefb9d8fc86ce4722df847946e96d0e81`
(source `7aae7e064b6e224dd3a5b95070b54d9205c32cf4`) adds private,
default-OFF query `game.command.query-raiktor-war-bound-loss-cleanup-v1-N`
and makes the Python adapter issue that query itself. External cleanup payload
injection is no longer accepted. `G2PostwarCleanupExpiryAdapterMetadata`
records this exact static contract; it does not add a `CapabilityDescriptor`,
parser vocabulary, open_kaishek runtime handler, opcode, or CLI command.

The status is `STATIC_READY_PRIVATE_DISPATCH_LIVE_NOT_RUN`. Both cleanup and
actual-expiry queries have private candidate dispatches, but neither this
cleanup lifecycle nor the joined receipt has run against CK3. Old-WarID
absence is only admission to the cleanup read. The `destroyed` result comes
from double-sampling the retained full-generation persistent regiment,
current regiment, and army stores; the adapter may not infer it.

| companion input | SHA-256 |
| --- | --- |
| adapter runner | `40696417f3bbd841e79116612697654c48868c90534bdfc0fdf43e161fdb47c8` |
| adapter manifest | `7874094361e8de6b38f77441b1ff59f512afcd13c309e0ffd02147185e86375f` |
| synthetic fixture | `5ce4b1d261b3c96123a7065947ce55f832e46d33cd69624a150c17d1f4a3e8ce` |
| cleanup contract | `11c7b77842676251bdb1516472cb15d4115105d4a7b3ab5b994b3430f5f48c61` |
| cleanup dispatch source contract | `1863e7b53d852b83f8fc3432e66c90eee72e73ecfbcbdacc1f48e47c232ab4d9` |
| candidate DLL | `5d366fa321da436601819e52827210defe42d1fe14950380d3d2722d6b992ff5` |
| candidate native test | `f7999cca5ae9ac70e64515afce391049d9e318a9f32b701482fcf1e4996ffe88` |

The B7 promotion capture freeze integrated before `a01f8cb` does not change
the two transport capability IDs, the source/ABI contract pins, or their
fail-closed descriptors. Its provider remains default-off, both production
capabilities remain unadvertised, and readiness remains live-pending, so the
existing `ZhongguoPromotionSourceTransportCapabilityProfile` requires no
code change.

The private dispatch is statically compiled, not production-live. Public and
action readiness, live authorization, source-specific attribution, decision
readiness, automatic surrender, and GEN-034 all remain false.

## 2026-09-05 R3 evidence synchronization

The preceding section is the original static/synthetic baseline. Its source
pins and synthetic vector are retained, not relabeled as live evidence.
`G2PostwarCleanupExpiryAdapterMetadata` now separately records the companion
R3 run at source `e72f9fa302811a823479635648eb008a6f5d8418`:

| immutable R3 input | SHA-256 |
| --- | --- |
| frozen candidate manifest | `2113032784cc3acc5da14557c14315b0aec9af03cdc15654739a3c54704f96da` |
| live report | `44e1f7c0b470b2cf7b6549192865402f21f88c7cf073e896de1b93632311d5d0` |
| live candidate DLL | `4d839524098891bd997009663e189929722746ab0404d88c1e91f7546efe238b` |

Artifacts reside beneath
`Z:\ck3_mod_rewrite_process_assets\zg361\g2-cleanup-formal-e72f9fa-r3-20260904`;
the report is `live-r3/report.json`. The manifest and report hashes were read
from those retained bytes during this synchronization. The companion topic
`docs/ck3-native-ai/g2-postwar-cleanup-expiry-current-pin-no-launch-2026-09-04.md`
records the native result and exact build.

R3 returned `GREEN_ACTION_BOUND_POSTWAR_RETENTION_EXPIRY`. One private
surrender was followed in the same lifecycle by exact-store destruction of
the frozen generations (`598 -> 0` soldiers), then two equal persisted-expiry
reads (`date_raw=53267736`, not formula-derived). Consequently the metadata's
private cleanup dispatch is now live-tested and its companion runtime-cleanup
primitive is ready. Overall status is
`PRIVATE_DISPATCH_LIVE_PRIMITIVE_COMPARISON_PENDING`.

This does not implement or certify an open_kaishek runtime evaluator, make the
synthetic fixture live, add a public capability, authorize a new CK3 attempt,
or complete the comparison policy. Public/action readiness, source-specific
attribution, decision readiness, automatic surrender, and GEN-034 remain false.
The frozen R3 result keeps its original source/accelerator pins; today's
metadata synchronization does not rewrite or rerun that historical attempt.
