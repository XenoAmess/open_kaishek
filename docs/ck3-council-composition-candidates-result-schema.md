# CK3 council-composition candidate result compatibility

Date: 2026-09-15 (Asia/Shanghai)

## Compatibility decision

The companion candidate adds the exact-build result capability
`game.query.council-composition-candidates-v1` with schema
`xar.ck3.council-composition-candidates/v1`. `open_kaishek` had no consumer or
registry entry for this result before the change. It now carries a passive
`CouncilCompositionCandidatesResultSchemaProfile` so future adapters can check
the frozen shape without duplicating it from memory.

This increment does not create a `CapabilityDescriptor`: the companion has not
frozen or advertised a runtime command, driver step or MCP tool for this result.
It also does not add a transport client, CK3 process access, a Paradox opcode,
an action, or a strategy policy. The compatibility state is therefore
`STATIC_RESULT_SCHEMA / UPSTREAM_CANDIDATE / TRANSPORT_UNADVERTISED`.

## Frozen v1 result

Every result carries `schema`, `schema_version`, `capability`, `exact_build`,
`status`, `unavailable_reason`, and `source_unavailable_reason`. An available
result additionally carries:

- one paused snapshot identity: `snapshot_id`, `public_revision`,
  `native_revision`, `date_raw`, and `paused`;
- `owner_character_id` and the fixed `councillor_steward` position;
- incumbent identity or `null`, `vacant`, and the corresponding `assign` or
  `replace` action route;
- `candidate_collection_complete` and candidate rows containing full signed
  character identity, native collection ordinal, final native eligibility,
  `stewardship` value, and the same position action route;
- eight readiness fields ending in `same_frame_ready` and aggregate `ready`.

Candidate rows are final rows accepted by the native provider. The v1 schema
does not reconstruct rejected rows and does not claim that
`native_collection_ordinal` is a score or stable ranking. All observations must
come from the same paused application-main transaction and exact snapshot,
revision, date, owner, and position binding.

The public unavailable vocabulary is pinned in the Java profile. A private
reader failure also preserves its source reason in the separate
`source_unavailable_reason` field. An unavailable result contains no snapshot,
owner, position, candidate, or readiness payload.

## Pairing and readiness boundary

The profile is paired to companion candidate commit
`e0d51a2dd8fc0637dd9a07ae599333ac252b949b`. The exact build remains CK3
`1.19.0.6`, executable
SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

The schema is static-only. It is not evidence that the private reader passed
the next real CK3 run, that incumbent and stewardship enrichment are live, that
a runtime/MCP endpoint exists, or that formal strategy consumed the result.
Reopen this compatibility package when the upstream candidate lands on master,
when runtime or MCP identity is frozen, when any field or reason vocabulary
changes, or when live/formal-strategy evidence advances.
