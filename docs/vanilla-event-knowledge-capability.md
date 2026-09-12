# CK3 vanilla-event knowledge MCP compatibility boundary

Date: 2026-09-10 (Asia/Shanghai)

The companion CK3 project now exposes the offline, read-only MCP tool
`ck3_query_vanilla_event_knowledge_v1`. This document and
`VanillaEventKnowledgeCapabilityProfile` record its public compatibility
surface without copying the companion's canonical event records into
open_kaishek.

## Public v1 contract

The request contains exactly:

* required `event_definition_key`;
* optional `ck3_build`, defaulting to `1.19.0.6`.

The response schema is `xar.ck3.vanilla-event-knowledge`, version `1`, with
the following complete top-level field set:

* `schema`, `schema_version`, and `status`;
* `event_definition_key`, `ck3_build`, and `ck3_exe_sha256`;
* separate `contract`, `analysis`, and `observations` projections;
* `unavailable_reason`.

An unsupported build returns `unsupported_ck3_build`; an invalid or
unregistered key returns `invalid_event_definition_key` or
`event_definition_key_not_registered`. Each case retains the same v1 envelope
and returns null knowledge projections. Unknown MCP arguments are rejected.

The exact game binding is CK3 `1.19.0.6`, executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

## Hash-bound companion inputs

The exact reviewed API bytes are bound directly. The MCP server hash advances
with companion commit `df1ed7cb00ef6c54de602fbf182e6204c77c1202`, which adds
four sibling portable read-only tools without changing this query's v1
request/response envelope. These hashes include the companion repository's
required UTF-8 BOM:

| companion input | SHA-256 |
| --- | --- |
| `ck3_autonomous_player/src/xar_autoplayer/vanilla_events/registry.py` | `9d81716542888ca9ef64bc2e5fbe11a44fe16d8b3f244dd1da415ae189ce7e89` |
| `ck3_autonomous_player/src/xar_autoplayer/bridge/mcp_server.py` | `A67D98C4D3B452D09AF0B9A830E5E95BB58C31AF52B31AAA586423B18E4ED320` |

The companion's focused MCP test lists and calls the tool through a driver
whose every gameplay method raises. Both cases pass, proving that this query
does not require or contact a running CK3 process.

## open_kaishek scope

`VanillaEventKnowledgeCapabilityProfile.QUERY` is read-only and deterministic,
with both `nativeCertified=false` and `runtimeCertified=false`. Those flags are
deliberate: the capability is an offline knowledge lookup, not a native CK3
observation or finite-runtime implementation.

This synchronization adds no parser vocabulary, schema opcode, IR lowering,
runtime handler, CLI command, Quarkus service, CK3 process access, mutation
authority, or event-selection policy. A later consumer may query the companion
MCP and validate schema version/build identity, but open_kaishek does not become
a second owner of the companion registry.

At root audit revision `738650b0113b6a3a61933d0a23501ce503ebe755`, the
default catalog contains 184 contracts, 184 analysis records, and 184
observation projections. The R463 addition `tgp_japan_yearly_events.1030`
exposes the exact-build call chain, empty saved-scope frame, four-option
rendered projection, five-option source snapshot, authored1/native0 health
route, and immutable pre-selection RED through the same read-only v1 envelope.
The event's faith theme does not add a faith, doctrine, tenet, fervor, or
conversion capability.

The root Operator later found R463 absent before the planned same-process
retry. Root commit `66b63f42a26a7ab16e6b4fa3e7394c7438e6079e` preserves the
unexplained process exit and GREEN cleanup without changing the 184-record
catalog or this v1 capability. Live closure of the new record remains pending
in a fresh bounded gameplay round.

At root revision `53e229e1d7c5227e1b7d8f1f06ab3d7f456271f8`, R465 proved the
registered authored1/native0 selection through an event-instance-advanced
postcondition. The `.1030` contract is now a production-live primitive while
its later Stage 10 source-window exhaustion remains a separate RED and emits
no P1 source receipt. Catalog counts and the v1 query envelope remain
unchanged.

The earlier 29-key observation inventory at revision
`2fa9034a59ebec3677b184379611fd45471ba0b6` was:

* `death_management.1007`, `ep3_emperor_yearly.2170`,
  `ep3_emperor_yearly.2211`, `ep3_landless_admin.1000`,
  `ep3_powerful_families.8012`, and `ep3_story_cycle_admin_eunuch.1001`;
