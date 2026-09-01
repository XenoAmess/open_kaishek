# Synthetic 361 vertical slice (offline only)

This document records the first executable pipeline for `open_kaishek`.
It is deliberately a synthetic fixture, not a CK3 adapter or a live
certification artifact.

The fixture is part of the Phase 0/M0 static-readiness evidence; the formal
readiness decision and its limits are recorded in
[`0003-m0-formal-readiness.md`](decisions/0003-m0-formal-readiness.md).
`runtime-fixture` is the highest status supported by this document. It cannot
be promoted to `fixture-live`, `differential-certified`, or `product-live`
without a paused CK3 artifact and an exact-build comparison.

## Slice boundary

The fixture models one small part of the B2 appeal/PIP area: mechanism `014`
with three namespaced operations and a finite state sequence:

```text
delivered -> appeal_open -> closed
```

`Synthetic361Fixture` generates the UTF-8-BOM source
`common/scripted_effects/zg361_synthetic_014.txt`.  Its bytes are passed
unchanged through the following stages:

```text
generated .txt bytes
  -> Parser (lossless CST and diagnostics)
  -> Validator (synthetic schema and parameter arity)
  -> StrictIrCompiler (typed IR with source spans)
  -> IrExecutor + RuntimeKernel (finite in-memory state)
```

The synthetic profile is `zg361-synthetic-014`, uses an all-zero executable
fingerprint by design, and certifies only the three fixture handlers.  The
profile must never be substituted for `ck3-1.19.0.6`.

## Offline appeal replay extension

`Appeal014Replay` adds one deliberately narrow, runtime-fixture slice around
the companion 014 contract: an already-posted treasury/gold/merit receipt set,
an owner+subject binding, and one finite D+90 deadline.  A matching reviewer
can resolve before the due day and refund each receipt once.  Reopened or
revised bindings make the queued deadline a marked stale no-op; repeated
resolution and a deadline after a successful resolution are idempotent
no-ops.  The queue is the repository's finite `ExecutionContext` fixture and
is not an inference about CK3's scheduler.

`Appeal014DifferentialFixture` keeps four hand-authored expected vectors
(`resolved-before-deadline`, `expires-at-deadline`, `stale-after-reopen`, and
`stale-after-revision`).  The JSON sidecar records the source paths and
SHA-256 values used for the read-only comparison:

```text
kaishek-zg361-profile/src/test/resources/zg361-014-appeal-differential.json
```

The source files are not copied into this repository.  The vectors provide
offline differential evidence for binding, deadline, queue, and receipt
accounting only; they do not promote the profile beyond `runtime-fixture` or
certify the existing CK3 implementation.

## Fail-closed rules exercised

- A parser error yields an empty, non-executable IR program.
- An unknown nested operation yields `UNSUPPORTED_UNKNOWN_OPCODE` and never
  reaches the runtime.
- A known CK3 opcode from the exact-build profile is still
  `NOT_CERTIFIED`; the executor refuses the whole program before any write.
- Runtime handlers reject illegal state transitions and choices with an
  explicit `INVALID` result; no default branch silently mutates state.
- `IrExecutor` performs an executable preflight, so unsupported instructions
  cannot execute a prefix and then disappear.

## Reproduce

From the standalone repository root (the local Maven cache is used for the
offline verification environment):

```powershell
$m2 = Join-Path $env:USERPROFILE '.m2\repository'
$env:MAVEN_OPTS = "-Duser.home=$env:USERPROFILE"
mvn -o "-Dmaven.repo.local=$m2" -pl kaishek-zg361-profile -am test
```

`Synthetic361PipelineTest` contains the positive file-boundary and execution
assertions plus the unknown-opcode and CK3-not-certified negative paths.  A
successful run is evidence for the `runtime-fixture` level only.  It does not
prove CK3 equivalence, MCP differential certification, scheduler behavior, or
product-live readiness.

`Appeal014ReplayTest` runs the four expected-vector comparisons and the
insufficient-balance/stale-scope negative paths in the same offline command.

## Preflight entry point

Parent CK3 acceptance can run the repository-level offline gate before
launching the game:

```powershell
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar preflight `
  --profile ck3-1.19.0.6-zg361 --fixture synthetic-361-014
```

The command emits the stable `open_kaishek.preflight.v1` JSON contract and
returns non-zero for a failed parser, validator, IR, or fixture stage. See
[`offline-preflight.md`](offline-preflight.md) for root scanning, exit codes,
and the explicit no-CK3/no-save boundary. A passing fixture remains
`runtime-fixture` evidence only.
