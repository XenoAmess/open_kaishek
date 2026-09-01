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
