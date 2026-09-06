# Zhongguo B4 career-HC/workforce capability boundary

Date: 2026-09-04 (Asia/Shanghai)

The companion project exposes the read-only exact-build query
`game.command.query-zhongguo-career-hc-workforce-postcondition-v1` through its
native allowlist reader, mailbox, driver, service, and MCP layers. The fixed
step is `query-zhongguo-career-hc-workforce-postcondition-v1`; the case kind is
`zhongguo.career-hc.workforce.route-b-no-hc-debit`.

`ZhongguoCareerHcWorkforceCapabilityProfile` records the public identity,
minimum response projection, exact-build provenance, and business
postcondition invariants. It does not execute the provider, register a Paradox
opcode, or advertise the action cell that precedes the query.

## Public and business boundary

The request contains only `request_nonce`, `expected_revision`, and an owner
filter. The subject is always the paused played character; callers cannot
select a subject, variable name, receipt state/choice, bucket, or arbitrary
character. The observed M360 receipt must bind that subject to the requested
owner, record route B as state `4` and choice `2`, and be read in the same frame
as the career-HC partition and manager-cost total.

A GREEN postcondition requires six non-negative HC buckets, their sum equal to
the authorized total, the conserved flag true, and route-B manager cost equal
to zero. Two complete allowlist reads and the before/after frame identity must
match. Typed-unavailable data cannot become GREEN, and action acknowledgement
is never accepted as a business result.

## Hash-bound companion inputs

| companion input | SHA-256 / identity |
| --- | --- |
| provider integration commit | `5e6fc9a0073ea7bbf9542bb3d95dfcd812c3a1f6` |
| ABI ledger | `ad1cc96a8bb6f3736d5dd69e9b4525ed33a54abab72a5f6c6863a1a08f383496` |
| source contract | `35bfa513acf0d209bf32c26c563698722df8dce03733dac005e2e56e2df076e0` |
| public JSON schema | `453b9a93fde9d472aa82c687bb2fd3db4fbef06d315ab91dcc4e167fa2f5b6c5` |
| Python query/normalization contract | `398210553c74fdefa800b77f1287570598e4c2c4c73be35f243bec889bc14c76` |
| exact game build | CK3 `1.19.0.6` / EXE `2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86` |

## Readiness boundary

The native provider is wired but remains default-off pending paused live
evidence. The descriptor therefore keeps `nativeCertified=false`,
`runtimeCertified=false`, and `certified=false`; it does not promote a route-B
action or ACK to production-live capability.

The current ABI adds only the default-OFF private candidate declaration from
`574187b1eb91175eb6711bd5017b9bac2a061ce7`. The source-contract hash also
tracks the registered formal replay and the purpose-based effect shards from
`061bc1abd2f227ee5b4a32cd752a90f95888b33c`; capability identity, public
inputs, response fields, invariants, and certification flags did not change.

This is a native/MCP data-plane addition, not a Paradox language change. No
parser, validator vocabulary, IR handler, runtime handler, or CLI command is
added. Existing full-corpus validator coverage RED remains a separate known
boundary.
