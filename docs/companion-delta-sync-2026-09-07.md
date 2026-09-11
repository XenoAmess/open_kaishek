# Companion T0/T1 compatibility delta, 2026-09-07

This offline audit compares companion root
`804ef3dfb5194eedc73e93b3ccf54ad226336130` against the previous audited
boundary `bf314be42b62b69c87c8825bf473286bf524a79b`, using clean
open_kaishek baseline
`main == origin/main == ce62218a01020a55d88b511ba200fd768a3b19d0`.
No CK3 process was started or
attached.

## Compatibility decision

`NO-CODE-CHANGE`: the companion range requires no parser vocabulary, IR node,
finite-runtime handler, profile descriptor, CLI command, or Quarkus service
change in open_kaishek.

The existing exact-build identity remains CK3 `1.19.0.6`, executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.
The audited range adds no `game.command.*` or `game.contract.*` literal. The
open_kaishek G2 capability remains the read-only
`game.command.query-g2-truce-evaluated-days-v1` descriptor with the same eight
response fields and five invariants. The Zhongguo promotion-source,
career-HC/workforce, manager/subordinate selector, and their pinned ABI,
source-contract, and Python-contract files are byte-identical across the
range.

## T1: G2 orchestration delta

The companion adds two parent-owned orchestration surfaces after the previous
three-way intake audit:

- `surrender_execution_readiness` projects the already composed assessment,
  candidate, and six-domain surrender terms into a Python policy result. It
  neither registers an open_kaishek capability nor changes the evaluated-days
  query.
- `xar.ck3.g2_three_way_exit_file_intake.v1` adds a hash-bound offline file
  intake. Manifest v1 is retained, while
  `raiktor-three-way-exit-file-intake-manifest-v2` optionally binds the
  surrender aggregate session. Its verified envelopes retain an empty
  mutation-command list and do not advertise an action or postcondition.

These are consumer/orchestration schemas in the companion repository, not
Paradox opcodes or public open_kaishek parser/IR/runtime transports. Adding a
speculative mirror would create a second authority for policy state, so the
correct compatibility action is provenance-only.

## T0: Phase 2 R192-R208 delta

The range includes the AF5 compensation-loop repair and the retained-session
progression through R208. R201-R208 add or refine exact event-window contracts
for health, spymaster, befriend, annual-summary, elimination, and PP
bargaining/receipt prompts. Those contracts live under the companion `tools/`
acceptance harness; they do not extend open_kaishek's public profile surface.

The product script changes are nevertheless covered as external parser input.
They include purpose-sharded compensation effects and small trigger/effect
repairs. The current 827-file Zhongguo corpus still parses losslessly. This is
syntax/CST coverage only: R208 remains a retained-session harness RED at the
next exact PP event, and neither promotion nor full-tree readiness is promoted
by this audit.

The shared native GUI dispatch implementation was corrected in the companion,
but its fixed review-now request, response fields, invariants, and
default-unadvertised product capability are unchanged. No open_kaishek profile
pin names that implementation file, so there is no stale source hash to
refresh here.

## Verification

- `py -B tools/check_metadata.py`: PASS.
- `mvn -o -ntp clean verify`: BUILD SUCCESS; 156 tests, zero failures,
  errors, or skips.
- `py -B -m unittest discover -s kaishek-zg361-profile/tests -v`: 8/8 PASS.
- `py -B kaishek-zg361-profile/tools/validate_domains.py`: PASS
  (`schema-only; runtime not implemented`).
- Shaded CLI JAR: 362,766 bytes; SHA-256
  `2e2e9f7edee40158cfe960390dcbf4279bf3fa75d8bc61500eb7de7a1ae5ba2e`.
- The three assertions from `kaishek-cli/smoke.ps1`: PASS by direct CLI
  invocation; the local PowerShell policy rejected executing the unsigned
  wrapper itself.
- `synthetic-361`: SUCCESS, explicitly synthetic, with 0 parse and 0
  validation diagnostics.
- Companion `XenoAmess_s_Eternal_Recurrence`: 54/54 files, 2,217,794 bytes,
  zero errors, corpus SHA-256
  `bf36f9e2e8c1cbf1880f4231029bb66a81e66e97684ca877397056b30b633ab5`.
- Companion `mod_zhongguo_style`: 827/827 files, 23,464,880 bytes, zero
  errors, corpus SHA-256
  `33528d077dc79c3ccf02bd3529ea0ffdcfa27747d8043c9af27289c0b3bae2c8`.
- Companion `verify_g2_open_kaishek_compatibility.py --require-checkout
  --require-clean`: `GREEN_STATIC` against the clean `ce62218` baseline; every
  reported compatibility check is true. After this provenance-only commit
  lands, the companion's exact checkout pin must advance to the new
  open_kaishek commit before repeating the same clean-checkout assertion.

