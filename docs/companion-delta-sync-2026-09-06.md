# Companion T0/T1 compatibility delta, 2026-09-06

This offline audit compares companion root
`20176498ad03f843b298f58f80c15be0eefa5e01` against the last open_kaishek
source-provenance boundary `a05b94e545fc6074fa2ffae2ffa76e34d9990d62`.
No CK3 process was started or attached.

## Repository reconciliation

The default checkout was clean and fast-forwarded from `4c1f686` to
`origin/main` `89abe057ddcf047f14167e2b0357082680128f42`. Those five upstream commits
are Stellaris-only and do not alter the CK3/Zhongguo profiles.

Every removed branch was checked against `origin/main`, and every worktree
attached to a removed branch was checked clean before removal:

- Patch-equivalent: `feat/cli-batch-replay` (`1e4c432`) and
  `feat/zg361-appeal-replay` (`308d2ba`).
- Ancestors with no unique commits: `sync/b2-lazy-trigger-ee4fab9`,
  `sync/b3-explicit-and-audit-20260904`, `sync/b7-promotion-20260904`,
  `sync/g2-cleanup-dispatch-20260904`,
  `sync/g2-cleanup-receipt-20260904`, `sync/g2-expiry-20260904`,
  `sync/g2-production-leaf-0b0fbc0`,
  `sync/g2-production-leaf-live-20260904`,
  `sync/g2-war-loss-candidate-20260904`, and
  `sync/promotion-candidate-switch-20260904`.
- Stale remote-tracking ancestor removed locally:
  `local/runtime-cli-20260901` (`7d32431`).
- Actual remote branches deleted after the same proof:
  `feat/cli-batch-replay`, `sync/b2-lazy-trigger-ee4fab9`,
  `sync/b3-explicit-and-audit-20260904`, `sync/g2-cleanup-dispatch-20260904`,
  `sync/g2-production-leaf-0b0fbc0`,
  `sync/g2-production-leaf-live-20260904`, and
  `sync/promotion-candidate-switch-20260904`.

Five clean detached historical worktrees were retained because they are not
branch refs and the cleanup request did not authorize guessing that their
detached state is disposable: `_open-kaishek-cultural-parameter-20260902`,
`_open-kaishek-next-accolade-20260902`, `_open_kaishek_b306_build`,
`open_kaishek_preflight`, and `open_kaishek_preflight-20260902`.

## Compatibility decisions

The only exact profile-pin change is career-HC/workforce:

- The ABI hash advances from `8e9187...` to
  `ad1cc96a8bb6f3736d5dd69e9b4525ed33a54abab72a5f6c6863a1a08f383496`
  because it now records the default-OFF private candidate switch. Production
  advertisement remains false.
- The source-contract hash advances from `26b1f8...` to
  `35bfa513acf0d209bf32c26c563698722df8dce03733dac005e2e56e2df076e0`.
  Its semantic query surface is unchanged. The delta records a formal replay
  gate plus the new purpose-based effect output paths after sharding; public
  inputs, 77 response fields, 12 invariants, schema hash, Python contract hash,
  and provider implementation are unchanged.

The source-specific G2 live adapter remains
`static-ready-live-command-default-off` and unexecuted. Its manifest hash is
`7e8b41980d112d40450cb830b54a09ace8a7b9499340afd5a96ede112ca4386b`;
source-specific loss, comparison input, three-way comparison, decision, and
automatic-surrender readiness are all false. The new owner-budget and
white-peace comparison providers are Python policy inputs, not public
open_kaishek parser/IR/runtime commands. No new `game.command` or
`game.contract` identifier was introduced in the audited root range; the only
added command references are existing pause, speed, and event-option actions.

Consequently this synchronization adds no parser vocabulary, IR node, runtime
handler, Paradox opcode, public capability, or action advertisement. Script
corpus parsing below establishes syntax/round-trip coverage only; it does not
certify CK3 lazy evaluation, engine loading performance, or gameplay semantics.

## Verification

`mvn -o -ntp clean verify` completed with `BUILD SUCCESS`: 156 JUnit tests,
zero failures, errors, or skips. The independent domain suite passed 8/8, the
domain validator returned `PASS`, and the dependency-free CLI smoke passed.
The shaded CLI JAR is 362,749 bytes, SHA-256
`e77f54c2b427dd41109b5bc245eb10eb9232f219a0ed73f513a5322e89db407b`.

The same JAR parsed both current companion corpora losslessly:

| corpus | files | bytes | errors | corpus SHA-256 |
| --- | ---: | ---: | ---: | --- |
| `XenoAmess_s_Eternal_Recurrence` | 54 | 2,217,794 | 0 | `bf36f9e2e8c1cbf1880f4231029bb66a81e66e97684ca877397056b30b633ab5` |
| `mod_zhongguo_style` | 827 | 23,460,956 | 0 | `a45aa985afc53e3535f22589aef1bfa773aec73a81ea62ae386251ae1e176b78` |

The root compatibility verifier is rerun after the exact open_kaishek commit
is pinned in the companion fixture.

Boundaries: `ck3_started=false`, `process_attached=false`,
`save_mutated=false`, `public_capability_added=false`,
`runtime_certification_promoted=false`, `decision_ready=false`,
`automatic_surrender_ready=false`, `gen_034_resolved=false`.
