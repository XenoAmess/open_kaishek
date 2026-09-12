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
hashes are `6C6D486908394F1A6CD5E3CB7BF394DA063CD8BE33965C2C9BEBF3E1FC2BCDBD`
for the Stage 9/11 action, `8884FA14CC34B1896E32F0B569F51CF527AD1BD6B2FD76F7C0EB2EE9C0C575FB`
for its operator, and `D544708327D9D38865AAC1C36EFE934CD82FE704CB9CBAB00D06465F684E36EB`
for the Stage 10 operator.

This remains `NO-PRODUCTION-CODE-CHANGE` in open_kaishek. The activation and
receipt are target-owned job inputs; Operator MCP 1.1 forwards the advertised
control without parsing product-specific activation fields. The existing
`13/13` focused adapter result therefore remains applicable. No Java test is
repeated for this documentation-only sync, and no CK3 round or video asset is
created.

## 2026-09-11 follow-up: G2 live admission manifest refresh

Root commit `c7a4cf234bd3596e3ebd2f2668615208c1300d56` refreshes one
whole-file SHA-256 in the target-owned G2 source-specific war-loss live
manifest. The shared Phase2 module had acquired unrelated later behavior; an
AST comparison showed that all seven definitions consumed by the G2 adapter
remain identical to its frozen source. The new manifest SHA-256 is
`4A370EEAE10B588ABD6730D2F4E0FC0998A73D80ADECE1A8CF778F2F54D73079`.

This remains `NO-PRODUCTION-CODE-CHANGE` for open_kaishek. Operator MCP 1.1
treats manifest paths and hashes as target profile inputs and does not parse
the G2 manifest. A fresh target-side no-launch run returned
`READY_TO_RUN_G2_SOURCE_SPECIFIC_LIFECYCLE`; its receipt SHA-256 is
`81A9F1D38FF1ED1F86FC89652A3D268D9F1ADCC336FFCE91ED5C729CEDCFAE4C`.
The existing adapter test result remains applicable, so no Java test is
repeated. No CK3 process or promotional-video asset was created or changed by
this sync.

### R440 blocker repair refresh

Root commit `a911c253ec29d3de1b204d4e4b91780e3a81e891` replaces the
G2 adapter's ineffective blind event shortcut with its existing verified OCR
recovery. The target-side adapter and manifest SHA-256 values are now
`CE9FF6D910D68003EA2768AA28E4B4871595FE648EB9EDDA3CCFECC9C4224848`
and `29549DFC108DFB734A3FB38D2AF00DE3C042DD928E71E9324F1FAD1C0B4113DE`.
R440 stopped before source capture on a stock single-option event; its harness
RED receipt SHA-256 is
`4016A49E72071DCA176224C5AE1283DBBC2293685F384EDB00023DFE7520B441`.

This is still `NO-PRODUCTION-CODE-CHANGE` for open_kaishek. The job command,
manifest and runtime evidence remain target-owned inputs, and Operator MCP 1.1
does not interpret their product fields. No Java API/schema/profile change or
new test is required. The target's focused Python tests and no-launch preflight
are GREEN; the live retry remains pending, and no readiness is promoted here.

### Hash-bound G2 resume admission

Root commit `0221a419780e41a2911ff7309389784c47174da6` adds the optional
target-side pair `--resume-save / --resume-save-sha256`. The adapter checks an
exact CK3 `1.19.0.6` save header and SHA-256 before launch, copies it into a
fresh userdir, and verifies destination size and hash before creating CK3. It
preserves `startup_mode=normal-event` and adds
`startup_source=resume-checkpoint` to the target-owned launch receipt. The new
adapter and manifest SHA-256 values are
`13202ADABC42D0A777EA22B946B9988CC9A9B287AAAF16C3D80C97CBDAAAB33C`
and `A2C2A93F08E23074D18B3181D7CF6C4ADDA5635C9B5E609BD736333564D59763`.

Operator MCP 1.1 continues to forward only profile-frozen target commands and
does not parse these adapter arguments or receipt fields. Therefore this is
`NO-PRODUCTION-CODE-CHANGE` for open_kaishek and requires no Java API, schema,
profile or test update. The target's focused Python tests are GREEN, and its
real-save no-launch receipt SHA-256 is
`1D5B0B773897A8975369D605272D2638D2C00E38F263F4EA8EC8074C7F9CB88B`.
No CK3 process or promotional-video asset was created by this compatibility
sync; G2 source-specific readiness remains unchanged.

### R442 non-modal timeline recovery refresh

Root commit `e073216a916988707fa2010c47c9885e83a1df75` repairs a target-side
harness RED observed after the resume path had loaded and advanced the game.
When no verified modal option exists, the adapter now reapplies its existing
timeline controls and continues only after OCR proves a strictly later game
day. The event-option path and all native source, MCP, action and readiness
boundaries are unchanged. The current adapter and manifest SHA-256 values are
`94E4C57404E8ECE7445B620592BC71D3493876B5E46A6FB2AE4CE9ABF12C736C`
and `90D1B3080165B3E64D40A8C999358599FDD5FA90C94639E062A2F9F72CE2C28B`.

This remains `NO-PRODUCTION-CODE-CHANGE` in open_kaishek: Operator MCP 1.1
forwards the target-frozen command and does not interpret the recovery branch.
No Java API/schema/profile or focused Java test changed. The target adapter
tests pass `23/23` in normal and optimized Python, and the exact R442 successor
passed no-launch admission with receipt SHA-256
`C10FDE71DAB6912562B47581406E1636FC2EEC2F295D21FDD1E5475AAB545391`.
No CK3 or promotional-video action is part of this compatibility sync.

### R444 canonical played-character binding refresh

Root commit `6dec09cc905b0ce07636097b6f0c1258b8e90805` fixes a target-side
bridge-readiness harness RED. The exact bridge had connected and published a
valid canonical snapshot, but the G2 adapter still required the obsolete
convenience field `played_character_id`; it now normalizes either that legacy
shape or `played_character.character_id`. No bridge DLL, wire protocol, MCP
schema, command, capability, game file, or launch profile changed. The current
target adapter and manifest SHA-256 values are
`9F88417E62F54E4D255C5495513E05F2FC0E568B8082ED1EE73AFAA46DF608A9`
and `AF5533CA4D086B46F1DCDEF0DC920DFE30208C88D0B10D13C5F859112D22165B`.