Boundaries: `ck3_started=false`, `process_attached=false`,
`save_mutated=false`, `public_capability_added=false`,
`parser_vocabulary_added=false`, `runtime_handler_added=false`,
`runtime_certification_promoted=false`, `decision_ready=false`, and
`automatic_surrender_ready=false`.

## 2026-09-10 follow-up: relocatable G2 adapter runtime paths

Companion commit `dc829a3307ca572a2c8747325f34235cf91a0aa0` adds
`--capture-executable`, `--bridge-dll`, and `--bridge-injector` to the private
source-specific war-loss adapter. Each argument only overrides the local path
for one runtime binary; the frozen manifest still supplies and enforces its
expected SHA-256. Omitting the arguments preserves the previous manifest-path
behavior. The exact runner SHA-256 is
`94E179F41D9BAB852D9A8E5CB50D9D1E7ED9C69F792C4650E93B626BE267E9F7`,
and the updated manifest SHA-256 is
`056D765A29707D206432973960E4863D011C6132ECB5C87F97C72D0A7A4AAA9A`.

`NO-CODE-CHANGE`: open_kaishek does not invoke this Python runner, interpret
its manifest, or forward its process-launch CLI. The new switches do not alter
a native bridge request/response schema, MCP capability, Paradox opcode,
open_kaishek profile descriptor, parser vocabulary, IR/runtime handler, CLI
command, or service endpoint. Therefore the existing G2 metadata and readiness
flags remain correct; copying the three private wrapper arguments into the
public Java CLI would create an unused duplicate launch surface.

The audit started from and retained clean `main == origin/main ==
6d26808df752398e0ba970f05547a81179c31a3b`; `git fetch origin` followed by
`git rebase origin/main` reported the
branch current. Focused Maven verification covered all four G2 metadata/profile
tests: 11 tests, zero failures, errors, or skips, and `BUILD SUCCESS`.
`py -B tools/check_metadata.py` passed, the independent Python domain suite
passed 8/8, and `validate_domains.py` returned
`PASS (schema-only; runtime not implemented)`. No CK3 process was started or
attached, no save was mutated, and no runtime/readiness claim was promoted.

## 2026-09-10 follow-up: portable B1 cycle snapshot MCP

Companion commit `4c4891eb899e736ad4f5c6eaabdaaedc2f296f05` adds the
public read-only MCP tool `ck3_query_zhongguo_b1_cycle_snapshot_v1` and its
exact-build response schema. Unlike the preceding private path overrides, this
is a new consumer-visible capability, so the compatibility decision is
`ADDITIVE-PROFILE-CHANGE`: open_kaishek now records its identity, 68 response
leaves, 14 consumption invariants, 38-variable fixed allowlist, and four
canonical input hashes in `ZhongguoB1CycleSnapshotCapabilityProfile`.

The implementation remains parent-owned. No parser vocabulary, IR/runtime
handler, write action, CLI command, or service endpoint is added here, and all
native/runtime certification flags remain false pending paused live evidence.
The synchronization used `git fetch origin` followed by
`git rebase origin/main`; no merge and no CK3 process were used.

## 2026-09-10 follow-up: portable vanilla-event discovery and evidence MCP

Companion commit `df1ed7cb00ef6c54de602fbf182e6204c77c1202` adds four
offline, read-only MCP tools: knowledge discovery, portable evidence listing,
bounded evidence reads, and exact-build source provenance. This is an
`ADDITIVE-PROFILE-CHANGE`; open_kaishek now pins each request/response envelope,
the four schema identities, provider source hashes, the content-addressed
manifest, and the 167-event source-index dataset in
`VanillaEventPortableAssetCapabilityProfile`.

The evidence transport exposes no machine-local bundle root or blob path.
Reads are addressed by uncompressed SHA-256 and bounded to 64 KiB; source paths
are repository-relative, and caller candidates remain explicitly lexical-only.
All four capability descriptors remain read-only, deterministic, and neither
native- nor runtime-certified. No parser vocabulary, opcode, IR/runtime
handler, CLI/service endpoint, CK3 process access, or write authority was
added. The B1 cycle snapshot compatibility logic from `c552744` is retained
unchanged. Detailed fields and hashes are recorded in
`docs/vanilla-event-portable-assets-capability.md`.

## 2026-09-10 follow-up: portable operator MCP handoff

Companion commit `e684fd05f94290d58e9fb7009046eb95d60468f4` adds
four target-side operator tools under contract `1.0.0` / profile schema `1`.
This is an `ADDITIVE-CLIENT-AND-PROFILE-CHANGE`: open_kaishek pins the public
capabilities/status/preflight/handoff envelopes and provider hashes, then adds
a transport-neutral Java adapter for the read-only readiness sequence and
request-ID-idempotent handoff/recovery.

Endpoint, target ID, job name and request ID are caller/deployment values;
operator identity, commands, process gates and paths remain target profile
data. The adapter contains no operator, machine, campaign, turn or path
constant, accepts no arbitrary command, and launches nothing itself. A first
handoff requires GREEN readiness; an ambiguous response can be retried with
the same request ID without rerunning a preflight that the already-started job
would make RED. See `docs/operator-mcp-compatibility.md`.

