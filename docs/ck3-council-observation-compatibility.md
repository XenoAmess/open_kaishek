# CK3 typed council observation compatibility boundary

## Decision

Root provider commit
`e7e99e872dbfe3d4774d000b7d9702e122ce0041` extends the existing read-only
`campaign-root-context-v1` and `xar.ck3.turn-bundle/v1` responses with:

- `campaign_root_context.council` and `readiness.council_ready`;
- dynamic materialized council positions plus five standard core vacancy rows;
- generation-validated incumbent and owner CharacterIDs;
- stable position/task keys, `general|county|court` targets, frozen state and
  `infinite|percentage|value` progress;
- typed root failure `council_unavailable` and exact-build provenance fields;
- `realm_state.council` and `readiness.realm_council_ready` in the turn bundle.

The provider can now return `turn-bundle status=available` when every bundle
input is observed. Missing optional snapshot inputs or an out-of-scope council
still yield `partial`. A landless, nomadic, celestial-government or
missing-primary-title campaign
root remains available while its council component reports
`outside_standard_landed_non_nomadic_core_scope`. The top-level campaign-root
`readiness.ready` therefore remains independent of this bounded component's
coverage.

Root commit `3f80e0f67fd2f3a1098da7e7048dc2870b3069e1` corrects the
celestial boundary after R676 showed that CharacterID `32904`'s ministry
representation cannot be read as the standard five-seat council layout. The
wire fields and reason vocabulary do not change: celestial now takes the
already documented component-level out-of-scope result while the remaining
campaign-root fields stay available.

The compatibility verdict is
`ADDITIVE_ROOT_SCHEMA_AND_READINESS / NO_OPEN_KAISHEK_CONSUMER /
DOCUMENTATION_ONLY`. A non-documentation repository search finds no caller,
closed Java response type, adapter or validator for either root tool.
open_kaishek's Operator MCP envelopes, Java profiles, endpoints, dependencies
and version `1.1.0` are unchanged.

## Provider contract pins

The provider remains CK3 `1.19.0.6`, executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

| Provider input | SHA-256 |
|---|---|
| campaign-root ABI | `ABDD4ADAEC8EE29EE144FF2F16C7769B55EDEF9C91C7328FB6D34C76DE805BFF` |
| campaign-root source contract | `E8F9A95FE2A1A008D8046F5C3C25080225CB16180FF6C5713108193069D16F0C` |
| native campaign-root reader | `2C7BBFFF91DC6A4EA978CF15DD9AC279DBD02F9E00B03F10010C88B42638C18D` |
| reader source-contract test | `A7B3D32AF51E7C49B4216EE8324BEF1D16F7B994453E304F5B0D0B96B8A30EBF` |
| Python campaign-root contract | `F01D5AE126A6466A5076E237EFED395012D9AF9E91067B34C4A77D2D2298E463` |
| turn-bundle contract | `EC032458AB313CF59C2B276F0BCA02E250EAB3F1125ED6051EDB67AC3CB2B609` |
| council interface document | `369A3D27D27D3494E8EEB2C1096C3063F0B69F5EA019289D557E07033EDFB83D` |
| campaign-root interface document | `641523FEB6D3F9CD1983BCF5FFFC981A6F892A8BE0D996F9D1325C31F7F117EB` |

These are provider source hashes, not a second schema definition and not CK3
live evidence. Root reports this extension as `static-ready / live=false`
pending one shared bounded independent/vassal paused read. Its fresh Release
build produced 103 immediate GREEN tests; three integration-contract REDs
caused by the concurrently added frontend executor were corrected and rerun
`3/3 GREEN`. Focused Python normal and optimized suites pass `47/47`.

## Portability and reopen trigger

This decision uses Git identities, exact-build identity and content hashes. It
does not require a fixed account, machine path, CK3 process or run identifier.
Any operator can repeat the audit with explicit clean root and companion
checkouts.

Reopen this decision if open_kaishek starts consuming either tool, if the
provider removes or renames council fields, changes task/target/progress or
readiness semantics, versions the envelope, or makes auxiliary vacancies
complete. A future consumer must then add a typed profile and fixture before
claiming compatibility.
