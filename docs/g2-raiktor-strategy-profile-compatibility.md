# G2 Raiktor strategy-profile compatibility boundary

Status date: 2026-09-12 (Asia/Shanghai).

The companion root commit `502020bfbb1309a5bbfe6359365c4df91e3caf7a`
changes the Python-side GEN-034 strategy configuration contract. The
compatibility decision is `NO_PUBLIC_MCP_OR_NATIVE_DELTA / CONSUMER_BEHAVIOR_DELTA`.

## Current behavior

- `provide_raiktor_owner_budget_profile(None)` now loads the repository profile
  `raiktor-exit-balanced-v1 / 1.0.0` instead of returning unavailable.
- The exact profile file SHA-256 is
  `4206D725EC702701725221EB274E1F248E607F3127B720D033779FD71154FD11`.
- The provider result adds `source_kind`, `source_profile_version`, and
  `default_source_used`.
- An explicit version-2 source is accepted only as `operator_override` and must
  bind the repository default's profile ID and version. Existing version-1
  approved owner artifacts remain accepted for compatibility.
- The repository profile is a planner input. It does not supply campaign
  dominance, white-peace observations or utility, a recommendation, an action
  authorization, or an action result.

The Python provider schema identifier remains
`xar.ck3.raiktor_owner_budget_profile_provider.v1`. Its availability behavior
and additive result fields changed, so downstream readers must tolerate the
new fields and must no longer interpret a missing source path as an unavailable
budget.

## open_kaishek impact

No Operator MCP tool, JSON-RPC method, Java API, native bridge command, DLL,
game file, launch option, dependency, or CK3 round changed. `open_kaishek` does
not duplicate the strategy profile or implement the root planner provider; the
root repository remains its single writer. This document is the compatibility
record for consumers that exchange the Python provider result outside the
public MCP/native surface.

No CK3 process was launched for this synchronization.
