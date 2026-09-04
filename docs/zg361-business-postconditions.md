# Zhongguo business postcondition profile

This document binds two companion-product query contracts to the independent
`open_kaishek` parser/profile/preflight boundary. It does not copy game data,
start CK3, or claim a live native provider.

## Capability bindings

`game.command.query-zhongguo-projects-metrics-postcondition-v1` requires the
CP #026 contribution receipt id/revision and the Phase-3 #229 source-lineage
fields. Companion commit `953634265ebf298cec3f2cf3065060e577dc8d17`
also makes top-level `checkpoint_state` mandatory, with the bounded states
`cp26_ready_p3_absent`, `p3_initialized_source_not_ready`,
`p3_source_ready_result_pending`, and `p3_result_committed`. The projected
invariants are:

* the contribution receipt id and revision are positive;
* Phase-3 project source id/revision equal the CP receipt id/revision;
* the #229 source id/revision equal the Phase-3 project source id/revision;
* the #229 metrics revision is positive; and
* the #229 dictionary key code is positive.

The provider-internal allowlist grew from 24 to 40 fields under
`zg361-cp26-direct-p3m229-lineage-v2` so the reader can bind direct CP26 state.
That allowlist and the central stage 7/8 source ordering do not add parser
vocabulary, an action, or a runtime handler. The candidate remains default
OFF, uncertified, and non-live.

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

The companion native adapter now has an explicitly private candidate switch,
`XAR_CK3_ENABLE_ZHONGGUO_PROMOTION_COMPENSATION_CANDIDATE_V1`. It defaults
OFF, so the default adapter still does not advertise the provider. An
explicit ON build advertises the already-wired read-only query only for the
bounded private candidate. This changes neither the public request/projection
schema nor this static descriptor, and no CK3 live result has been collected;
`nativeCertified`, `runtimeCertified`, and production-live readiness remain
false. The source pin is companion `cac1e85b616827a9ae11d755dd71f119325e6f3f`
with source-contract SHA-256
`98ab5f09bb44d6d5cb1062fea64e6fdf9e41cf160f64ecd8d5a644b9086ef627`.
Fresh MSVC 19.51 Release registry builds at that companion commit passed the
focused adapter-registry test once with the switch omitted/OFF and once with
it explicitly ON. These are static native checks only and did not start CK3.

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
