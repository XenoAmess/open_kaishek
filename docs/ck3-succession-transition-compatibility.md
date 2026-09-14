# CK3 succession-transition compatibility boundary

## Decision

Root commits `9fd9bfd2171d1d19c68acb4fff952cf9abaa4d9f` and
`9b6100f66d9253e8f7dd787309334bb06d43c784` add a private G2-M3
expectation/reconciliation contract and one optional additive
`succession_expectation` member to the root Python driver's existing
`driver-state.json` v2 envelope.

The compatibility verdict is
`ROOT_PRIVATE_STATE_EXTENSION / OPEN_KAISHEK_NO_CONSUMER /
DOCUMENTATION_ONLY`. A non-documentation repository search finds no
open_kaishek reader for the root driver-state file, no caller for the private
retention/reconciliation methods, and no closed Java type for the new schemas.
No Java, Operator MCP, CK3 profile, endpoint, dependency or version change is
required.

The root loader continues to accept existing v1/v2 state files without the
new member. Same-PID root recovery validates the optional value against the
episode identity. Root checkpoint/seed restore and explicit operator character
rebind discard it and require a fresh root observation. These are root-owned
lifecycle rules; open_kaishek does not reproduce or certify them.

## Provider pins

The root inputs at `9b6100f66d9253e8f7dd787309334bb06d43c784` are:

| Provider input | SHA-256 |
|---|---|
| succession transition contract | `5553FAD5DDB9DFAEEEDDD6175C9ECEF149F8227719AC6E857BB982546E083641` |
| native Python driver | `1BFF5415FBF049D28A525C510826B630D5A2E428BAF521B8B03D7A79F524756C` |
| succession transition document | `B992E6F7EFC0A73851F3D409A5CBDC3486D1380C0C71CE5E8547EE1411445AB8` |

These hashes are provenance only. They do not turn open_kaishek into a second
owner of the root contract and do not certify a production CK3 inheritance
transition.

## Reopen conditions

Add a typed companion profile and focused fixture before claiming compatibility
if any of the following becomes true:

- the expectation or reconciliation schema is exposed through public MCP;
- open_kaishek begins reading root `driver-state.json`;
- a root runner result containing these fields becomes a companion input;
- the root makes the optional field mandatory or changes the envelope version.

This decision is path and operator independent. It is bound by Git identities
and content hashes, not by a workstation, account or CK3 round.
