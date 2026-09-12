# CK3 vanilla-event portable asset compatibility boundary

Date: 2026-09-10 (Asia/Shanghai)

Companion commit `6e780e08d136df8d8fb0141c8f0d41bab644fcf2` carries four
offline, read-only MCP tools around the existing single-event knowledge query.
`VanillaEventPortableAssetCapabilityProfile` pins their consumer-visible v1
contracts in open_kaishek:

| MCP tool | request fields | response schema |
| --- | --- | --- |
| `ck3_list_vanilla_event_knowledge_v1` | `ck3_build`, `query`, `namespace`, `evidence_class`, `has_observations`, `after_key`, `limit` | `xar.ck3.vanilla-event-knowledge-index` v1 |
| `ck3_list_vanilla_event_evidence_v1` | `event_definition_key`, `kind`, `after_evidence_id`, `limit`, `ck3_build` | `xar.ck3.vanilla-event-evidence-list` v1 |
| `ck3_read_vanilla_event_evidence_v1` | `evidence_id`, `offset`, `max_bytes`, `ck3_build` | `xar.ck3.vanilla-event-evidence-read` v1 |
| `ck3_query_vanilla_event_source_provenance_v1` | `key`, `build` | `xar.ck3.vanilla-event-source-provenance` v1 |

Knowledge and evidence lists use stable keyset pagination and return dataset
SHA-256 identities. Evidence reads are addressed by the uncompressed content
SHA-256, return base64 chunks of at most 65,536 bytes, and verify the packaged
blob before returning content. MCP requests cannot supply a bundle root or file
path. List/source responses expose only repository-relative logical paths.

Historical observation bytes are preserved exactly and may contain locators
from the machine on which they were captured. The read envelope identifies
that condition with
`historical_artifact_may_contain_nonportable_locators`; those bytes are
evidence, not current lookup paths. Source provenance likewise keeps
`caller_candidates_are_lexical_only=true` and
`caller_candidates_review_status=not_manually_reviewed_as_call_edges`, so an
exact-token candidate cannot be promoted to a proven runtime call edge.

## Frozen provider evidence

The compatibility descriptor binds the exact provider commit and these inputs:

| companion input | SHA-256 |
| --- | --- |
| MCP server | `A67D98C4D3B452D09AF0B9A830E5E95BB58C31AF52B31AAA586423B18E4ED320` |
| discovery implementation | `962966AD9AD18666F138E0B491F1FA68172CC149FC81261E0302534D8DF4A0D2` |
| portable evidence implementation | `FECDEC3F78349A2D25B24DC29AAF342D48893764375547171DE3D9F06545B29E` |
| source index implementation | `4A8ECB5576FB5F140EC6FDE08CB8CB185E523CD833D5CE84B8EA34436C706D8D` |
| portable evidence manifest | `F2D15B19DCEB80BE6EFDE6FCA49DAD2AE6842A49AE37A7F94DB1F6DE00B039E1` |
| source index dataset | `FE3A1D8472B14A6901117243A21E7A97E5AD60EE685D03057D5F17BC568F43DC` |

The four response schema byte hashes are pinned in the Java descriptor. At root
revision `738650b0113b6a3a61933d0a23501ce503ebe755`, the manifest
contains 280 content-addressed evidence entries: 192 deduplicated source
definitions and 88 observation artifacts. Its 1,086 references include 184
generated definition references, 522 explicitly lexical caller candidates,
276 manually reviewed analysis-source references, and 104
observation-artifact references. The source index covers all 184 registered
events with zero missing, ambiguous, or namespace-mismatched definitions. The
newest additive record is `tgp_japan_yearly_events.1030`; its exact-build
definition, two lexical caller candidates, and R463 pre-selection RED are all
available through the existing read-only interfaces.

Root closure commit `66b63f42a26a7ab16e6b4fa3e7394c7438e6079e` subsequently
records that R463 disappeared after that immutable RED was saved. This does not
alter the packaged evidence bytes, manifest, source index, request/response
schemas, or offline verification result; only live recovery now requires a new
bounded round.