* `epidemic_events.0110`, `epidemic_events.1064`, `epidemic_events.5009`,
  `faction_demand.1101`, `faction_demand.2001`, and
  `great_holy_war.0011`;
* `historical_char_creation_events.1`, `intrigue_temptation.3020`,
  `natural_disaster.6901`, `natural_disaster.7021`,
  `natural_disaster.7031`, `natural_disaster.8001`, and
  `stress_threshold.1721`;
* `tgp_china_ministry.0100`, `tgp_dynastic_cycle.0081`,
  `tgp_dynastic_cycle.0091`, `tgp_dynastic_cycle_events.0001`,
  `tgp_dynastic_cycle_events.0020`, and `tgp_movement_events.0160`;
* `trait_specific.4001`, `travel_danger_events.3002`,
  `tribute_mission.1005`, and `vassal_interaction.0040`.

The current portability tranche covers eleven existing records:
`great_holy_war.0011`, `tgp_dynastic_cycle.0091`,
`ep3_emperor_yearly.2170`, `ep3_emperor_yearly.2211`,
`ep3_powerful_families.8012`, `historical_char_creation_events.1`,
`intrigue_temptation.3020`, `natural_disaster.8001`,
`natural_disaster.7021`, `natural_disaster.6901`, and
`travel_danger_events.3002`. Their reusable contracts bind the active player
through `$player` and no longer carry a campaign date or numeric player
identity. The removed live bindings remain queryable only in each record's
`observations` projection, so another operator or machine can reuse the same
contract without inheriting the original campaign identity.

This tranche did not change `registry.py`, `mcp_server.py`, the MCP tool ID, or
the v1 request/response envelope. Consequently the byte hashes and Java
compatibility descriptor above remain current; only this content-audit revision
and inventory advance. Catalog and metadata counts are content inventory, not
part of the v1 ABI, so compatible records and metadata may continue to be added
without changing the request/response shape.

At root revision `6e780e08d136df8d8fb0141c8f0d41bab644fcf2`, the catalog
advances additively to 185 contracts, 185 analysis records, and 185 observation
projections. The new `stress_threshold.1011` record binds the active player
through `$player`, requires the exact R498 two-character-scope and native
`0/1/5` projection, and selects authored option 6 / native option 5. Its
analysis freezes the exact-build definition and level-one stress-pool caller;
its observation preserves the R498 pre-selection RED. The query v1 envelope,
request fields, registry implementation, MCP server implementation, and their
previously pinned byte hashes do not change.

At root revision `0494375d74a4928573cf47672b6150a0ab894ed2`, the catalog
advances additively to 186 contracts, analysis records, and observation
projections. `trait_specific_interactions.0011` binds player as
root/recipient/subject, requires a distinct actor plus the exact weak-slot,
boolean-theme, and native `0/1/2` projection, and selects authored2/native1.
Its exact-build definition, interaction caller, option effects, and R500 RED
are available through the unchanged v1 read-only interfaces.

At root revision `fdf23f537d4238850154f85184f503ba63136a3d`, the catalog advances additively to 187 contracts, analysis records, and observation projections. `travel_completion_event.1000` binds root and travel owner to the player, requires the exact two travel-plan/three province/distinct travel-leader scope shape, and selects the sole rendered native option `0`. Its exact-build definition, travel on-action callers, and R502 RED are available through the unchanged v1 read-only interfaces.

At root revision `f4586847ec7d5a4201bde398a38137262e438d8c`, the existing
`analysis` projection gains two additive `selected_choice_effect_profile`
records. `tgp_travel_events.0030` identifies authored base stress loss `-30`
while refusing an exact runtime delta because character stress-impact modifiers
are not observed. `natural_disaster.7031` distinguishes the selected option's
non-material warning tooltip from its common-after variable write. Both use
`xar.ck3.vanilla-event-choice-effect`, version `1`, and remain offline
source-structured metadata rather than native/runtime-certified effect preview.
The query request, top-level response fields, tool ID, registry/MCP server
implementation hashes and Java compatibility profile remain unchanged.

At root revision `0d7ddc5761b0997101806b179a52585332720beb`, the existing
`trait_specific.8001` analysis gains a third additive
`selected_choice_effect_profile`. Native option 1 records source-authored
`add_gold = minor_gold_value`, marks its runtime amount non-exact because the
value depends on monthly income, treasury and era, and publishes a strictly
increasing played-character gold postcondition. This changes record content
only: catalog counts, query request, top-level response fields, tool ID,
registry/MCP server implementation hashes and Java compatibility profile stay
unchanged.
