# CK3 active-war strategic-power compatibility boundary

Date: 2026-09-12 (Asia/Shanghai)

Root provider commit `81f76e04704305df214e34961a32b768ce14a485`
extends the existing public MCP tool `ck3_query_war_entry_assessments`. Its
single `target_character_ids` entry may now identify either a current
declaration target or the current primary opponent in an active war. The
optional `expected_revision` and exact-build CK3 `1.19.0.6` binding are
unchanged.

The response adds `target_scopes`. Each target reports the stable source value
`declarable_war`, `active_war_primary_opponent`, or both in that order. The
provider checks that source before the native request and again on the same
paused frame after the result. An unrelated CharacterID is rejected before a
named-pipe request.

The native schema-v1 result remains unchanged. Its frozen readiness key
`targets_declarable_ready` is a legacy spelling for a Python-approved target
scope; consumers use the new outer `target_scopes` field to distinguish an
active-war opponent from a declaration target.

## Compatibility and readiness

open_kaishek records this as a read-only, deterministic, native-certified
compatibility profile. It neither starts CK3 nor exposes a gameplay action.
The active-war scope remains runtime-uncertified until the root provider
captures its bounded R459 pre-surrender query. This synchronization therefore
does not certify campaign dominance, a surrender recommendation, action
readiness, or GEN-034 completion.

The contract stores no machine path, account name, credential, CK3 process ID,
or CK3 round identifier. Another operator or machine can consume the same
versioned MCP boundary.

## Frozen provider evidence

| root input | SHA-256 |
| --- | --- |
| MCP server | `3723D21778A5DEAD2D7539BEC9E35E95BB040CE7478334446F375879B2918DBA` |
| Python contract | `0FCA88E320BD2EFBA75371AC54568F984923BABFE06953A0A30D244E4CF50BDA` |
| service | `CD0855232359148329426D26846F3CC77FA3E0C923BF17BC90383283AE26D813` |
| native driver | `5265BC19A3A8003D8A8551BAD50025CE8722979F45C8C99D30EC0CF8AF125ED5` |
| focused bridge test | `13F794BE2B79F30C37ED99AF2064C53D424101F6E7E0130E3AF407AC5A584440` |
| focused contract test | `67A601A0D4867B3224EDDED69B2769B7054944ADAB6467D0FBFDE721A4C6F533` |

No CK3 process or promotional-video asset is touched by this synchronization.
