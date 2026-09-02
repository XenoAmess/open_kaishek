# CK3 phase-two schema slice

Date: 2026-09-02 (Asia/Shanghai)

This note records one deliberately narrow profile increment for the CK3
1.19.0.6 phase-two offline preflight. It is a syntax/profile improvement, not
runtime certification or a claim that the full mod tree is ready.

## Selected opcode

`has_variable` is registered as a scalar `TRIGGER` with `THIS` scope,
zero named parameters, and `certified=false`. The phase-two source uses the
scalar form (`has_variable = <key>`) for presence gates around owner/cycle/case
state and fresh-scope reads. The exact-build static evidence is recorded in
the parent CK3 research ledger at
`docs/ck3-native-ai/combat-phase-events.md:1580-1605` (CK3 1.19.0.6, executable
SHA-256 `2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`):
the native evaluator checks variable-key presence and does not inspect value
truthiness or type. The checked-in vanilla tree also uses only the scalar form
for this trigger.

No `certifiedSemantics` entry was added. A fixture, ACK, or static opcode
descriptor must not be reported as live or differentially certified.

## Bounded before/after measurement

The same source roots and parser corpus hash were used for both measurements.
The pre-change count was obtained with the same current validator while
temporarily omitting only `has_variable` from the opcode map; this avoids
mixing older validator revisions into the comparison.

| root | parser files / bytes | corpus SHA-256 | validator before | validator after |
|---|---:|---|---:|---:|
| phase-two seed fixture (3 files) | 3 / 5,867 | `3faf52bfd98d67d4ee8d39327d563230b8e03c5fff4a6b958c4282a02460d9ca` | 92 | 62 |
| full phase-two mod root (75 files) | 75 / 23,831,185 | `540333b6d145626805da964ac5fbfbff65afcec547bf7a84b76ec265fa8b2b82` | 306,655 | 233,708 |

The seed slice contains 24 exact `has_variable` unknown-opcode reports; all are
removed. On the full root, 65,219 exact `has_variable` unknown-opcode
occurrences are removed. The 22 remaining reports whose names merely contain
that text are the distinct, unregistered `has_variable_list` operation; it is
deliberately not added without exact-build evidence.

The same narrow increment also makes the existing `ScriptSide` information
effective for registered triggers inside condition containers in effect and
on_action files: only child blocks named `limit`, `trigger`, `potential`,
`allow`, or `check` are exempted from the trigger/effect domain mismatch.
Direct effect-side predicates, script-value files, unregistered operations,
and all other domains remain RED. This side-aware rule is necessary to avoid
turning recognized trigger use into a broad false-positive `WRONG_DOMAIN`
surge; it does not certify runtime semantics.

The full-root breakdown is `UNKNOWN_OPCODE=298,755`, `WRONG_DOMAIN=7,893`,
`UNKNOWN_DIRECTORY=7` before the slice, versus
`UNKNOWN_OPCODE=233,558`, `WRONG_DOMAIN=143`, `UNKNOWN_DIRECTORY=7` after it.
The remaining 143 domain diagnostics are intentional script-value/direct-side
boundaries, not silently suppressed errors. The overall validator remains RED:
many other native and project-defined operations are intentionally outside this
bounded profile.

The post-change CLI artifact was produced offline with
`synthetic-361-014`; parser and fixture IR/runtime remain green, while the
root validator remains red as expected. The shaded/ordinary CLI JARs are
byte-identical for this build (SHA-256
`CBCD5F868F5C46AA7B5A2C70E11705B978F95DE3437E0809D20A4139F62DD0E4`).

## Regression boundary

`Ck3Profile11906Test` pins the descriptor shape and keeps it uncertified;
`ValidatorTest` proves the observed scalar trigger no longer produces an
`UNKNOWN_OPCODE`. Full semantic execution and any domain-side relaxation need
their own exact-build evidence and must not be inferred from this slice.

## Follow-up bounded increment: selected-rule and modifier predicates

Date: 2026-09-02 (Asia/Shanghai)

The profile now also recognizes two scalar trigger forms already supported by
the exact-build research ledger.  This is still a syntax/profile increment;
both descriptors remain `certified=false` and `certifiedSemantics` is empty.