This is `NO-PRODUCTION-CODE-CHANGE` for open_kaishek. Operator MCP 1.1 forwards
the target-owned command and receipt without interpreting either played
character representation. The existing compatibility tests remain applicable;
no Java test is repeated. Target focused tests pass `23/23` in normal and
optimized Python, and the refreshed real-save no-launch admission SHA-256 is
`979DCD959E125310DC9A32CF9F6FF14C30CB33F0AC821C983E222B7DC1BA0489`.
R444 cleanup is GREEN with no CK3 process remaining, and no promotional-video
asset was touched.
### G2 target-option arm guard refresh

Root commit `4efd2ab80e26d42bf083d3f7a8237d41ade75d2e` changes only the
target-side pre-bridge UI choreography. Before generic modal recovery, the G2
adapter now identifies the exact `.1071.a` option by full text or by the three
localized option-region tokens `扶上 / 君士坦丁堡 / 皇位`, then preserves the
existing `atomic_arm` before click order. No native observer, DLL, MCP command,
wire schema, capability, game file, dependency, or Operator MCP control changed.
The target adapter/manifest SHA-256 values are
`6CB7E3B97524C8EB2AFB56329BE97052A0E63752A96BF1887EDD51E5879079AB`
and `C271267FBA0F4FA666EA121C0E7DE2B4D288011C70A893483B138F3098010648`.

This remains `NO-PRODUCTION-CODE-CHANGE` in open_kaishek. The target's focused
normal and optimized Python tests each pass `24/24`; its hash-bound R440
pre-target save admission is GREEN with receipt SHA-256
`5E6A0A00E3E77D63802D2783C273394D337BDA5F241A647AD25800A42263AACD`.
Operator MCP 1.1 continues to forward the target command without parsing UI
recognition details, so no Java test is repeated. No CK3 round or promotional
asset was created by this compatibility sync.

### R445 letter recovery and vanilla-event asset refresh

Root commit `3b632641ab839a6b9d569208e762fc39ad9fa052` adds a target-side
visual handler for the real `chancellor_task.1004` foreign-affairs letter. It
requires the three rendered tokens `掌玺大臣 / 外交行为 / 可怕的误会` and locates
the option inside the existing event-option region before clicking. This UI
choreography does not change an MCP command, schema, native bridge, game file,
or open_kaishek runtime contract.

Root commit `dacc1d759d349ff142f167e265f09077c51da27d` then publishes the
event as an additive shared vanilla-event asset. The public v1 envelopes stay
unchanged; the catalog and exact-build source index advance from 182 to 183
events, and the source-index dataset identity becomes
`265EBCE989627D68C69DDEF178A7BC0EBE1DE846721E42584D2B8271D14E9CFD`.
The contract is campaign-neutral and records authored option 1/native option
0. Its analysis preserves the actual negative-opinion effect; the R445
observation explicitly says native event-window context was unavailable and
does not invent dynamic scope identities.

open_kaishek updates only its read-only capability pin and documentation. It
adds no parser, IR, runtime, CLI, service, CK3 process access, or mutation
behavior. The focused Java profile test is the only required executable check;
it passes `4/4` with zero failures, errors, or skips. The first Maven invocation
was rejected before compilation because PowerShell split the unquoted
`-Dsurefire.failIfNoSpecifiedTests=false` argument; the quoted retry is GREEN.
No CK3 or promotional asset is involved.

### R448 private-capture dependency refresh

Root commit `8e2a8917143e261ccac589436b44baafdb1b9d14` corrects a
target-owned diagnostic contract after R447 proved that the native
`spawn_army` breakpoint was reached but the private capture executable rejected
the hit before appending evidence. The executable now records the actual
`evaluated_name` on every captured row; its final six-row validator still
requires the reviewed target identity and therefore remains fail-closed. The
external executable advances from SHA-256
`B8328D5C0B52AF667BB71D2BBE660C803BF46EC0A7549A514083B7DBB8BA5A72`
to `B05E0B6D3CA8DBEC41C8C5107AB8F9AACD4E99981E442AC1DBF3077868241007`,
and the target source-specific contract SHA-256 becomes
`7DFA946A90F7C3DD5DF8305CFA07A39BD635BE06C5610FA1BC02FAD16D0B1654`.

This is `NO-PRODUCTION-CODE-CHANGE` for open_kaishek. It changes no public MCP
envelope, Java API, profile schema, command, capability, bridge DLL, game file,
or launch order. The target's focused matrix passes `53/53` in normal and
optimized Python, the rebuilt capture executable passes its self-test, and the
R448 no-launch admission receipt is GREEN with SHA-256
`5FF8771F9CCCA853FA4C4FE8FA7B7BE0787C3EAB5EEF25B18FD8FD5A9601E3EB`.
R448 remains a bounded diagnostic run; no CK3 process or promotional-video
asset was created by this compatibility sync.

### R448 localized evaluated-name contract correction

R448 captured all six action-bound `spawn_army` executions from the accepted
`bookmark.1071.a` option. Every row carried the same exact WarID, a unique
loaded node and CArmy generation, measured 500 creation-time soldiers, and the
locale-dependent evaluated display name `诺曼路匪`. The previous private
validator compared that runtime display text with the authored localization
key `norman_highwaymen`, causing the otherwise complete capture to remain RED.
The frozen RED capture SHA-256 is
`B819D4C94B3BD25EC1B505368801FE5EC2BD09CBCEFB934543B687CB1A984A1D`;
cleanup is GREEN and no CK3 process remains.

Root commit `0235a50241f3dd6c37d375ff00bf56d76620d3a9` makes the private
contract locale-neutral: the evaluated display name must be nonempty and equal
across all six rows, while loaded-node, WarID, army generation, measured
soldiers and regiment mappings remain hard identity checks. The rebuilt private
capture executable SHA-256 is
`020F051DDE034CBBC67C5A308F8E035FFA3E224844AC413261AA257466B0F185`;
the source contract SHA-256 is
`A96F54BC9556B23473C4BC9120009B6C6BC411CC0035F68E6C0CE8073F6C3586`.
Its self-test and the target's focused normal/optimized Python matrices are
GREEN (`53/53` each).

This private data-contract revision remains `NO-PRODUCTION-CODE-CHANGE` for
open_kaishek. Operator MCP 1.1 forwards the target-owned command and does not
parse the capture rows, so no Java API, public schema, capability profile,
bridge DLL, game file, or launch order changes. It does not promote G2
readiness and does not affect the T0 P1 or promotional-video gates.

