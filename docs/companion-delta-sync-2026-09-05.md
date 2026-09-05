# Companion T0/T1 compatibility delta, 2026-09-05

The synchronization starts from clean `main == origin/main ==
37cab82ec54a70fde79351af7240ed3d49c96adb`. It compares companion range
`ee4fab944737685b21d7cc1f18ff572cf0238d90..cee3fac` and reuses previous
unchanged reactor/parser evidence instead of repeating the full corpus.

## Actual changes and coverage

- T0's promotion transport ABI pin advances to `d077bcf` for the fixed-name
  GUI descendant fallback ledger. Source and Python transport contracts,
  capability IDs, request/output fields and readiness invariants are unchanged.
- T0's stale named Character event-window output belongs to the native/Python
  bridge, not an open_kaishek descriptor or runtime consumer. Decoder work is
  `not-applicable` here; no synthetic identity or event-choice evaluator is added.
- B1's `is_alive` list filter and B2's nested first-use `trigger_if` guards
  exercise existing scalar/block AST shapes. `is_alive` is already a BOOLEAN
  trigger descriptor. Weak Character lifetime, list rebuilding, variable write
  ordering and CK3 lazy evaluation are not certified finite-runtime semantics.
  The parser cannot establish whether the attempted B1 fix resolves the live
  error; only the companion delayed-consumer run can establish that.
- Promotion event timing/role contracts and failure timeline capture are
  companion Python choreography, not Paradox schema or wire changes.
- T1's R3 cleanup/expiry primitive is now backed by its actual immutable native
  artifact. The existing metadata records that companion private gate as
  live-tested and runtime-cleanup-ready, separately from the unchanged
  synthetic fixture. No public action, comparison policy or open_kaishek
  runtime evaluator/certification is introduced.

Profiles remain `ck3-1.19.0.6-zg361`,
`ck3-1.19.0.6-zg361-promotion-source-transport-v1` and
`ck3-1.19.0.6-g2-postwar-cleanup-expiry-adapter-v1`. CK3 build is `1.19.0.6`,
EXE SHA-256
`2d00ff3101ef70b566f2fcbae292f09263199c80e9dc8f139b82d7d96f83db86`.

## One focused verification

Actual command, from this repository:

```powershell
mvn -o -ntp -pl kaishek-cli -am '-Dtest=ZhongguoPromotionSourceTransportCapabilityProfileTest,G2PostwarCleanupExpiryAdapterMetadataTest' '-Dsurefire.failIfNoSpecifiedTests=false' package
```

JDK `C:\jdk-21\bin\java.exe` and Maven `C:\apache-maven\bin\mvn.cmd`;
`7 tests`, zero failures/errors/skips; `BUILD SUCCESS` at
`2026-09-05T10:24:07+08:00`. An initial unquoted comma in PowerShell was rejected
before Maven started, then corrected; it was command-shell syntax, not code RED.
The rebuilt CLI JAR is 358,078 bytes, SHA-256
`bb94cd9142112a62df57b901ca5e008b3a8ec0c05feec6ec3d3a7551df5512c9`.

Each following changed source was passed once to
`java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar parse <absolute-file>`
under companion `Z:\ck3_mod_rewrite\_root-promo-split-20260902\mod_zhongguo_style`:

| source | bytes | SHA-256 | result |
| --- | ---: | --- | --- |
| `common/scripted_effects/zg361_b1_runtime_effects.txt` | 258,059 | `97405658627c3589e7ca173164e75919cb5b2e257877253aff3e0fb2746f1df8` | PARSED, roundTrip=true, 0 diagnostics |
| `common/scripted_effects/zg361_b2_debt_consumers_effects.txt` | 17,725 | `080b03e4a87fbc3e009957b22d3ed4e81c20ffda47b5397ef0941caab699b3b0` | PARSED, roundTrip=true, 0 diagnostics |
| `events/zg361_b1_runtime_events.txt` | 29,692 | `343fda673773848f4bda8ee66952d844e4629ca739d02da56ebf8968e146af1d` | PARSED, roundTrip=true, 0 diagnostics |
| `events/zg361_events.txt` | 24,092 | `cf6d2461739b05ab2f24ba3396858535c9c320207db12d021f9cf10b95271b84` | PARSED, roundTrip=true, 0 diagnostics |

All four exits were zero. Validator/IR/runtime were not rerun for unsupported
whole-product semantics. No product source was copied into this repository.
No effect file was changed by this synchronization; the B1 historical-size
exception is not a new B2+ exception or proof about CK3 loader performance.

`ck3_started=false`, `process_attached=false`, `save_mutated=false`. This
offline synchronization does not upgrade T0 promotion-loop readiness, G2
decision readiness or GEN-034. See the linked
[promotion transport](zg361-promotion-source-transport-capability.md) and
[R3 metadata](g2-postwar-cleanup-expiry-adapter.md) records for exact source and
live evidence boundaries.

## Follow-up: B1 old-save active witness

Companion commit `a05b94e545fc6074fa2ffae2ffa76e34d9990d62`
changes only the producer-side truth condition for the existing
`zg361_promotion_source_b1_active` fixed widget. A directly loaded pre-split
manager may now be recognized by the existing manager-only
`zg361_b1_policy_next_review_serial` witness when the new manager cycle serial
has not yet been migrated. The transport still returns the same fixed widget's
`widgets.effective_visible` field; no request/output field, capability ID,
allowlist entry, ABI, native decoder, parser vocabulary, IR or runtime handler
changes.

`ZhongguoPromotionSourceTransportCapabilityProfile.ROOT_INTEGRATION_COMMIT`
therefore advances to `a05b94e545fc6074fa2ffae2ffa76e34d9990d62` as an exact
source-provenance update. Profile ID, source-contract/ABI/Python-contract
hashes, invariants and every readiness/certification flag remain unchanged.
The focused Java profile test and one lossless parse of the changed scripted
GUI are sufficient offline checks; parser success is not CK3 runtime evidence,
and no live-readiness claim is promoted by this synchronization.

The focused Maven package completed at `2026-09-05T12:26:56+08:00` with
`3 tests`, zero failures/errors/skips and `BUILD SUCCESS`. The resulting CLI
JAR is 358,077 bytes, SHA-256
`b1db12aff8fd918547e64694f8b43cf0287de4c06021340677865fcfc2a9801a`.
Parsing the companion GUI at `a05b94e` returned `PARSED`, 2,566 bytes, 641
tokens, 20 blocks, `roundTrip=true` and zero diagnostics; its SHA-256 is
`31ad1ee16f4f2ae4da99b40455330c20253b36e2c287e84c9cc688b1a23a5568`.
`git diff --check` was GREEN. No CK3 process was started or attached and no
save or product source was modified by this external-repository update.