* `has_game_rule = <setting-key>` is backed by the `[static-confirmed]`
  CK3 1.19.0.6 factory/leaf and scalar setting-key layout in the parent
  ledger, `docs/ck3-native-ai/combat-phase-events.md:1256-1290` and the
  cross-check in `campaign-root-context.md:301-330`.  The evidence uses
  `easy_difficulty`/`very_easy_difficulty` and proves selected-setting
  membership, not a general runtime evaluator contract.
* `has_character_modifier = <modifier-key>` is backed by the
  `[static-confirmed]` character modifier membership chain in the parent
  ledger, `docs/ck3-native-ai/combat-phase-events.md:1632-1668`.  The key is
  resolved by stable modifier bytes and active-row pointer membership; no
  arbitrary modifier execution is inferred.

Both references are for CK3 1.19.0.6 executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

### Bounded measurement

The same 75-file phase-two root and parser corpus were used as the previous
measurement (`23,831,185` bytes, corpus SHA-256
`540333b6d145626805da964ac5fbfbff65afcec547bf7a84b76ec265fa8b2b82`).
Compared with the preceding `has_variable` slice, the two new descriptors
remove 430 `has_game_rule` and 171 `has_character_modifier` unknown-opcode
diagnostics:

| root | validator UNKNOWN_OPCODE | validator WRONG_DOMAIN | UNKNOWN_DIRECTORY |
|---|---:|---:|---:|
| preceding slice | 233,558 | 143 | 7 |
| this increment | 232,957 | 151 | 7 |

The eight additional `WRONG_DOMAIN` diagnostics are the existing deliberate
script-value boundary: those files use the newly recognized trigger inside a
script-value condition, while this validator still rejects trigger opcodes in
the `SCRIPTED_VALUES` domain.  No condition-domain relaxation was bundled into
this increment, so the boundary remains visible rather than being silently
suppressed.  The overall root scan is still RED and is not a CK3 launch gate.

`Ck3Profile11906Test` pins both scalar shapes and `ValidatorTest` covers one
representative scalar use for each.  Maven's focused reactor run completed
with 8 profile tests and 18 validator tests passing; no CK3 process, save, MCP,
or network was used.

## Follow-up bounded increment: government flag membership predicate

Date: 2026-09-02 (Asia/Shanghai)

The exact-build 1.19.0.6 ledger describes `government_has_flag =
<government-flag-key>` as a scalar membership trigger on the current
character's government.  The static chain resolves the government type and
searches its sorted flag-ID span; the supporting parent evidence is
`docs/ck3-native-ai/campaign-root-context.md:259-269` and
`docs/ck3-native-ai/combat-phase-events.md:1893-1904`.  This increment only
registers the scalar profile shape as `TRIGGER`/`CHARACTER` with no parameters;
it does not add a runtime evaluator or a `certifiedSemantics` entry.

The phase-two corpus uses this predicate in character interaction, activity,
event, and scripted-value conditions.  Before/after counts and corpus hash are
recorded by the parent acceptance preflight; the schema reduction is expected
to leave the overall root scan RED because the profile remains intentionally
bounded.  `Ck3Profile11906Test` pins the descriptor and `ValidatorTest` covers
the observed scalar form.  No CK3 process, save, MCP, or network is used.

## Follow-up bounded increment: character perk membership predicate

Date: 2026-09-02 (Asia/Shanghai)

The exact-build CK3 1.19.0.6 ledger contains the static-confirmed form
`has_perk = stalwart_leader_perk` in the combat-phase event path.  The recorded
chain is the character `CharacterPerkSpan` membership lookup (literal RVA
`0x438FE18`, factory `0x28681C0`, evaluator `0x2867940`, and loaded database
`0x88EC20`), with the stable perk key carried at `CharacterPerk+0x18`.
The parent evidence is `docs/ck3-native-ai/combat-phase-events.md:885-923`;
the frozen executable fingerprint is CK3 1.19.0.6 SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