### R449 dynamic source-WarID lifecycle binding

R448 also proved that a newly created `raiktor_claim_cb` WarID is a dynamic
full-generation identity: the captured value was `33554473`, while the old
source lifecycle command still froze an unrelated historical value
`50331699`. The previously unexecuted live continuation would therefore have
failed after a GREEN observer handoff. Root commit
`5743466d1074af68ff12930bbe299becc12fef8c` now derives the lifecycle WarID
from the already validated source capture. An optional CLI value is retained
only as an equality assertion after capture; it is no longer required before a
natural event creates the WarID.

The source WarID remains subject to the existing full-generation, six-row,
persistent-regiment, active-war, same-PID, same-frame, and postwar identity
checks. The change does not weaken a query or action gate. Target focused tests
pass `53/53` in normal and optimized Python. The lifecycle, outer-owner, and
live-adapter manifest SHA-256 values become
`A026E526B0495087B3AF575BE7547CD54D1374AE0BBDAA72CBE0D278D116716C`,
`0E941BDB65EE2700DC2952F3431A4FDF1818C8E3D0B13A91405FCB593533EF87`,
and `C67C711401A1DA4BC8509FED9EE8AC0A4EA8D24EE46D762440EC0E88FE46F1F2`.

This remains `NO-PRODUCTION-CODE-CHANGE` for open_kaishek. Operator MCP does
not interpret the target-owned CLI assertion or private source WarID handoff;
no public envelope, Java API, profile schema, bridge DLL, game file, or load
order changed. No CK3 process or promotional-video asset is involved in this
compatibility sync.

### R449 bounded debugger-detach retry dependency

R449 passed the corrected six-row localized-name and dynamic-WarID checks, but
the private observer returned `debugger-detach-failed` immediately after the
final debug event was continued. The same detach seam succeeded in R448, so
the evidence identifies an intermittent release race. The frozen R449 capture
contains six complete rows and has SHA-256
`E382E079DC7A6124A9961E174A3E802F8E67B0C95403325FB16485FD51A5E178`;
the outer cleanup remained GREEN and no CK3 process survives.

Root commit `454f515d8ef55ddf6e3cd5eccfa0a8cfb26e7630` adds a bounded
detach retry: at most 20 calls separated by 25 ms, for a maximum sleep budget
of 475 ms. The private artifact now records the attempt count and final Win32
error. All existing breakpoint restoration, detach-before-bridge, fail-closed,
and cleanup gates remain. The replacement private executable SHA-256 is
`EEE39F858E941E1500DA13FB11906814FA4D70EE42DED894CFDEB03ACEF709B8`;
the source contract and live-adapter manifest SHA-256 values are
`346D3DE9105B44749D9BA1E11F820A7C538063980CE53C24A9572B486F9FB247`
and `657E538D8754C6094F6FC3B3055BFE31D49C0BC7F27DAF6A7D392E1155A2A247`.
The executable self-test and target focused normal/optimized matrices are
GREEN (`54/54` each).

This remains a private dependency change. open_kaishek's public MCP envelope,
Java API, profile schema, capability set, bridge DLL, game files, and load order
do not change, so no Java test is repeated. No CK3 process or promotional-video
asset is involved in this compatibility sync.

### R451 read-only default-truce-reader diagnostic dependency

R450 proved an input-current capability RED: both same-frame public term
queries for WarID `33554473` returned the other domains while leaving
`evaluated_days_observable=false`. No checkpoint, termination, postwar phase,
or source-save mutation occurred. Root commit
`ad3404d268c6a2a86d80ee2cb2b73ae5c36213a1` now provides an explicit
read-only pre-termination probe plus default-OFF reader telemetry. Probe
completion and terms readiness remain separate, and the normal action gate is
unchanged.

The root live-adapter manifest is SHA-256
`7C66EAAA51AEAD8E020CB8E3D41D3F8794DB4BFF8742C6E182ADA6B7E95733E8`;
the hash-bound no-launch receipt is
`E10DA2DBF2DDD3F7B427972223F4162BA886875F1445F47C4320BD375FC6B56D`.
The diagnostic DLL/injector hashes are
`AB1BF87A1C4C20BC488F857BA0454E070225F0027259DDC6282530D92AAB1705`
and `B5353527B56B4C99A7AF11A09C59A0850C481A45ED7CDA4C519AF956D97B39EC`.
Focused root tests pass `57/57` in normal and optimized Python; diagnostic-ON
and default-OFF bridge targets compile.

This is a private runner/build dependency sync. open_kaishek's public MCP
envelope, Java API, profile schema, capability IDs, bridge DLL, game files, and
load order do not change, so no Java test is repeated. No CK3 process or
promotional-video asset is involved in this compatibility sync.

### R451 pause-OCR occlusion dependency

R451 captured six valid natural-source rows for WarID `33554473` and detached
the debugger, then a top-center in-game notification covered the pause OCR
region. The run stopped before bridge attach with zero diagnostic rows,
checkpoint, mutation, or postwar activity; cleanup was GREEN. Root commit
`d486e7066ff65e1d3c8abeeb337346bf3f0e2f48` accepts only a strict
three-second HUD-date freeze after the pause click when that OCR check times
out. Missing or advancing dates remain RED. The R452 no-launch receipt is
SHA-256
`F89EE9D57A34038B822B74E2B75DD1F2BE9CCF1ED4798B7E636C78C033F02CCF`.

This is a private runner dependency. No open_kaishek parser, runtime, public
MCP envelope, Java profile, bridge DLL, game file, or load order changes, so
no Java test is repeated and no promotional-video asset is touched.

### R452 pre-resume Prepare dependency

R452 reached the bounded read-only terms probe on unique PID `132200`, but the
default-reader diagnostic stopped at `collector-vtable-verified` with
`callback_count=0` and `last_failure=invalid_request`. Static call-chain review
showed that the source-specific adapter's ordinary launch followed by late
`--pipe` injection never called `XarCk3BridgePrepareStartup`, so the
preview-entry observer was absent before its arm request.

