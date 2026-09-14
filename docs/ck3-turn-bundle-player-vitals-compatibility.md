# CK3 turn-bundle player-vitals compatibility

## Additive contract

The existing public `ck3_query_turn_bundle_v1` response adds
`ruler_state.value.vitals` with schema `xar.ck3.player-vitals/v1`. It reuses the
turn bundle's paused-frame identity and adds no MCP tool, native RPC, endpoint,
or action authority. The existing `ruler_state.value.health_band` and
`ruler_state.value.stress_points` fields remain present during this migration.

The new object contains these top-level fields:

- `schema`, `status`, and `binding`;
- component envelopes `health`, `stress`, and `legitimacy`;
- `readiness` with `identity_ready`, `health_ready`, `stress_ready`,
  `legitimacy_balance_ready`, `legitimacy_classification_ready`, and
  `vitals_ready`;
- `planner_signals` with `succession_preparation_priority`,
  `avoid_discretionary_stress_gain`, and `protect_legitimacy_floor`.

`binding` carries the played full-generation `character_id`, `snapshot_id`,
native `snapshot_revision`, and `date_raw` from the same turn-bundle frame.
Health retains signed Q100000 raw/scale plus its frozen band and threshold
booleans. Stress retains nonnegative points plus the `>=100` threshold result.
Legitimacy remains a separate component and may be unavailable without erasing
the other two components.

## Partial-readiness rule

The initial projection has no general ruler-legitimacy observation. It
therefore publishes:

- `vitals.status=partial` when health or stress remains available;
- `legitimacy.status=unavailable` with
  `player_legitimacy_state_unavailable`;
- `legitimacy_balance_ready=false`,
  `legitimacy_classification_ready=false`, and `vitals_ready=false`;
- independent health and stress readiness according to their actual component
  state.

The partial aggregate remains useful. Health plus ready succession inputs may
still publish `succession_preparation_priority`; stress may still publish
`avoid_discretionary_stress_gain`. Only `protect_legitimacy_floor` is
unavailable when legitimacy is missing. A consumer must not require
`vitals_ready=true` before reading an independently available component or
planner signal.

A future engine-proven legitimacy component may be `available`, or
`not_applicable` with `no_applicable_legitimacy_type`. This can close
classification readiness without inventing a raw balance. It does not permit a
consumer to reconstruct legitimacy type or level from title, government, or
raw balance.

## Companion impact

At baseline `dcb33d90415f0e24229b431423e3983bcfb3b7f3`, a repository-wide
non-documentation search finds no `ck3_query_turn_bundle_v1`, turn-bundle
caller, `ruler_state` parser, closed Java response type, endpoint, or profile
in `open_kaishek`. There is therefore no runtime adapter to change. The
additive nested object is pass-through compatible with the independent
Operator MCP envelopes, dependencies, endpoints, and repository version.

The compatibility verdict is
`ROOT_PUBLIC_TURN_BUNDLE_NESTED_FIELD_ADDED /
OPEN_KAISHEK_NO_TURN_BUNDLE_CONSUMER / DOCUMENTATION_ONLY`. No fixture or
static assertion in this repository is presented as upstream runtime or live
certification.

## Pairing status and reopen conditions

The compatibility record is paired to landed upstream main commit
`a30e56832dcbf0b3f84f6fa726570cb22bc15634`. Its object fields, retained
legacy health/stress fields, and component-local readiness behavior match the
candidate reviewed above. The current pairing status is
`paired_to_upstream_main`.

Reopen this boundary if `open_kaishek` gains a typed turn-bundle consumer, the
legacy health/stress fields are removed, the v1 field set or readiness meaning
changes, or the upstream provider begins publishing general ruler legitimacy.
The contract contains no machine path, account, CK3 process, or round identity.
