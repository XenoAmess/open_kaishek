# Zhongguo business postcondition profile

This document binds two companion-product query contracts to the independent
`open_kaishek` parser/profile/preflight boundary. It does not copy game data,
start CK3, or claim a live native provider.

## Capability bindings

`game.command.query-zhongguo-projects-metrics-postcondition-v1` requires the
CP #026 contribution receipt id/revision and the Phase-3 #229 source-lineage
fields. The projected invariants are:

* the contribution receipt id and revision are positive;
* Phase-3 project source id/revision equal the CP receipt id/revision;
* the #229 source id/revision equal the Phase-3 project source id/revision;
* the #229 metrics revision is positive; and
* the #229 dictionary key code is positive.

`game.command.query-zhongguo-promotion-compensation-postcondition-v1` requires
the `zg361_pp_m147_receipt_serial/revision` and
`zg361_comp_promotion_receipt_*` variable families. The projected invariants
are:

* both business receipt serials are positive and equal;
* that serial equals the portfolio delivered-result case;
* receipt choice revision equals the #147 consumer revision and the posted
  choice revision;
* posted revision is later than choice revision; and
* the PP T case and compensation L/AE/AF internal kernel cases remain separate
  identities rather than being equated by the cross-product receipt.

The Java profile records these as immutable `CapabilityDescriptor` instances
bound to `ck3-1.19.0.6-zg361-business-postconditions-v1`. Both capabilities
are read-only and deterministic at the contract level, but
`nativeCertified=false` and `runtimeCertified=false` until exact-build native
observation and live postcondition evidence exist.

## Parser and preflight scope

The companion generators use existing CK3 operations (`set_variable`,
`change_variable`, `has_variable`, `if`, and `limit`) plus two source shapes
that the strict profile must recognize:

* uppercase `NOT` as a structural trigger container; and
* a trigger-side scalar comparison with `var:*` on the left.

The latter is recognized as a bounded source expression, not registered as an
opcode. It remains fail-closed on the effect side, and calculated-value block
equality retains its separate CK3 1.19.0.6 unsupported diagnostic. No runtime
handler or certified semantic is added.

Run the two focused offline fixtures with:

```powershell
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar preflight --fixture zg361-projects-metrics-postcondition-v1
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar preflight --fixture zg361-promotion-compensation-postcondition-v1
```

Each fixture is BOM-bearing and must produce parser/validator `GREEN`,
IR/runtime `SKIPPED`, `ck3_started=false`, and the corresponding capability ID
with both certification flags false.
