# Zhongguo B3 manager/subordinate selector capability boundary

Date: 2026-09-04 (Asia/Shanghai)

The companion project at canonical commit
`fefb408e13c4ea2aa4c512d3e3900991f9c13f7b` exposes the read-only exact-build
query `game.command.query-zhongguo-manager-subordinate-selector-v1` through its
native mailbox, driver, service, and MCP layers. The fixed driver step is
`query-zhongguo-manager-subordinate-selector-v1`, and the selector kind is
`zg361-bounded-ai-direct-manager-selection-v1`.

`ZhongguoManagerSubordinateSelectorCapabilityProfile` records the public
identity, minimum response projection, exact-build provenance, and selection
invariants as an immutable `CapabilityDescriptor`. It does not implement or
execute the native selector, register a Paradox opcode, or advertise the
downstream B3 manager-governance action cell.

## Public boundary

The request contains only `request_nonce` and the paused snapshot's
`expected_revision`. Callers cannot provide manager/subordinate IDs or assert
AI, government, rank, or relationship eligibility.

An available result contains one provider-observed manager/subordinate pair,
their source contract identities, the manager's primary-title and government
projection, a complete readiness block, exact-build provenance, and the
service's paused-snapshot binding. The manager must be a living AI-controlled
`celestial_government` landed duke-or-higher direct vassal of the player; the
subordinate's immediate liege must be that manager. Selection preserves the
first complete pair in nested native contract order.

The provider performs two complete selection reads in one paused
application-main frame. Both selections and the before/after frame identity
must match. Malformed relationship storage is typed unavailable and is not
silently reclassified as “no candidate”; unavailable results never leak a
candidate.

## Hash-bound companion inputs

| companion input | SHA-256 / identity |
| --- | --- |
| canonical integration commit | `fefb408e13c4ea2aa4c512d3e3900991f9c13f7b` |
| selector ABI ledger | `b10d596bdc18842c4a582a932affd12fd035382c879477625a18f6a4417bf55a` |
| selector source contract | `b175ea8231e22614c144abad1b108b9f43ed60a9f9e36406ca00017700851057` |
| Python query/normalization contract | `26e5e2ce6ddc13c496ac497c476de3ebf935788b386ae6fdf5a58c049332a7b7` |
| exact game build | CK3 `1.19.0.6` / EXE `2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86` |

## Readiness boundary

The companion contract is static/fixture ready but has no production live
artifact. Accordingly the open_kaishek descriptor remains read-only and
deterministic with `nativeCertified=false`, `runtimeCertified=false`, and
`certified=false`. The T0-only action-cell helpers are internal and
live-pending; they are intentionally absent from this public capability
profile.

This is a native/MCP data-plane addition, not a Paradox syntax or opcode
change. No parser, validator vocabulary, IR handler, runtime handler, or CLI
command is added. Existing full-corpus validator coverage RED remains a
separate known boundary.
