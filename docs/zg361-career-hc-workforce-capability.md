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
| ABI ledger | `8e91879901316bb165983f577887cdf7cb37c30ab7c98cf63d97f3fee77c0928` |
| source contract | `26b1f859cb6194d1d9295443be370862926dc81dabaa57d1f447f62c09a4062e` |
| public JSON schema | `453b9a93fde9d472aa82c687bb2fd3db4fbef06d315ab91dcc4e167fa2f5b6c5` |
| Python query/normalization contract | `398210553c74fdefa800b77f1287570598e4c2c4c73be35f243bec889bc14c76` |
| exact game build | CK3 `1.19.0.6` / EXE `2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86` |

## Readiness boundary

The native provider is wired but remains default-off pending paused live
evidence. The descriptor therefore keeps `nativeCertified=false`,
`runtimeCertified=false`, and `certified=false`; it does not promote a route-B
action or ACK to production-live capability.

This is a native/MCP data-plane addition, not a Paradox language change. No
parser, validator vocabulary, IR handler, runtime handler, or CLI command is
added. Existing full-corpus validator coverage RED remains a separate known
boundary.
