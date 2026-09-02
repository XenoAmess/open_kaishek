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
`--fixture ck3-calculated-value-014` selects the CK3 1.19.0.6
schema-only loader regression fixture. It contains two supported calculated
range comparisons (`>=` and `<=`) and one observed direct `=` comparison whose
RHS contains `value`/`add`/`subtract`. The fixture is intentionally `RED` with
diagnostic code `CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED`; this is expected
evidence for the loader boundary, not a runtime or semantic certification.
`--fixture none` is a diagnostic parser/validator-only run and intentionally
leaves IR/runtime skipped.
`--fixture ck3-war-days-trigger-11906` selects the CK3 1.19.0.6
schema-only duration-trigger fixture. It exercises `war_days >= 365` and
`war_days < 9125`; parser and validator are GREEN while IR/runtime remain
explicitly SKIPPED because the native evaluator is not runtime-certified.
`--fixture ck3-has-innovation-trigger-11906` selects the CK3 1.19.0.6
schema-only Culture innovation-membership fixture. It exercises scalar
`has_innovation` keys for `innovation_quilted_armor` and
`innovation_war_camels`; parser and validator are GREEN while IR/runtime
remain explicitly SKIPPED because the native evaluator is not runtime-certified.
`--fixture ck3-has-cultural-tradition-trigger-11906` selects the CK3 1.19.0.6
schema-only Culture cultural-tradition-membership fixture. It exercises
scalar `has_cultural_tradition` keys for `tradition_fp1_coastal_warriors` and
`tradition_ep3_imperial_tagmata`; parser and validator are GREEN while
IR/runtime remain explicitly SKIPPED because the native evaluator is not
runtime-certified.
`--fixture ck3-has-cultural-pillar-trigger-11906` selects the CK3 1.19.0.6
schema-only Culture selected-pillar-membership fixture. It exercises scalar
`has_cultural_pillar` keys for `heritage_north_germanic` and `ethos_bellicose`;
parser and validator are GREEN while IR/runtime remain explicitly SKIPPED
because the native evaluator is not runtime-certified.
`--fixture ck3-has-cultural-parameter-trigger-11906` selects the CK3
1.19.0.6 schema-only Culture parameter fixture. It exercises scalar
`has_cultural_parameter` keys for
`knights_slightly_more_prone_to_injury` and `unlock_zhanmadao`; parser and
validator are GREEN while IR/runtime remain explicitly SKIPPED because the
native evaluator is not runtime-certified.
`--fixture ck3-is-acclaimed-trigger-11906` selects the CK3 1.19.0.6
schema-only Character acclaimed predicate fixture. It exercises both scalar
boolean forms `is_acclaimed = yes` and `is_acclaimed = no`; parser and
validator are GREEN while IR/runtime remain explicitly SKIPPED because the
native Character-to-CAccolade reader is not runtime-certified. The fixture
does not introduce an ACCOLADE scope.
`--fixture ck3-can-be-acclaimed-trigger-11906` selects the CK3 1.19.0.6
schema-only Character acclaim-eligibility predicate fixture. It exercises
both scalar boolean forms `can_be_acclaimed = yes` and
`can_be_acclaimed = no`; parser and validator are GREEN while IR/runtime
remain explicitly SKIPPED because the full qualification evaluator is not
runtime-certified. The fixture does not introduce an ACCOLADE scope or call
the application-main helper.
`--fixture zg361-projects-metrics-postcondition-v1` (alias
`zg361-projects-metrics`) selects the schema-only CP #026 to Phase-3 #229
lineage fixture. It exercises receipt cursor/id/revision writes and the
project/metrics source projection, including uppercase `NOT` and trigger-side
scalar `var:*` comparisons. Parser and validator are GREEN while IR/runtime
remain explicitly `SKIPPED`; provenance includes the product capability ID and
`native_certified=false` / `runtime_certified=false`.
`--fixture zg361-promotion-compensation-postcondition-v1` (alias
`zg361-promotion-compensation`) selects the schema-only #147 promotion to
compensation receipt fixture. It exercises the positive/equal serial and
choice/posted revision source shapes while keeping the T and L/AE/AF kernel
case identities outside the cross-product receipt. Parser and validator are
GREEN while IR/runtime remain explicitly `SKIPPED`, with the same explicit
uncertified provenance boundary.

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

## Canonical checkout binding

Parent runners should bind the checkout or shaded CLI explicitly instead of
silently resolving whichever local jar happens to be first on `PATH`:

```powershell
$env:XAR_OPEN_KAISHEK_ROOT = 'Z:\workspace\open_kaishek'
$env:XAR_OPEN_KAISHEK_JAR = 'Z:\workspace\open_kaishek\kaishek-cli\target\kaishek-cli-0.1.0-SNAPSHOT.jar'
```

`XAR_OPEN_KAISHEK_ROOT` must identify a checkout that contains the preflight
contract (at least commit `b306a95` or its descendant), and
`XAR_OPEN_KAISHEK_JAR` must be built from that checkout. A stale checkout or
jar that does not expose `preflight` is `UNSUPPORTED`, never `GREEN`; archive
the resolved repository commit and jar SHA-256 with the parent acceptance
artifact. The variables are optional to the CLI itself, but explicit binding
is required for reproducible parent integration.

## Parent-runner boundary

The intended integration is a subprocess call before `run_acceptance.py` (or
an equivalent CK3 harness) starts the game. The parent can persist the JSON as
an acceptance artifact, then continue to the native runner only when the
preflight exit is zero. This repository owns the offline parser/validator and
fixture contract; wiring a particular parent repository or CK3 save path is a
separate integration change.

## Current parent entry bindings

The phase-two loader callback gate and the G2 truce-duration capture have
different offline coverage. Keep their results separate:

* For the phase-two loader callback gate, run the intended frozen source root
  with `synthetic-361-014`. The root parser is useful for detecting malformed
  input before launch, but there is no offline fixture for callback/node
  identity, vtable, thread, lifetime, or completion. Those are native
  observations. Return-address capture and selection among native outer callers
  are the same native-only boundary; they do not justify a script opcode or a
  new fixture. This also applies when an observed outer continuation is an
  exact executable RVA such as `0x3B9CFD2`: PDATA ownership, local CFG, and its
  native call context are not script-profile inputs. A parser GREEN or the
  synthetic runtime GREEN must not promote loader readiness; a bounded
  full-root validator RED remains a schema-coverage boundary.
* For G2, run the intended frozen source root with
  `ck3-war-days-trigger-11906`. The dedicated fixture checks the observed
  scalar integer comparisons and leaves IR/runtime `SKIPPED`. It cannot produce
  the live `evaluated_days` input/result, expiry, war binding, policy, action,
  or postcondition. Traversal of the loaded native effect tree, including a
  Context node's scope count and child vtable shape, is likewise outside the
  offline script-profile contract. A next-layer corpus expressed as native
  vtable identities and vector counts (for example one `1/1` subtree and one
  `6/6` subtree) remains native capture input; it is not a new Paradox opcode,
  scope, or schema fixture.

Neither entry justifies a new opcode from the current evidence. The registered
descriptors remain `certified=false`, and offline preflight remains prior to,
not a replacement for, one bounded exact-build native observation.