Root commit `8f1a522c0163796056ae8bc1281839b4fed8edf1` changes the private adapter
composition to use the shared suspended-process runtime: verify the unique
exact PID, inject without `--pipe` to execute Prepare, then resume; the existing
post-source `--pipe` call only starts the same-PID MCP worker. The manifest now
hash-binds `ck3_autonomous_player/src/xar_autoplayer/runtime.py` and reports
`suspended_launch_before_observer=true` plus
`native_bridge_prepared_before_resume=true`. The R453 no-launch admission is
SHA-256 `A4C58C0E23CF658E5449887DBD300CDD3B5F0E2AA33943A00FA5799071143EEA`.

This is a private runner/manifest dependency update. open_kaishek's public MCP
envelope, Java API, capability IDs, profile schema, bridge DLL, game files, and
load order remain unchanged. No Java suite or CK3 run is required for this
companion sync; live readiness remains false until the bounded R453 probe.
### R453 suspended-process identity dependency

R453 proved that the unique newly suspended CK3 PID can appear in the global
inventory before CIM/Toolhelp publishes `ExecutablePath`. Root commit
`69f0fbf5d7e7d723d12726064c1c23a9f1b4563e` keeps the unique-PID requirement
and, only when the inventory path is blank, reads the exact image path from the
shared runtime's retained Win32 process handle before applying the existing
path/hash checks. The failed target never resumed or reached Prepare.

The R454 no-launch receipt is
`5DBB22DE0BD7674242CC3B36EC9C02CD3B15902BBCA652008696DD48AADE6F46`.
This private orchestration behavior does not change open_kaishek's public MCP,
Java, profile, or command contracts; readiness remains pending live evidence.
### R454 prepared startup receipt dependency

R454 completed suspended identity validation, pre-resume Prepare, and primary
thread resume on PID `77472`; the private outer owner then rejected the new
`suspended-prepared-normal-event` receipt because its validator retained the
old `normal-event` literal. Root commit
`55702c167ed31c940a717484e0d939560397f4c3` aligns the owner validator and
fixture with the truthful prepared startup mode while continuing to reject
unrelated modes. Combined focused tests pass `40/40` in normal and optimized
Python. R455 admission is
`B43F5BD9D9F16F75A3F55F51781E46A23E7A3C1A17B88DD9732058CC78150BB0`.

This is a private orchestration receipt change. open_kaishek's public MCP,
Java, profile, command, and readiness contracts do not change.
### R455 current-war truce result

R455 completed the prepared source-first read-only path on PID `196216`. Both
public terms queries for WarID `33554473` returned
`evaluated_days_observable=true`, `evaluated_days=1825`, and
`terms_ready=true`. Both private diagnostic rows completed one callback and one
valid evaluation context, returned without failure, and destroyed the context.
Root evidence commit is `4bed362d87eda2028e745db6f03a8c44f7df401f`;
report and diagnostic SHA-256 values are
`E4C3DCFCA6B8DF1CF90ED376E638BD73241A227744D06B46FA212083BFB3DC69` and
`28CA8EB52E53A83B42CD2A0E29AF28A174A620E8439475DCB6DA1A6B81F0E774`.

The read-only run created no checkpoint, submitted no command, and did not
start postwar polling. This updates the private capability evidence only:
source-specific loss, comparison, action, and GEN-034 remain false. No public
open_kaishek MCP/Java/profile contract changes.

### R456 checkpoint successor binding

R456 proved that a successful `save-checkpoint` publishes a successor snapshot
while the paused gameplay identity remains stable. Root commit
`eced42e8cf09cc061dd900e0f6f8c87678e8f817` keeps the exact pre-save source
frame as the command authorization anchor and adds a hash-bound
`post_checkpoint_frame`. The latter requires a different nonempty snapshot ID
and strictly greater public/native revisions while PID, connection generation,
date, episode, played character, paused state, and active WarID remain equal.

The R456 checkpoint is `69,302,764` bytes with SHA-256
`0759E25C2612E127A63C676CC73A9DB95B336319DAC16CF0C632CDE7ED817530`.
Surrender was not submitted; report SHA-256 is
`E28E820CE713897A92E51416D6F655F399B1C0B421ABBC8FBEF0A19ACD095668`.
Focused root lifecycle/owner/adapter tests pass `48/48` in normal and optimized
Python. R457 no-launch admission is
`3AAE6BA08E360E0380AC4B11B4F11CB4BBC68EF0D2E3F65EBA8DC9DFB17FD701`.

This is an additive private lifecycle receipt change. open_kaishek's public
MCP envelope, Java API, capability IDs, profile schema, bridge DLL, game files,
and load order do not change. No Java suite or CK3 run is required for this
companion sync. Source-loss, comparison, action, and GEN-034 readiness remain
false pending one bounded R457 normal lifecycle.

### R457 normal lifecycle bridge-profile correction

R457 passed the checkpoint successor gate but stopped before surrender because
the normal manifest still selected the completed R451 read-only diagnostic DLL.
That build has both private postwar candidates OFF. Root commit
`35cb830f45434dc713d4e580692e0c6ab5f36796` restores the frozen normal DLL and
injector `4D839524...238B` / `43983E28...E08D`, whose build profile enables
actual-truce-expiry and war-bound-loss-cleanup.

Focused root lifecycle/owner/adapter tests pass `48/48` in normal and optimized
Python. R458 no-launch admission is `A270F42E...D20F`. This changes the private
runtime dependency selected for the normal evidence run; open_kaishek's public
MCP, Java, profile, capability IDs, and game files remain unchanged. No CK3 or
Java test is needed for this companion documentation sync.


### R458 checkpoint-to-action revision handoff

R458 used the intended postwar bridge and completed source capture, two GREEN terms reads, and the durable checkpoint `079F3573...9FB1`, then stopped before surrender. The successful save published the already validated successor frame, but the private lifecycle continuation passed the pre-save public revision to the optimistic action gate. Command history ends at `save-checkpoint`, so this is a harness contract RED and adds no product RED.

Root commit `35e5f9ac2d0964def1fd8ecba0e41e9909213e2a` passes `post_checkpoint_frame.revision` into the immediate surrender continuation and requires it to be a strict integer successor of the pre-save revision. The standalone continuation keeps its existing behavior. Focused root tests pass `59/59` in normal and optimized Python; R459 admission is `45B981E2...26F1`.

This changes an internal Python lifecycle argument only. open_kaishek's public MCP envelope, Java API, profile schema, capability IDs, bridge DLL, game files, startup configuration, and load order remain unchanged. No Java suite or CK3 run is required for this companion sync. Source-loss, comparison, action, and GEN-034 readiness remain false pending one bounded R459 lifecycle.


