# G2 actual truce-expiry candidate boundary

Date: 2026-09-04 (Asia/Shanghai)

Companion commit `04c1a00f0599378dfa8810be14ce535b2ed17f21`
adds the read-only exact-build candidate
`game.command.query-raiktor-actual-truce-expiry-v1-N`. Its CMake option
`XAR_CK3_ENABLE_G2_ACTUAL_TRUCE_EXPIRY_CANDIDATE_V1` defaults to `OFF`, and
the ordinary adapter capability list therefore does not advertise it.

`G2ActualTruceExpiryCandidateMetadata` records identity, source hashes, and
negative readiness boundaries only. It exposes no `CapabilityDescriptor` and
adds no parser vocabulary, IR/runtime handler, opcode, or CLI command.

The candidate distinguishes the post-application persisted one-way truce row
from the earlier duration evaluator. An available future result requires two
equal native reads on one paused frame, native `has_truce=true`, and a
persisted expiry later than the observed current date. Transport ACK is not
state evidence and cannot make the candidate ready.

Companion commit `f16cdf0d63df06f4e6b0bbde08f6324e25c3d885`
freezes a deterministic retention ticket from the existing live evaluated-days
receipt and static war-bound loss candidate. The retained values are 598
pre-termination soldiers and 1825 evaluated days. That ticket does not
authorize a live run or bind a termination action.

| companion input | SHA-256 |
| --- | --- |
| actual-expiry source contract | `9b71a5001453970df851e7b0d908929f5b598a0efceafc9ed7438a4d3bb214a3` |
| actual-expiry ABI ledger | `422352b9989259f6f9060d47b5763c5e81b355cc36130124652d5a60cd78b6a7` |
| Python contract | `55fe165e5c001cd6858c4c47345fc8343dc270c9d77b7db9678e2d5596cf405c` |
| candidate header | `43b90ee1f4ebaae83785c786e9c38d9a7396bab07858f0ccd702165181572747` |
| candidate source | `9074c83277697f1f652fb7216bda4647694ac1ef545bcd5f483055afec4c747c` |
| retention manifest | `21d5e530df76d80ec5919f536276bcb0340a607d0c83dbbd73e6451b724d5e91` |
| retention runner | `ea4b81a037c59b20f64e3e6800e9bec7e4de8ec856c1d0029639edf620498e04` |

Actual expiry observation, native/runtime certification, production live,
decision readiness, automatic surrender, and GEN-034 all remain false.
