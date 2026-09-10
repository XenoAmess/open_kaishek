# CK3 vanilla-event portable asset compatibility boundary

Date: 2026-09-10 (Asia/Shanghai)

Companion commit `df1ed7cb00ef6c54de602fbf182e6204c77c1202` adds four
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
| source index implementation | `959DF0ED0D704A10AC4D0CE2CC57ABD0C6FACC52EFD204EFF8F84210E552F5DF` |
| portable evidence manifest | `BC843955BF8EAC8E0133386EFDD0D9DEF4DE794C50559E4F8C93EA6DFF9A5545` |
| source index dataset | `50DAE4938EF8D2D7677C10C55CB89FB34189B1E11F6788DCB00A4C8FA248759B` |

The four response schema byte hashes are pinned in the Java descriptor. The
manifest contains 213 content-addressed evidence entries: 157 deduplicated
source-definition payloads and 56 observation artifacts. Its 902 references
include 167 generated definition references, 497 explicitly lexical caller
candidates, 181 manually reviewed analysis-source references, and 57
observation-artifact references. The source index covers all 167 registered
events with zero missing, ambiguous, or namespace-mismatched definitions.

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