### R459 source-specific surrender outcome consumed

Root commit `2bc9f01f0eebda5086e250400b9591fce95678ae` records the first qualified same-PID source?terms?checkpoint?single-surrender?postwar lifecycle. R459 measured the six source executions at 3000 soldiers before termination, proved the exact generations destroyed to zero at the surrender boundary, and read persisted truce expiry `53227656` for `evaluated_days=1825`. Root report and offline policy-intake hashes are `87ADB7E1...4B34` and `43B0A053...4FE1`.

The offline consumer now validates the producer's actual `terms_ready=true` and preflight `live_executed=false` fields and includes the checkpoint successor in its fixture. This is an internal Python report-consumption alignment. open_kaishek's public MCP, Java, profile, capability IDs, bridge DLL, game files, startup configuration, and load order remain unchanged; no Java suite or CK3 rerun is required here.

Compatibility readiness advances only for the private observed-surrender input: `source_specific_loss_ready=true` and `comparison_input_ready=true`. Campaign dominance, owner budget, and same-frame white-peace comparison remain unavailable, so three-way comparison, decision/action, automatic surrender, and `GEN-034` remain false.

### Terminal-stage activation game-day bound enforcement

Root commit `980dc00ee1fcf24ceb94a8655cf88fb47ea507e0` makes the
target-owned terminal-stage operator honor the existing
`source_route.max_advance_days` activation field. The action cell persists the
configured value and its absolute date deadline; a retained-session retry must
keep the same value. Missing fields retain the previous global default. The
action/operator source SHA-256 values are
`1049E1A8D79984CCC682293630F8AA51B174A4C35F0102CBE71DF7E642732E55`
and `DF958ABCAC6FEF1E8EEDDBF4326802DADEED7AE8BCA7D3153194E5EFC26D775B`.

This remains `NO-PRODUCTION-CODE-CHANGE` for open_kaishek. Operator MCP 1.1
forwards the target-advertised control and does not interpret the target-owned
activation payload, so its public Java API, profile schema, capability IDs,
endpoint, and machine-independent adapter behavior are unchanged. Focused root
action/operator tests pass `20/20` in normal and optimized Python; the existing
Operator MCP adapter result remains applicable and is not repeated. No CK3
process or promotional-video asset was started or changed by this sync.


### R463 `health.2201` non-epidemic physician contract variant

Root commit `d3fcf7cb0cee450f835d62ece615e3bda6964b9e` extends the
shared CK3 1.19.0.6 vanilla-event knowledge record for `health.2201`. R463
observed the already reviewed four-option physician projection with five exact
saved scopes and no `epidemic`; the previous record only represented that
projection when an upstream epidemic scope was also present. The new variant
keeps authored option 1 / native index 0, root/third-party bindings, scope
types, and exact option indices unchanged. Focused normal and optimized Python
tests each pass `1/1`; the selection-before RED remains frozen at SHA-256
`555AA864F9BB9FC982E8C1E174CF095AF651AAED687F3329765A0D8606E98D8D`.

This changes only data returned inside the existing
`xar.ck3.vanilla-event-knowledge` v1 contract for one event. The MCP envelope,
tool name, schema version, Java adapter, target profile, capability IDs, bridge
DLL, game files, startup configuration, and load order do not change. Existing
consumers already treat contract payloads as detached JSON, so no Java code or
test change is required. R463 remains paused for the target-owned same-process
Python hot recovery; this companion sync does not launch or control CK3.

### R463 `tgp_japan_yearly_events.1030` content compatibility

Root commit `738650b0113b6a3a61933d0a23501ce503ebe755` adds the
CK3 1.19.0.6 `tgp_japan_yearly_events.1030` record after R463 paused on its
previously unknown event window. The campaign-neutral contract binds the
played character, zero saved scopes, rendered native indices `0/1/2/3`, a
five-option source snapshot, and authored option 1 / native index 0. The
corresponding analysis freezes the exact event definition and yearly caller
files; the portable bundle includes the immutable pre-selection RED at SHA-256
`07BC6B2AA7A4AD2AC5AE4422CE9B129118C1940123DD187E2A3BC8AE2AE5A4C6`.

The catalog and source index advance from 183 to 184 event keys. The source
index now contains 522 lexical caller candidates. The portable bundle contains
280 evidence entries and 1,086 references and passes its offline self-check.
Focused contract tests pass `1/1` in normal and optimized Python.

This is an additive content update inside the existing read-only v1 envelopes.
MCP tool names, schemas, request fields, response fields, Java capability
profiles, DLL/game files, startup configuration, load order, and machine/path
independence are unchanged. No Java code or repeated adapter test is required.
This sync neither launches nor controls CK3; current round R463 remains owned
by the root Operator job for same-process Python hot recovery.

### R463 post-checkpoint process exit

Root closure commit `66b63f42a26a7ab16e6b4fa3e7394c7438e6079e` records that
the R463 CK3 process disappeared after the `.1030` RED checkpoint was saved and
before the planned same-process retry began. No CK3 control or gameplay input
was sent by the root operator before the disappearance, and no crash event,
dump, or exception artifact was found, so the termination source remains
unproven. The old Operator job then completed canonical and managed cleanup as
GREEN; CK3 and old Operator inventories are now empty.

The content contract added at root commit `738650b0113b6a3a61933d0a23501ce503ebe755`
is unchanged. The next root activation will consume the frozen partial
checkpoint with only the original horizon's remaining 10 game days. This
operational correction changes no public MCP envelope, Java API, profile
schema, capability ID, bridge DLL, game file, startup configuration, load
order, or machine-independent lookup behavior. No Java or CK3 rerun is needed
for this documentation-only compatibility correction.

### R465 event closure and bounded source exhaustion

Root commit `53e229e1d7c5227e1b7d8f1f06ab3d7f456271f8` adds the R465
post-selection observation for `tgp_japan_yearly_events.1030`. The native
postcondition proves authored option 1 / native index 0 advanced event instance
`343`; the event contract is therefore a production-live primitive. The same
run exhausted its fixed Stage 10 source window without seeing `zg361cl.390`,
so no source receipt or P1 gate was claimed.

