# G2 truce evaluated-days capability boundary

Date: 2026-09-04 (Asia/Shanghai)

The public capability remains
`game.command.query-g2-truce-evaluated-days-v1` with profile
`ck3-1.19.0.6-g2-truce-evaluator-v1`. Its eight required fields and five
invariants are unchanged: two evaluator reads must agree on one paused frame,
an observed duration is non-negative, and persisted expiry is never inferred
from that duration.

## Provider transition

Companion commit `a3c13246ef32b35e117b08dbb86f61986c1dabe3`
moves the privately proven synchronous leaf-context reader into the default
production build. The installed exact-build preview-entry hook supplies the
transient leaf wrapper and verifies that it is the expected CAddTruce effect
before evaluating duration twice. All private capture/diagnostic build options
remain off in the frozen production candidate at
`0b0fbc047610a8ef25f47a59f7b42c83c176d69e`.

This is a provider/readiness change, not a public schema change. The private
leaf-context build observed two stable `1825`-day results, but the frozen
default production binary has not yet run its paused public-wire acceptance.
Consequently `nativeCertified`, `runtimeCertified`, and `certified` remain
false. `expiry_observable` also remains false, and no termination action is
enabled by this descriptor.

## Hash-bound companion inputs

| companion input | SHA-256 |
| --- | --- |
| source contract | `df720cd33d3606634378a5cff20d77227b82a35265269789bde4a51cff988e0d` |
| production candidate manifest | `6b5783bca00a1b082aa5fec834ee73a95860535549b7525a616c82f178265c58` |
| provider source | `5299c88f4cd7b27959e4518d5a48061ae0ef39ae629a2590c269a8fe912f397a` |
| provider header | `e49d31f35fbb3f5bc713ea94cb9ff3e83ec9fa713772968a0dcffefd20200b2a` |

The exact game binding remains CK3 `1.19.0.6`, executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.
No parser, validator vocabulary, IR/runtime handler, or CLI command changes
are required for this native provider transition.
