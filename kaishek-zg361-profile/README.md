# Kaishek 361 domain profile (Phase 0)

This module is part of the independent
[XenoAmess/open_kaishek](https://github.com/XenoAmess/open_kaishek) repository.
It contains the first, schema-only projection of the companion source document
`<companion-root>/mod_zhongguo_style/docs/361-domain-runtime-architecture.md`. The
companion mod source and its `docs/` directory are **not** included in this
independent repository; provide their path explicitly when running a corpus
check. The checked-in
`src/main/resources/zg361/domains.json` describes all 38 domains (A–AL), the
1–361 mechanism coverage, state graphs, permission boundary, bounded capacity,
cleanup and stale-deadline contracts. It intentionally does **not** execute a
domain runtime or claim CK3 readiness.

The project source is licensed under [GPL-3.0-only](../LICENSE). This profile
contains metadata and tests authored for this repository, not CK3 game files or
copied companion-mod code.

Run the dependency-free validator and tests from this module directory:

```text
py tools/validate_domains.py
py -m unittest discover -s tests -v
```

From the repository root, the corresponding Java module build is:

```powershell
mvn -o -ntp -pl kaishek-zg361-profile -am test
```

The Java `DomainGraphValidator` exposes the corresponding domain, ACL and
state-graph checks as a typed projection; the Python validator remains the
authoritative JSON-level checker for coverage metadata and global boundaries.
Neither implementation depends on Quarkus or a JSON library.

The offline synthetic 014 vertical slice is implemented by
`Synthetic361Pipeline`: it generates a BOM-bearing `.txt`, runs the lossless
parser and schema validator, lowers to strict IR, and executes three explicitly
certified in-memory handlers (`delivered -> appeal_open -> closed`). The
fixture profile is not the CK3 profile and must not be described as live or
differential-certified. See [`../docs/synthetic-361-slice.md`](../docs/synthetic-361-slice.md)
for the boundary and reproduction command. The full 361 target corpus remains
an external input (normally
`<companion-root>/mod_zhongguo_style`); a successful synthetic fixture does not
imply that the external mod has been parsed or that CK3 semantics are certified.

`ZhongguoBusinessPostconditionProfile` also projects two read-only product
query contracts: the CP-to-Phase-3 project metrics lineage and the #147
promotion-to-compensation lineage. Their dedicated preflight fixtures prove
the exact source shapes (`NOT` and trigger-side scalar `var:*` comparisons)
parse and validate against CK3 1.19.0.6. IR/runtime remain `SKIPPED`, and both
native/runtime certification bits remain `false`; see
[`../docs/zg361-business-postconditions.md`](../docs/zg361-business-postconditions.md).

`G2TruceEvaluatorCapabilityProfile` records the read-only G2
`evaluated_days` observation contract, including same-frame double-read and
non-inferred expiry invariants. The default production leaf-context reader has
passed a paused, read-only, dual-query public-wire acceptance, so that narrow
observation primitive is native/runtime certified. Expiry, decision, automatic
surrender, and action readiness remain closed; the descriptor does not add an
opcode or an action path. See
[`../docs/g2-truce-evaluated-days-capability.md`](../docs/g2-truce-evaluated-days-capability.md).

`G2WarBoundLossCandidateMetadata` pins the separate default-OFF frozen-set
`598 -> 0` cleanup candidate without registering a capability or changing the
public wire. Source attribution, termination/surrender causality, public
readiness, production live, and GEN-034 remain false; see
[`../docs/g2-war-bound-loss-candidate.md`](../docs/g2-war-bound-loss-candidate.md).

`ZhongguoManagerGovernanceCapabilityProfile` records the Phase-2 B3
manager-governance snapshot capability after its native mailbox, driver,
service, and MCP transport were wired. It remains a read-only, hash-bound,
uncertified descriptor; the MCP command is not registered as a Paradox opcode
or an open_kaishek runtime handler. See
[`../docs/zg361-manager-governance-capability.md`](../docs/zg361-manager-governance-capability.md).

`ZhongguoManagerSubordinateSelectorCapabilityProfile` records the exact-build
read-only selector used to observe one bounded AI manager and one direct
subordinate. The profile validates the public query identity, response fields,
selection invariants, and companion hashes while deliberately advertising no
downstream action capability. See
[`../docs/zg361-manager-subordinate-selector-capability.md`](../docs/zg361-manager-subordinate-selector-capability.md).

`ZhongguoCareerHcWorkforceCapabilityProfile` records the exact-build read-only
M360 route-B career-HC/workforce postcondition query. It freezes the receipt,
headcount-conservation, zero-manager-cost, same-frame, and typed-unavailable
invariants while both the native provider and downstream action remain
uncertified and unadvertised by default. See
[`../docs/zg361-career-hc-workforce-capability.md`](../docs/zg361-career-hc-workforce-capability.md).

`G2ActivityTypeSchemaRedFixture` records the next G2 compatibility boundary:
the `zg361_jingcha` activity source parses, while activity-type-owned keys are
still reported as `UNKNOWN_OPCODE` by the conservative Phase 0 validator. The
fixture is intentionally RED and does not add guessed activity vocabulary,
native/runtime certification, or a CK3 launch path.