The portable bundle now contains 281 evidence entries and 1,087 references,
including 89 unique observation artifacts and 105 observation references. Its
manifest SHA-256 is
`B9910108273AB057E4FC246E89FCE303D65C2986C42B8CF33ACC016CEC694D55`.
This is an additive content update inside the existing read-only v1 contracts;
public MCP/Java/profile schemas, capability IDs, DLL/game files, startup
configuration, load order, and machine-independent access remain unchanged.

### Promotion-source deadline stop behavior

Root commit `c39b51dbb843c017175c2d38a1a11e964d8201c9` fixes the concrete
R465 observation-window overshoot. The target-owned Python runner keeps speed 5
for the main route, slows to speed 1 for the final three game days, and refuses
another resume after checking the declared deadline frame. Focused normal and
optimized tests pass `2/2`; no separate CK3 run was added for this small fix.

This changes no Operator MCP control name or payload, public event-knowledge
schema, Java API, profile format, capability ID, bridge DLL, game file, startup
configuration, or load order. open_kaishek needs no source or test change; the
next required root source route will provide the live verification.

### Active-war strategic-power MCP scope

Root commit `81f76e04704305df214e34961a32b768ce14a485` extends the existing one-target strategic-power query from declaration targets to the union of declaration targets and current active-war primary opponents. The response adds an explicit `target_scopes` source classification; the native payload, DLL, exact-build binding and read-only behavior are unchanged.

This is a public input-scope and response-field change, so open_kaishek adds `ActiveWarStrategicPowerCapabilityProfile` and a focused compatibility test. The profile is native-certified but remains runtime-uncertified for the new active-war scope until the root provider performs its one bounded R459 checkpoint query. No CK3 process or promotional-video asset is involved in this compatibility sync. Full field, invariant and hash pins are in [`ck3-active-war-strategic-power-capability.md`](ck3-active-war-strategic-power-capability.md).

### R471 active-war strategic-power runtime certification

Root commit `050c94fbd2ba9ecd41f4419e1dc936bd7c083774` records two
identical official MCP results from the corrected DLL on one unchanged paused
R459 checkpoint frame. The query now has exact-build native/runtime
certification for the active-war primary-opponent scope. The original report
RED is retained and explained as a runner-only field-name error; canonical
`native_command_history` proves exactly two successful reads and no mutation or
time advance, and the offline reclassification is GREEN.

The open_kaishek profile advances to v3 and pins the root evidence commit,
report/reclassification hashes, corrected runner, test, native sources, and
DLL. This certification stops at the observation primitive: campaign
dominance, owner budget, white-peace comparison, exit recommendation, action
readiness, and GEN-034 remain uncertified/unresolved. This sync does not start
or control CK3 and does not touch promotional-video assets.

### R479→R480 process-local restore generation correction

Root commit `b9b24ce48243eb3618ffa9574d1e42e49e907f57` corrects the
managed cold-restore contract after a real lifecycle exposed its invalid
cross-process assumption. The supervisor replaced CK3 PID `86544` with
`77320`, loaded the byte-bound checkpoint at `date_raw=53366616`, restored
played character `32904`, and received a stable paused `map_ready` frame. Both
DLL processes correctly reported their first process-local
`connection_generation` as `1`; the old Python wait incorrectly required the
second value to be greater than the first.

Cold restore now proves the supervisor ACK's distinct PID pair and same pipe,
then binds each positive process-local generation to its own PID and stable
frame. A retry after an ACK-side timeout can resume without a third CK3 only
when the replacement driver's command history, lifecycle outbox, checkpoint
bytes, date, and played character all agree. Focused root tests pass terminal
cold restore `14/14` and the completion gate `7/7` in normal and optimized
Python, plus the native driver and next-episode regression pair `2/2` in both
modes.

This changes target-owned lifecycle validation semantics, so it is recorded
here for companion compatibility. It does not change an Operator MCP tool,
request or response field, schema/version, Java API/profile, bridge DLL, game
file, startup configuration, load order, or machine-independent path behavior.
open_kaishek therefore requires no source-code or Java-test change. T0 P1 and
its final promotional-video lock remain owned by the root project.

Root follow-up commit `d0f471c5b7c8bacf2a81c9e01d15a86423e173ea`
applies the same process-local rule to the target-owned managed-cleanup
consumer. The original cleanup RED is retained byte-for-byte at SHA-256
`3CCF3EBCA1647B6CF28824C2AD3FF7B0B51FA7EA3BBB749147DAB86790D268BF`;
an offline replay of that frozen session report produces GREEN cleanup
`2C578FF4F8BB4D23DF10C1D01C4D0CEE80B3B31D0E48C58ACDFCF9BBD21A1F56`
for PID lineage `86544 -> 77320` and process-local generation lineage `1 -> 1`.
The target's cleanup-related normal and optimized Python tests pass `21/21`.
This follow-up changes only target-owned acceptance evidence semantics; the
public MCP contract and every open_kaishek runtime surface listed above remain
unchanged.

### Stage 10 player-publication reachability correction

Root commit `58e8cc9e616fe0621b967b40871c36580b2bf909` replaces the
obsolete Stage 10 activation topology. The target no longer treats an AI
manager selected from an owner-side `.390` frame as the formal source. A real
player manager now schedules one idempotent callback after publishing B1;
`zg361mg.90` re-roots only the case owner on that player's direct superior and
opens F/AK on the player subject. The owner-local evaluation cycle is the
published subject review serial plus one. It preserves strict lag without
creating an AI review serial. Stage 10 enumeration and the common opener both
reject AI subjects, including old saves that retain historical serials.

The Operator MCP 1.1 surface remains compatible: target-owned profiles still
advertise exactly `status`, `run-stage10`, and `cleanup`, and open_kaishek still
forwards only those identifiers. The source receipt and job implementation are
target-owned data, so no Java API, adapter, profile schema, MCP request/result,
endpoint, credential, path, DLL, launch configuration, or load order changes.
The previous `.390` plus AI-selector activation description is superseded for
future target profiles; it remains historical evidence for the rejected R467
route. Runtime certification is pending one bounded R481 `.120` observation.

Frozen root provider hashes are:

| root artifact | SHA-256 |
| --- | --- |
| manager dispatch effects | `51BF1761AB581F491F0D9A52852429B354136211891CB6838163C7D76A558DD3` |
| manager runtime events | `4B82F54D4E15391C1DF8154B0F7AA1A5F5C7235F035E978EC9F2F2D46C5E7444` |
| Central publication hook effects | `964F596685A4F9A541059E54056207783B74DF126973BD4742399D1DF2665FB0` |
| F case open wrapper | `8E049F2A97A8BF4C66A9DF0A9E7BDFA144D890918F7DC167F036F3A9DC25A307` |
| AK case open wrapper | `4848CBFB7D7F7C7E325BBEEFD6FB167E05B419E256EA4BD35FB21A68FD047828` |
| root reachability note | `DE7467F791C130DB1F1A5DA767F5AA72A166EB20D1E6D9F05614E011F0DE7A8D` |

Root focused validation is case kernel `12/12`, manager `53/53`, and Central
`45/45` in both normal and optimized Python, plus current generators and local
static validation. No CK3 instance or promotional-video asset was touched by
this compatibility sync.

### Stage 10 target receipt v2 and bounded action alignment

Root commit `c1d43ba385d50bbd3ac5dc47cdbdadc4f1d15873` completes the
target-owned runner migration required by the player-publication correction.
The Stage 10 job now consumes `zg361_stage10_player_publication_source_v2`.
Its prelaunch receipt binds a SAV0101 CK3 1.19.0.6 checkpoint, product-tree
hash, player-manager CharacterID, distinct immediate liege, at least one
direct landed vassal, player title tier, and celestial government. Offline
topology is only an input-admission hint: after launch, the existing
campaign-root query must authoritatively reconfirm the played character,
immediate liege, non-independent status, duke-or-higher tier, celestial flag,
and `zg361_on` before any product action.

The action then uses the existing review-now transport and promotion-source
navigator, with one absolute 30-game-day deadline, to pause on the real
`zg361mg.120`. It does not consume `.390`, run the AI-manager selector, or
switch the played character. The existing manager-governance query verifies
the same owner/player F case at `state=5 / active=false` before the event is
acknowledged. Target-focused action and operator tests pass `5/5` and `4/4`
in normal and optimized Python.

This remains compatible with Operator MCP 1.1. The advertised target controls
are still exactly `status`, `run-stage10`, and `cleanup`; the adapter treats
receipt fields as target-owned activation data and does not parse them. No
Java API, schema, transport, endpoint, credential, machine path, DLL, launch
configuration, or load order changed. The prior `.390`/selector receipt is
retained only as historical evidence and must not be used for new Stage 10
profiles. Runtime certification remains pending one bounded live `.120`.

### Stage 10 target receipt v3 and multiplayer-source rejection

Root commit `01c2dd6ef00465fa11755183ffaf3315eb4f4bb7` preserves the
R481/R482 source-admission RED and closes the target-specific prelaunch gap.
The attempted `SAV0101` source contained `meta_number_of_players=5` and five
`played_character` records; exact-build CK3 reached the in-game map but did
not restore a played character or install the main-thread mailbox. The Stage
10 action never ran, so the result does not reclassify the player-publication
product fix.

The Stage 10 job now consumes
`zg361_stage10_player_publication_source_v3`. In addition to the v2 hash,
build, product-tree, and player-manager topology bindings, v3 requires one
offline player record and a hash-verified
`zg361_stage10_player_source_capture_v1` artifact. That artifact must bind the
same MCP-native checkpoint to a paused, map-ready, non-independent celestial
player manager and its direct superior. This target-specific receipt is
validated before CK3 launch.

The portable Operator MCP remains version `1.1.0`; target discovery, job
handoff, `status` / `run-stage10` / `cleanup`, identity checks, and file-record
transport are unchanged. No Java adapter, public schema, DLL, game file,
startup setting, or load order changed. Operators on other machines rebuild
the v3 file records with their own absolute paths and the same content hashes;
there is no dependency on R481/R482, account `xenoa`, or this machine.

The canonical and managed R482 cleanup artifacts are GREEN. T0 P1 remains
`8/9`, Stage 10 remains live-pending, and the final-video lock remains active.
The next launch is allowed only after a live-admitted single-player checkpoint
has produced the v3 source receipt.

### Reusable offline CK3 save-topology inspection

Root commit `7f756c6ac63096963b6e919453c720309474af18` replaces the
temporary R482 save scan with `ck3_save_player_topology_offline_v1`. The CLI
accepts caller-supplied save, Rakaly, target-character, and output paths; it
records file hashes, player metadata and records, then derives celestial
manager candidates from title, living landed character, and vassal-contract
tables. It contains no target account, machine path, round, or fixed character.

This is an offline prelaunch asset and does not alter the public Operator MCP
or claim live state. A consumer must still obtain exact-build campaign-root
evidence and an MCP-native checkpoint before creating a Stage 10 v3 receipt.
Focused normal/optimized parser tests pass `2/2`. The root evidence reports
deterministically reject the five-player R482 source and select `29037 ->
32904` from the already admitted R159 single-player world for the next native
capture.

### Bounded Stage 10 player-source capture job

Root commit `c87f7040f6d28b18068f5b5cd910160147e36e23` adds the
target-owned `stage10-player-source-capture` job. It restores a previously
live-admitted single-player checkpoint, verifies the current campaign root,
uses the existing generic native character-rebind capability once, verifies
the target player manager and direct owner, and saves a new checkpoint through
the existing MCP native-save capability without advancing game time. The
result kind is `zg361_stage10_player_source_capture_v1`.

The job advertises exactly `status`, `capture-source`, and `cleanup`; it has no
retry control. Its activation hash-binds the reusable offline topology report,
the prior exact-build live qualification, and checkpoint provenance. The
portable Operator MCP 1.1 already forwards controls advertised by a target, so
no Java adapter, public MCP schema, transport, endpoint, credential, DLL, game
file, launch configuration, or load order changes. Other operators supply
their own absolute paths while preserving file hashes; no current round,
account, or machine path is part of the interface.

Focused root tests pass `3/3` in normal and optimized Python. This package did
not launch CK3 and does not qualify Stage 10 `.120`; T0 P1 remains `8/9` and
the final-video lock remains active until the later bounded live result.

### R483/R484 source-capture dispatch correction

Root commit `4c49425864954035154b69fb432241ec76bf1f1b` preserves the
first live source-capture attempt as a harness RED. R483 frontend warmup ended
before sole gameplay R484 restored the admitted player `32904`; the full
loader/native gate was GREEN. The target worker then inherited the AF5 base
validator and lost its source-specific fields before any rebind, save, or game
time advance.