Root revision `53e229e1d7c5227e1b7d8f1f06ab3d7f456271f8` adds the R465
post-selection artifact for `.1030`. The bundle now contains 281 evidence
entries, 89 unique observation artifacts, 105 observation references, and
1,087 total references. The current manifest SHA-256 is
`B9910108273AB057E4FC246E89FCE303D65C2986C42B8CF33ACC016CEC694D55`.
The new artifact is content-addressed and remains available without the
originating runtime path.

Root revision `6e780e08d136df8d8fb0141c8f0d41bab644fcf2` adds the
source-reviewed `stress_threshold.1011` definition, its single exact-token
external caller candidate, two manually reviewed source references, and the
R498 pre-selection observation. The bundle now contains 282 evidence entries,
90 unique observation artifacts, 106 observation references, and 1,092 total
references. Generated-definition and lexical-caller references advance to 185
and 523; manually reviewed analysis-source references advance to 278. The
source index covers all 185 events with zero missing, ambiguous, or
namespace-mismatched definitions. The R498 RED is addressed by
`B51B8960C470FA5D76A73724F6791425EF5B23BF4FC10B6D361DF4B6574F392F`;
the current manifest and source-index dataset hashes are pinned in the table
above and in the Java descriptor.

Root revision `0494375d74a4928573cf47672b6150a0ab894ed2` adds the
source-reviewed `trait_specific_interactions.0011` definition, its poetry
interaction caller, option-effect source, and R500 pre-selection observation.
The bundle now contains 286 evidence entries, 91 unique observation artifacts,
107 observation references, and 1,098 total references. Generated-definition,
lexical-caller, and manually reviewed analysis-source references advance to
186, 524, and 281. The source index covers all 186 events. The R500 RED is
addressed by `8DF21A7682E1B30258A12DB736B975E348CE434C6A2A3D0AFFF11F30424B04AA`;
the current hashes are pinned in the table and Java descriptor above.

Focused offline Maven verification for the revised portable-asset descriptor
passes `4/4` tests. No CK3 process or target runtime was contacted.

## open_kaishek scope

All four descriptors are read-only and deterministic, with
`nativeCertified=false` and `runtimeCertified=false`. This change adds no
Paradox opcode, parser vocabulary, IR lowering, runtime handler, CLI command,
service endpoint, game process access, or mutation authority. The companion
continues to own both implementation and packaged bytes. The previously synced
B1 cycle snapshot descriptor from open_kaishek commit `c552744` is unchanged.

## Verification

- `mvn -o -ntp ... clean verify`: `BUILD SUCCESS`; 167 Java tests, zero
  failures, errors, or skips. The sandbox used a writable local repository
  populated solely from the existing local Maven cache.
- Focused old/new vanilla-event capability profiles: 7/7 Java tests pass.
- `tools/check_metadata.py`: PASS.
- `unittest discover -s kaishek-zg361-profile/tests`: 8/8 PASS.
- `kaishek-zg361-profile/tools/validate_domains.py`: PASS
  (`schema-only; runtime not implemented`).
- Work began from `main == origin/main == c552744`; the local source ref was
  fetched and `git rebase origin/main` reported current. No merge was used.
- CK3 was not started or attached, and no save or companion repository file
  was modified.

Root revision `fdf23f537d4238850154f85184f503ba63136a3d` adds the source-reviewed `travel_completion_event.1000` definition, travel on-action caller candidates, and R502 pre-selection observation. The bundle now contains 290 evidence entries, 92 unique observation artifacts, 108 observation references, and 1,105 total references. Generated-definition, lexical-caller, and manually reviewed analysis-source references advance to 187, 527, and 283. The source index covers all 187 events. R502 RED `E797ABDD300239ADBC9A60F41E7623B6F58E5B7ABA8D6BB97828F21CD24A6A5A` is included; current hashes are pinned in the table and Java descriptor above.
