# CK3 council-composition candidate MCP compatibility

Date: 2026-09-15 (Asia/Shanghai)

## Additive Council20 surface

Companion candidate commit
`a6857514dd909caeda68a07ec2baee053c53daac` adds the read-only MCP tool
`ck3_query_council_composition_candidates_v1`, backed by capability
`game.query.council-composition-candidates-v1` and fixed step
`query-council-composition-candidates-v1`. The result schema remains the
Council19 contract `xar.ck3.council-composition-candidates/v1` pinned by
`CouncilCompositionCandidatesResultSchemaProfile`.

The MCP request contains exactly `expected_snapshot_id`, `public_revision`,
`native_revision`, `date_raw`, `owner_character_id`, and `position_key`; the
only valid position is `councillor_steward`. The service validates the complete
same-frame result, rechecks the paused frame after the query, and returns the
native envelope plus readiness, queried snapshot/revisions, schema, scope, and
the normalized request.

`CouncilCompositionCandidatesMcpCapabilityProfile` freezes this transport
shape as a read-only deterministic `CapabilityDescriptor`. It neither copies
the Python implementation nor introduces an open_kaishek MCP client or CK3
process adapter.

## Unsupported and live boundaries

Registration of the MCP function and formal planner consumer does not make the
bottom native query available. The native and hybrid drivers require the
bridge to advertise the exact capability and step; otherwise they return the
existing unsupported path, and the hybrid driver never falls back to another
backend for this pure native observation.

The formal planner can request and consume a complete same-frame observation.
When it finds a vacant steward seat, it can select a deterministic native-legal
candidate, but it stops before mutation because
`game.action.assign-councillor-v1` has no semantic action implementation. This
package therefore records:

- MCP registration and formal query-consumer implementation: present;
- native capability advertisement and runtime certification: pending;
- paused CK3 live evidence and formal strategy live consumption: pending;
- councillor assignment/replacement action and postcondition loop: pending.

The exact build remains CK3 `1.19.0.6`, executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.
This is a static compatibility package and does not advance a gameplay or
whole-campaign milestone.

Reopen it when the upstream candidate lands on master, the bridge advertises
the capability, a paused live result exists, the assignment action is added,
or any request/response field changes.
