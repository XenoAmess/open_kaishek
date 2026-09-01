# Offline preflight contract

`kaishek-cli preflight` is the small, deterministic gate that a parent CK3
acceptance runner can invoke before it crosses the game-launch boundary. It
does not start CK3, load a native bridge, contact MCP, mutate a save, or use
the network. The report is an early static/fixture signal; it is not a live
CK3 certification artifact.

## Invocation

Build the standalone CLI, then run one command from the repository root:

```powershell
mvn -o -ntp -DskipTests package
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar preflight `
  --root Z:\ck3_mod_rewrite\XenoAmess_s_Eternal_Recurrence `
  --profile ck3-1.19.0.6-zg361 `
  --fixture synthetic-361-014
```

`--root` is optional. It may name a directory or one `.txt`/`.gui` file;
supported files are scanned in stable relative-path order. The default profile
is `ck3-1.19.0.6-zg361` and the default fixture is `synthetic-361-014`.
`--fixture appeal-014` selects the checked-in offline appeal replay vectors.
`--fixture none` is a diagnostic parser/validator-only run and intentionally
leaves IR/runtime skipped.

The command writes exactly one JSON object to stdout. The stable top-level
fields are:

* `tool_version`, `profile_id`, and `build_fingerprint` identify the selected
  static profile;
* `fixture_id` identifies the requested offline fixture;
* `parser`, `validator`, `ir`, and `runtime` contain stage status and bounded
  diagnostics/samples;
* `root_scan.parser` and `root_scan.validator` preserve the external-root
  result separately from the fixture result; and
* `provenance` records `mode=offline`, the root hash, fixture scope, and the
  explicit `ck3_started=false` / `save_mutated=false` / `network_used=false`
  boundaries.

Each stage uses `GREEN`, `RED`, or `SKIPPED`. Exit code `0` means every
requested stage is green. Exit code `1` means a parse, validation, IR, or
runtime-fixture failure (including a missing/malformed root). Exit code `4`
means an unsupported profile/semantic selection. A parent runner should
archive the JSON and gate CK3 launch on exit code `0`; it should still report
the later native/live result separately.

The CK3 profile currently contains a deliberately bounded certified schema.
Therefore a full mod tree can correctly return `RED` for unsupported syntax or
semantics. Pass the smallest intended source slice when using preflight, and
do not interpret a fixture-only `GREEN` as `fixture-live`, differential
certification, or product readiness.

## Parent-runner boundary

The intended integration is a subprocess call before `run_acceptance.py` (or
an equivalent CK3 harness) starts the game. The parent can persist the JSON as
an acceptance artifact, then continue to the native runner only when the
preflight exit is zero. This repository owns the offline parser/validator and
fixture contract; wiring a particular parent repository or CK3 save path is a
separate integration change.
