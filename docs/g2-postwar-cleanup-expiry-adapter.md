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
