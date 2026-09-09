# CK3 vanilla-event knowledge MCP compatibility boundary

Date: 2026-09-09 (Asia/Shanghai)

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

The companion integration commit was still being assembled when this
open_kaishek projection was created. The exact reviewed API bytes are therefore
bound directly:

| companion input | SHA-256 |
| --- | --- |
| `ck3_autonomous_player/src/xar_autoplayer/vanilla_events/registry.py` | `493c4bf9ece11e0b2de01ea03fe39e296c6d5e2d52f2fb5d27baabb3a834bbb9` |
| `ck3_autonomous_player/src/xar_autoplayer/bridge/mcp_server.py` | `d1490c78446a8f0bb725ab977cecf7acd6c198a6cf040d48dffeaf300a1fa9ae` |

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
a second owner of the 156-record registry.