This slice registers only the observed scalar profile shape:
`TRIGGER`/`CHARACTER`/`STRING`, zero named parameters, deterministic and
read-only, with `certified=false`.  It deliberately adds no runtime evaluator
or `certifiedSemantics` claim: static syntax evidence is not runtime
certification, and predicates without a closed exact-build descriptor remain
out of scope.

### Bounded measurement

The stock combat-phase event corpus contains 15 `has_perk` occurrences (the
observed key includes `stalwart_leader_perk`).  The frozen phase-two mod root
contains zero occurrences, so this G2/combat-schema preparation does not claim
to reduce the phase-two loader RED or change its file/diagnostic counts.

Offline verification used the same 1.19.0.6 profile and synthetic fixture:

* focused Maven tests passed: 10 profile tests and 20 validator tests;
* offline package produced byte-identical CLI JARs, 316,131 bytes each,
  SHA-256 `392B130B7F6DCB516627EAE284CF673C7F109D6857A5C6388AE56F02EC0BF1AD`;
* preflight artifact
  `Z:\\ck3_mod_rewrite_process_assets\\zg361\\phase2-bounded-gate-20260902\\kaishek-has-perk-preflight-20260902.json`
  has SHA-256
  `F37560B6C0B3113C3B6F0EDE04368E9C5B57BE55F473938DCE7A2D355787E8E9`.
  The parser/IR-runtime fixture stayed GREEN (76 files, 23,831,410 bytes),
  while the bounded validator remained RED with 233,014 diagnostics, as
  expected for the unchanged phase-two root.

No CK3 process, save mutation, MCP call, or network access was used.

## Follow-up bounded increment: dynasty perk membership predicate

Date: 2026-09-02 (Asia/Shanghai)

The exact-build CK3 1.19.0.6 ledger contains the static-confirmed scalar form
`dynasty ?= { has_dynasty_perk = warfare_legacy_3 }`.  Its native chain
resolves Character -> House -> Dynasty with full-generation identity checks,
resolves the stable perk key from the loaded DynastyPerk database, and tests
pointer membership in the dynasty's owned-perk span.  The parent evidence is
`docs/ck3-native-ai/combat-phase-events.md:804-850`; the frozen executable
SHA-256 is
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

This increment registers only `has_dynasty_perk = <stable-key>` as a scalar,
deterministic, read-only `TRIGGER` with `STRING` input and
`certified=false`.  The current profile does not expose a dedicated
`DYNASTY` scope type, so the descriptor uses `THIS` to preserve the native
current-scope requirement without inventing a scope-transition contract.  It
does not add a runtime evaluator, a `certifiedSemantics` entry, or a claim that
the enclosing `dynasty` relation is executable offline.

### Bounded measurement

The frozen phase-two root contains zero `has_dynasty_perk` occurrences, so
this G2/combat-schema preparation does not claim to reduce the loader RED.
The exact-build stock accolade-type file contains 22 scalar occurrences,
including `warfare_legacy_3`, which confirms the observed syntax remains in
the frozen data rather than only in the research prose.

Focused Maven verification passed 11 profile tests and 21 validator tests.
The offline package produced a 316,162-byte CLI JAR with SHA-256
`D4BA0FF5E6A9C85ED0853FD78D44940E98445F2867E9D6CA5902AF0E19B29476`.
Direct CLI preflight against the frozen phase-two root kept parser GREEN
(`75` files, `23,831,185` bytes, zero diagnostics; corpus SHA-256
`540333b6d145626805da964ac5fbfbff65afcec547bf7a84b76ec265fa8b2b82`)
and retained the expected bounded validator RED (`233,014` diagnostics).
The synthetic fixture IR/runtime stages stayed GREEN.  No CK3 process, save
mutation, MCP call, or network access was used.

## Follow-up bounded increment: effective DLC feature membership predicate

Date: 2026-09-02 (Asia/Shanghai)

The exact-build CK3 1.19.0.6 ledger describes
`has_dlc_feature = <feature-key>` as a scalar trigger over the current
process's effective gameplay feature bitset.  The static chain is the
`CHasDLCFeatureTrigger` registration and leaf (`0x289B350` parser,
`0x289B430` evaluator) reading the 44-entry bitset rooted at
`module+0x576CC68`; the parent evidence is
`docs/ck3-native-ai/loaded-feature-manifest.md:8-16,56-84` and the frozen
executable SHA-256 is
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

