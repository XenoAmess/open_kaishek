# CK3 succession-transition compatibility boundary

## Decision

Root commits `9fd9bfd2171d1d19c68acb4fff952cf9abaa4d9f` and
`9b6100f66d9253e8f7dd787309334bb06d43c784` add a private G2-M3
expectation/reconciliation contract and one optional additive
`succession_expectation` member to the root Python driver's existing
`driver-state.json` v2 envelope.

Root commit `0f813a3a5749f0cdee765562c71cd95c70235c4d` adds the private
planner/driver continuation literal `continue-as-reconciled-successor`. It is
advertised only after the predecessor terminal is settled and a current-frame
successor reconciliation fully matches. The action changes only root-local
episode identity and caches; it sends no CK3 command and restarts no process.

The compatibility verdict is
`ROOT_PRIVATE_STATE_AND_ACTION_EXTENSION / OPEN_KAISHEK_NO_CONSUMER /
DOCUMENTATION_ONLY`. A non-documentation repository search finds no
open_kaishek reader for the root driver-state file, no caller for the private
retention/reconciliation methods, no action-step router for either episode
transition literal, and no closed Java type for the new schemas.
No Java, Operator MCP, CK3 profile, endpoint, dependency or version change is
required.

The root loader continues to accept existing v1/v2 state files without the
new member. Same-PID root recovery validates the optional value against the
episode identity. Root checkpoint/seed restore and explicit operator character
rebind discard it and require a fresh root observation. These are root-owned
lifecycle rules; open_kaishek does not reproduce or certify them.

## Provider pins

The current root inputs at `0f813a3a5749f0cdee765562c71cd95c70235c4d` are:

| Provider input | SHA-256 |
|---|---|
| succession transition contract | `9A93BC81C8595888C1EDD4EAF97F2A4A759C28ED20D9DF55177720F1687A44C4` |
| native Python driver | `A44CA53E052957FCB8720FDE9FB1820CF4A8D9AA5FE8D5A90771E050814A3DA5` |
| planner service | `CEFB957D83324786EFFE3147D1AF525D688C6EB8A384F00C8FB6D53DFF0B64E6` |
| one-life strategy | `E80D23FB0578A706750D44FC3E4497AB32B64F1C3511DD138DFBAEFDD4EA1F7D` |
| succession transition document | `C7701EB5DC4623FF360FA53E0AA90C98E3D07B796ABD82265669CD8228B215FF` |

These hashes are provenance only. They do not turn open_kaishek into a second
owner of the root contract and do not certify a production CK3 inheritance
transition.

## Reopen conditions

Add a typed companion profile and focused fixture before claiming compatibility
if any of the following becomes true:

- the expectation or reconciliation schema is exposed through public MCP;
- open_kaishek begins reading root `driver-state.json`;
- open_kaishek begins routing root one-life action literals;
- a root runner result containing these fields becomes a companion input;
- the root makes the optional field mandatory or changes the envelope version.

This decision is path and operator independent. It is bound by Git identities
and content hashes, not by a workstation, account or CK3 round.
