# CK3 player-faction alerts public capability

Date: 2026-09-14 (Asia/Shanghai)

## Public boundary

G2 REALM2 adds the read-only capability and step
`game.command.query-player-faction-alerts-v1` /
`query-player-faction-alerts-v1`, exposed through MCP tool
`ck3_query_player_faction_alerts_v1(expected_revision)`. The native command
envelope binds a positive `query_sequence` and the paused frame's
`snapshot_revision`; its payload key is `player_faction_alerts`. The MCP result
mirrors `player_faction_alerts_ready` from
`player_faction_alerts.readiness.alert_ready`.

`PlayerFactionAlertsCapabilityProfile` freezes the exact-build CK3 `1.19.0.6`
request, command envelope, MCP response, nested rows, readiness fields and
reason vocabulary. The profile is paired to landed upstream main commit
`1957b6d0ce133f76d56552b76a3ec96f7c740135`.

## Payload and strict partial behavior

The payload contains `schema_version`, `status`, `snapshot_revision`,
`date_raw`, `player_character_id`, `targeting_faction_count`,
`targeting_factions`, `county_exposures`, `planner_projection`, `readiness`,
`component_unavailable_reasons`, `unavailable_reason`, and `provenance`.

Targeting-faction rows freeze identity, type, target, leader, special
character/title, war, fixed-point power/discontent, member identities and the
stock dangerous classification. County exposure rows freeze county, faction,
type, target, fixed-point power threshold and the same dangerous
classification. Identity lists and row identities are sorted and unique; all
fixed-point values use scale `100000`.

The production candidate reuses the existing live campaign-root targeting
count. It does not claim detailed native faction rows or county exposure:

- `targeting_rows_ready=false`, an empty targeting list and
  `targeting_rows_native_reader_not_frozen`;
- `county_exposure_ready=false`, an empty county list and
  `county_exposure_native_reader_not_frozen`;
- `stock_dangerous_predicate_ready=false`, `alert_ready=false`, and an
  unavailable planner projection;
- `exact_ultimatum_timing_ready=false` in both readiness and planner output.

An available partial frame still requires the same paused frame, played-player
identity and targeting count. The mirror remains false until all detailed alert
components are ready. This keeps the count observable without presenting an
empty detail list as proof that no faction danger exists.

Top-level unavailable reasons are `unsupported_build`,
`requires_application_main`, `requires_paused`, `state_changed`, and
`reader_not_implemented`. An unavailable frame exposes no partial identity,
count, row, county or planner claim.

## Companion impact and readiness

A pre-change repository search found no player-faction alert consumer. Because
this is a new public query, open_kaishek adds a passive typed capability profile
and focused contract test. It does not add a transport client, start CK3,
register a Paradox opcode or authorize faction/war mutations.

The profile is `static-ready` for the frozen public contract and explicitly
`nativeCertified=false / runtimeCertified=false`. Reusing the targeting count
does not certify the full query. Native and runtime readiness can advance only
after the detailed reader boundary changes and matching paused live evidence is
published.

The compatibility package starts from open_kaishek baseline
`7652f8f49a8e73429d41581441067e37d3799747` on branch
`wp-ok-g2-realm2-20260914`. It contains no account, machine path, process ID or
CK3 round dependency.

Reopen this document when rows/county become native-ready, when exact ultimatum
timing becomes observable, or when open_kaishek gains a runtime caller for this
MCP tool.