This increment registers only the observed scalar profile shape:
`TRIGGER`/`THIS`/`STRING`, zero named parameters, deterministic and read-only,
with `certified=false`.  It deliberately adds no runtime evaluator,
feature-manager query, or `certifiedSemantics` entry: static exact-build
evidence is not runtime certification, and installed DLC metadata is not a
substitute for the current process bitset.

The focused profile and validator tests cover the scalar form and a
representative `royal_court` key.  Offline package/preflight verification is
required before merge; no CK3 process, save mutation, MCP call, or network is
used for this schema-only slice.

## Follow-up bounded increment: court-position membership predicate

Date: 2026-09-02 (Asia/Shanghai)

The exact-build CK3 1.19.0.6 ledger closes the scalar form
`has_court_position = <court-position-key>` on a current Character.  The
observed Garuda path binds the trigger literal at `0x435AE20`, registration
chain `0x530620..0x5306B3`, factory `0x435C7F8`/creator `0x2826AD0`, and the
compiled evaluator at `0x2825600`.  Its native reader resolves the loaded
court-position type by stable key, then tests the current Character's
generation-safe held-position ID span; the parent evidence is
`docs/ck3-native-ai/combat-phase-events.md:1723-1777`.  The frozen executable
SHA-256 is
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

This slice registers only the observed scalar profile shape:
`TRIGGER`/`CHARACTER`/`STRING`, zero named parameters, deterministic and
read-only, with `certified=false`.  It adds no court-position database
reader, scope transition, runtime evaluator, or `certifiedSemantics` entry;
static exact-build evidence is not runtime certification.

The frozen game tree contains 1,222 scalar `has_court_position` occurrences,
including the exact `garuda_court_position` key.  The current phase-two mod
root contains no occurrence, so this preparation increment does not claim a
reduction of its existing bounded validator RED.  Focused profile and
validator tests pin the shape and representative use.  No CK3 process, save,
MCP call, or network was used.

## Follow-up bounded increment: war-days duration predicate

Date: 2026-09-02 (Asia/Shanghai)

The next narrow semantic slice is the stock `war_days` trigger used by CK3
war AI and interaction gates.  Exact-build static evidence is pinned to the
CK3 1.19.0.6 executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`:

* the anchor ledger records the high-static-confidence evaluator at RVA
  `0x2848230` (`ck3_1_19_0_6_anchors.json`, `war_days_trigger_evaluator`);
* `CWar+0xE0` is the signed `start_date_raw` field, and the documented native
  calculation is `(current CGameState date_raw - start_date_raw) / 24`;
* vanilla `common/trigger_localization/00_war_triggers.txt` maps
  `war_days` to `WAR_DAYS_TRIGGER` (file SHA-256
  `A582506DA28E6917D0D00160A79C1ED6E1F46BEACAC90AD9CD877E1569FD76DD`), while
  `common/character_interactions/00_war.txt` contains representative
  `>=365`, `>=182`, and value-copy forms (file SHA-256
  `5C99B8F14893929A9BC2DBB5B258CDD2D4233D5805091952209413DE876EE09F`);
* the frozen installation has 28 `war_days` occurrences across 13 script
  files.  These counts are provenance only and do not imply runtime
  certification.

The profile now registers the minimal scalar shape
`TRIGGER`/`WAR`/`INTEGER`, zero named parameters, deterministic and read-only,
with `certified=false`.  `Ck3WarDaysFixture` renders a UTF-8-BOM source slice
with both range operators (`war_days >= 365` and `war_days < 9125`).  The
offline preflight accepts fixture ID `ck3-war-days-trigger-11906`; parser and
validator are expected GREEN while IR/runtime are explicitly SKIPPED.  This
is a schema/shape increment only: no native reader, date resolver, action,
or `certifiedSemantics` entry was added, and no CK3 process, save mutation,
MCP call, or network access is involved.
