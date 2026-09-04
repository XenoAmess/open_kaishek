# G2 postwar cleanup/expiry adapter boundary

Date: 2026-09-04 (Asia/Shanghai)

Companion integration `a01f8cb684d39e2ea8e95fbf0f20f170b6f1a396`
(source `beb17743a6440650eec2ca9c0bf270733bce2527`) adds a
fixture-only Python adapter that joins the frozen retention ticket, one native
surrender ACK, a native cleanup observation, and two equal persisted-expiry
reads. `G2PostwarCleanupExpiryAdapterMetadata` records this exact static
contract; it does not add a `CapabilityDescriptor`, parser vocabulary,
runtime handler, opcode, or CLI command.

The no-launch preflight status is
`GREEN_STATIC_ADAPTER_LIVE_BLOCKED_ON_CLEANUP_DISPATCH`. The actual-expiry
query has a private candidate dispatch, and the cleanup reader exists in the
native library, but the cleanup reader has no runtime query dispatch. Old-war
absence alone cannot prove destroyed-generation cleanup, and the Python
adapter is forbidden from inferring it. The committed synthetic fixture is
therefore not CK3 evidence.

| companion input | SHA-256 |
| --- | --- |
| adapter runner | `7d8a0e3ce8560b7153ec5e4a89407aa019d0ea20671254d1286351880ad5d80d` |
| adapter manifest | `7ce021720d0749288142040da5233f7bbdddd5a2f3b8ac187df1f72770b5a051` |
| synthetic fixture | `5ce4b1d261b3c96123a7065947ce55f832e46d33cd69624a150c17d1f4a3e8ce` |
| no-launch preflight | `da4b52f29cbb3dec478061877080c674ae11f08c80a13dbe353b6ca170fa6adf` |
| synthetic receipt | `66e09371be876f5bf4a8e97bccb8be8b0e19398894fa7b88a0056fdf2e49e4d1` |

The B7 promotion capture freeze integrated before `a01f8cb` does not change
the two transport capability IDs, the source/ABI contract pins, or their
fail-closed descriptors. Its provider remains default-off, both production
capabilities remain unadvertised, and readiness remains live-pending, so the
existing `ZhongguoPromotionSourceTransportCapabilityProfile` requires no
code change.

Runtime cleanup, public/action readiness, live authorization,
source-specific attribution, decision readiness, automatic surrender, and
GEN-034 all remain false.
