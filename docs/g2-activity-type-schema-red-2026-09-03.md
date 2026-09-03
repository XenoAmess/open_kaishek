# G2 activity-type schema boundary (2026-09-03)

## What changed

The independent `open_kaishek` checkout now carries a deliberately negative
compatibility fixture for the current G2 activity source. It is a small,
reproducible slice of
`common/activities/activity_types/zg361_jingcha.txt` containing five
activity-type-owned keys:

`province_filter`, `phases`, `on_start`, `on_complete`, and
`guest_invite_rules`.

The lossless parser returns `GREEN`; the conservative CK3 1.19.0.6 profile
validator returns `RED` with five `UNKNOWN_OPCODE` diagnostics. This is an
explicit schema-coverage boundary, not a parser failure and not evidence that
the game rejects exactly these five keys at runtime.

## Companion observation

The parent project's no-launch preflight scanned the frozen Phase2/G2 corpus
with parser `GREEN` and validator `RED`. The report identified
`common/activities/activity_types/zg361_jingcha.txt:UNKNOWN_OPCODE` in its
bounded samples, with corpus SHA-256
`28e681358558f5e975fae911bf5ddf54eacb3c95dac3133852eaaeca6425c284` and
`172255` validator diagnostics. The checked-in fixture stores those values as
provenance only; it does not embed the full companion corpus.

## Deliberate boundary

No activity key was added to `Ck3Profile11906`, no opcode allow-list was
changed, and no exact-build activity evaluator was inferred. Before converting
this RED fixture into a positive schema slice, the next work must freeze CK3
1.19.0.6 and obtain exact-build source/call-chain evidence for activity
top-level ownership, scope, and evaluation timing. Until then:

- parser `GREEN` means only that the source is syntactically readable;
- validator `RED` means activity vocabulary coverage is incomplete;
- IR/runtime remain `SKIPPED`;
- `ck3_started=false`, `process_attached=false`, and `mutation_sent=false`.

## Reproduction

From the repository root:

```powershell
mvn -o -ntp -pl kaishek-zg361-profile -am test
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar `
  preflight --fixture ck3-g2-activity-type-schema-red-11906
```

The fixture preflight intentionally exits `1` and emits
`"status":"RED"`; this is the expected result until the schema gap is
resolved with evidence.