The worker now explicitly calls the source-capture validator before entering
the shared managed lifecycle. Controls and result kinds are unchanged, so the
Operator MCP adapter needs no code change. Focused root tests pass `4/4` in
normal and optimized Python. Both rounds have GREEN cleanup and are
terminated; the next attempt must use incremented rounds and a freshly
hash-bound activation rather than retrying R484.

### R485/R486 player-source certification and topology IDs

Root commit `fec55f8a650a3b0fff41dd104fad79663afacb9c` records the
successful bounded source capture. R486 used the existing native rebind and
save capabilities to move the single player from `32904` to manager `29037`,
verify its direct owner `32904`, and save at unchanged game date. Live source
evidence is `EFCFE5DD...AE2CA`; checkpoint `C11AFCF4...21BFA` is one-player
and has unique played/current character `29037`.

`ck3_save_player_topology_offline_v1` manager candidates now include sorted
`direct_landed_vassal_character_ids` alongside the existing count. This is an
additive offline report field used by the target's v3 source receipt; it does
not change Operator MCP 1.1, the native bridge, or a live query schema.
Consumers that ignore unknown JSON fields remain compatible. R485/R486 have
GREEN cleanup and are terminated; Stage 10 `.120` and P1 remain pending.

### Stage 10 target receipt v4 and fixed-tail bound

Root commit `e58c2229a12f097e2bae2a093226027ad7e6b876` preserves the
R487/R488 30-day Stage 10 result as harness RED and replaces the unsupported
deadline assumption. The exact checkpoint is already at player-manager B1
D+299 with `zg361b1.102` scheduled one day later. Static product flow then
requires `.103 +30d`, common settlement, manager publication, and the five
F-ticket stages before `.120`; the target action therefore uses one absolute
45-game-day bound.

The target-owned source kind is now
`zg361_stage10_player_publication_source_v4`. It retains every v3 binding and
additionally requires the frozen R488 action evidence to prove initial
`review_requested=false`, B1 active, and Central/PP inactive; it also binds a
`ck3_scheduled_event_queue_offline_v1` report whose exact checkpoint, manager
root, `zg361b1.102`, and `days_from_current=1` agree with the source. The
receipt records the fixed-tail contract as D+299, `.102 +1d`, and maximum 45
days. All fields remain target-side activation data validated before launch.

The reusable scheduled-event inspector accepts caller-supplied save/Rakaly or
melted-input paths, event prefix, root CharacterID, and output path. Its report
binds source hashes, game/current/queued dates, event IDs, roots, and relative
days; it is prelaunch-only and cannot claim live state. Focused target tests
pass `10/10` in normal and optimized Python.

Operator MCP remains `1.1.0`: discovery, handoff, `status` /
`run-stage10` / `cleanup`, transport, and Java adapter are unchanged. No DLL,
game file, startup setting, load order, endpoint, credential, account,
machine path, or round identifier changed. Direct/managed R488 RED hashes are
`50BBD120...65E3` / `06E7859B...F8EA`; cleanup is GREEN and both rounds are
terminated. P1 remains `8/9`, and the final-video lock remains active until
one new bounded attempt supplies `.120`.

### Stage 10 target receipt v5 and calibration-tail bound

Root commit `85be4c71a7aa2d0ba04cd87c385c14e15143624f` supersedes the
45-day sufficiency claim after the sole R490 gameplay attempt. R490 passed the
loader, exact-build native, mount, and error gates, then preserved target RED
at its 45-game-day absolute deadline with B1 still active. Its live log proves
that the valid common-superior bank close and pending/reopen entry occurred;
there is no stale manager-calibration ticket, so this evidence does not
establish a new product defect.

The exact source uses `m142=1` and `m143=1`. The target now accounts for the
authored 31-day pending watchdog and 30-day post-seal reopen, followed by the
one-day player publication callback and five daily F tickets. The conservative
path is B1 D+299 through Stage 10 `.120` at D+403, or 104 days. The action uses
one 120-game-day absolute bound, leaving 16 days for scheduler granularity;
it still does not reopen or replay the full B1 cycle and exposes no retry.

The target-owned source kind is now
`zg361_stage10_player_publication_source_v5`. It retains the v4 bindings and
adds hash-bound R490 45-day RED evidence. Its fixed-tail object records the
common-bank/calibration cycle days, pending and reopen delays, publication and
F-ticket delays, the latest D+403 target, the 104-day required tail, and the
120-day action cap. Focused root tests pass `9/9` in normal and optimized
Python; no broad suite or CK3 rerun was used for the correction.

Operator MCP remains `1.1.0`. Discovery, handoff, controls, transport, Java
adapter, public live schema, native bridge, DLL, game files, launch settings,
load order, endpoints, credentials, and portability requirements are
unchanged. R489/R490 are terminated and cleanup is GREEN. P1 remains `8/9`,
and the final-video lock remains active pending one v5-bound attempt.

### Stage 10 B1 exact-roster repair and v5 invalidation

Root commit `11cf6499879741860269b6fb5b9cc8325bf05cab` supersedes the
assumption that only the Stage 10 observation bound remained unresolved.
The sole R492 gameplay attempt reached the full 120-day deadline after seven
season publications and ten final-callback compaction failures, while B1
remained active and Central/PP remained inactive. Frozen source inspection
found 29 live references in player-manager `29037`'s persistent subject list,
but only six belonged to the current `owner/cycle/case=29037/17/17` tuple;
the other 23 belonged to manager `29628` case `19/19`.

The product repair applies the same owner, subject, cycle, case, active, and
roster exact-tuple filter to both persistent B1 lists and fails closed when a
row cannot publish that tuple. This is an internal mod state-machine semantic
change. Operator MCP remains `1.1.0`; discovery, handoff, the three controls,
Java adapter, public live schema, native bridge, DLL, launch settings, load
order, endpoints, credentials, machine portability, and cleanup protocol are
unchanged.

`zg361_stage10_player_publication_source_v5` is now historical evidence only:
it binds the old product tree and cannot admit another launch. The next target
receipt must be v6 and bind the R492 product RED, exact-roster evidence, and
the repaired product tree. Focused B1 runtime tests pass `76/76` in both normal
and optimized Python; no additional CK3 run or broad suite was used for the
repair. R491/R492 are terminated, cleanup is GREEN, P1 remains `8/9`, and the
final-video lock remains active.
