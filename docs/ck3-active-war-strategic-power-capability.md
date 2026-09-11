# CK3 active-war strategic-power compatibility boundary

Date: 2026-09-12 (Asia/Shanghai)

Root provider commit `283904d5438e07c18a49903333eafcaaabb75e80`
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

The native schema-v1 result shape remains unchanged. R470 proved that the old
DLL rejected an active-war-only target with `target_not_declarable`; root
provider commit `283904d5438e07c18a49903333eafcaaabb75e80` corrected the
native frame and reader to freeze and admit declaration targets and active-war
primary opponents separately.

## Compatibility and readiness

open_kaishek records this query as read-only, deterministic, native-certified,
and runtime-certified for exact build CK3 `1.19.0.6`. It neither starts CK3
nor exposes a gameplay action. R471 used the corrected DLL to issue two
official MCP queries on one unchanged paused checkpoint frame. Both returned
the active-war source and identical native results apart from monotonic query
sequence: player `29829` power `13075500000`, opponent `28551` total power
`16770900000`, and ratio `128262/100000`.

The original R471 report is retained as RED because its runner audited absent
`history` instead of public `native_command_history`. The canonical history
contains exactly the two successful read-only queries and no mutation or time
advance. Root commit `050c94fbd2ba9ecd41f4419e1dc936bd7c083774`
fixes that audit and records a GREEN offline reclassification without replaying
the query or restarting CK3.

This narrow certification does not certify campaign dominance, a surrender
recommendation, action readiness, or GEN-034 completion. Those remain separate
policy and action capabilities.

The R470 evidence is pinned by root commit
`4ac06131ce04a4af02cbe9898b8eca780c8aa181` and report SHA-256
`CCF29894130EB673C59FDAD03E39D15A6BEB649392C6A2D02E1DF8A92C7E022D`.
R470 issued one read-only query, no retry, no time advance, and no mutation;
cleanup was GREEN.

The R471 original report SHA-256 is
`F467676201497A75C08ED5F6C72AFE64618337C73EFD2BA816B981470CE1E7CD`;
the GREEN offline reclassification SHA-256 is
`D8F43EABC2A38F451FCAB1FE8DAEEB157EF8C62439B904DF96E8AFA301E924C9`.

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
| native frame header | `51F1830553A353C52A8AD757550791CFC556ECEB13BE7F07857361C901F9F2B9` |
| native reader | `9448C95D787B88453085FA9AB76C895AC72B2911F320877396DF9962E4207D74` |
| bridge dispatch | `25D62A80967B9AD41090C6310D4BE21A8B0A1F7F17FCEE02884CEC60425666AC` |
| native unit test | `AF8D6425C40C220FD866F2A49C4C4E5B8183EB0C329310B8EDC6455098B1B135` |
| native source-contract test | `927FFE8451423F7D09835B6FF280C4E83FFF4486C56DCF35A1D0B9ACE3B81058` |
| native ABI ledger | `4F786F156356F3B52EE1D1C408CF4A2912E154867793ECF2E75A3728D0F1385A` |
| production-live DLL | `65C14FE284EA99036DBFBA950B3BE38C3656FA2D064017FD8A38FF21B32B61EF` |
| corrected live runner | `A364FCFB89AAD6C25DABAB72AD708BB564793B484B79B983AB3DCE11ACC10679` |
| focused runner test | `F14E420ECE5784BC6B3B2F7FBB9DF79E8D13416D8AE2955ECC069B684110FED3` |

No CK3 process or promotional-video asset is touched by this synchronization.