## 2026-09-10 follow-up: portable operator MCP job controls

Companion commit `5610de6bae4f5ac7e04ba3bedb25084837608183`
advances the operator contract to `1.1.0` while retaining profile schema `1`.
Optional per-job controls are frozen in the target profile; capabilities and
job status disclose only their names, and the fifth tool sends only the
selected profile-owned payload to the matching live job. This is an
`ADDITIVE-CLIENT-AND-PROFILE-CHANGE`.

open_kaishek now pins the 1.1 provider hashes and control request/response
metadata. The transport-neutral adapter discovers controls before calling
them, preserves request-ID/server-instance idempotency, returns and caches
write `RED` results without an implicit retry, and never accepts caller stdin
bytes. Existing `1.0.0` four-tool servers and profiles remain supported with
an empty control set; existing contract-V1 targets accept the compatible 1.1
server upgrade. Endpoint, target, job, control and request identifiers remain
deployment/caller inputs, with no account, machine, run number or path bound
into the client. No CK3 or promotional-video action is performed.

## 2026-09-10 follow-up: coat-of-arms source probe MCP

Companion commit `16755189c172772669a0d30456da1f977d6b6de0` adds
`ck3_probe_coat_of_arms_source_v1`, a public, parameterized native MCP command.
This is an `ADDITIVE-PROFILE-CHANGE`: open_kaishek now records the three-field
request, result/session-binding evidence, 128 KiB ASCII source boundary and
side-effect semantics in `CoatOfArmsSourceProbeCapabilityProfile`.

The tool is not read-only even when `apply=false`, because detection writes the
OS clipboard and refreshes the designer paste preview. It is not exposed as an
autonomous action and no native/runtime certification is promoted while the
provider's new-DLL per-payload live matrix remains pending. The dedicated
coat-of-arms reader does not extend open_kaishek's Paradox parser vocabulary,
IR/runtime, CLI, or Quarkus service. Detailed compatibility and frozen hashes
are in `docs/ck3-coat-of-arms-source-probe-capability.md`; this synchronization
does not start or attach to CK3.

Companion follow-up `718a60d538249fb6eecd47284e622fd33d71d260`
installs the same native probe hook during bridge worker bootstrap instead of
waiting for the gameplay-snapshot mailbox gate. The compatibility impact is a
provider-pin and readiness-invariant refresh: frontend probing can now become
available before a gameplay snapshot. Tool advertisement, request/result
schemas, status meanings, clipboard/designer side effects and certification
boundary are unchanged. open_kaishek pins the new bridge/test bytes without
adding runtime behavior or launching CK3.

## 2026-09-11 follow-up: Stage 10 target-owned job controls

The companion adds a repeatable target-side `stage10-player-subject` operator
with exactly `status`, `run-stage10`, and `cleanup`. It deliberately omits an
in-place retry and keeps its exact Stage 9 source plus Stage 10 terminal saves
in target-owned artifacts. The action and operator source SHA-256 values are
`64571FE39EDCB0D9811745D9DDD8E57F9E7E113B5812F290AF8263D6B1216FED`
and `8ADFE06C3910DB359FEDB49E36780055A5F27499FB0478B45D415E882BD04DF2`.

This is `NO-PRODUCTION-CODE-CHANGE` for open_kaishek. Contract 1.1 already
discovers arbitrary profile-owned job/control identifiers and forwards only
those identifiers through `operator_control_job`; it does not enumerate
product job names. A focused compatibility test now binds the exact three
controls, forwards `run-stage10`, and rejects unadvertised `retry-stage10`;
the focused test class passes `13/13`.
No Java API, profile schema, provider pin, parser/IR/runtime behavior, endpoint,
credential, machine path, account, or CK3 round is added. No CK3 process or
promotional-video asset is started, attached, or changed.

### Qualified `.390` activation input

The target-side Stage 9/11 job now captures a Stage 10 source only when the
paused `zg361cl.390` frame and manager selector are both observed, archives the
checkpoint immediately, and publishes a receipt bound to those bytes. The
Stage 10 job requires that receipt before any CK3 launch. The current source
hashes are `E9812CD4B2477760E957F7277A274195166CB00C120ED0BBDA2A39498FECA163`
for the Stage 9/11 action, `EB8D1E10DF4BEE810324C28166DF70BD7C018B581B8509578914152CA2E5AC23`
for its operator, and `5A88505525D24C049B905EC66C45B20530A2C222EC93E4EB30B5034AB324FE04`
for the Stage 10 operator.

This remains `NO-PRODUCTION-CODE-CHANGE` in open_kaishek. The activation and
receipt are target-owned job inputs; Operator MCP 1.1 forwards the advertised
control without parsing product-specific activation fields. The existing
`13/13` focused adapter result therefore remains applicable. No Java test is
repeated for this documentation-only sync, and no CK3 round or video asset is
created.
