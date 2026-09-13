# CK3 held-title partition compatibility boundary

## Decision

Root provider commit
`6eaf0b40af541b09faf04b4d9f01f1de6a588abe` adds the following fields to its
existing read-only campaign-root and turn-bundle responses:

- `campaign_root_context.held_title_partition`;
- `campaign_root_context.readiness.held_title_partition_ready`;
- typed root failure `held_title_partition_unavailable`;
- provenance `held_title_ids_offset=0x1E0`;
- `succession_state.partition` with current per-title first-heir rows and
  derived risk state;
- `alerts.succession_partition_split` and
  `readiness.succession_partition_ready=true` on an available root.

The compatibility verdict is
`ADDITIVE_ROOT_SCHEMA / NO_OPEN_KAISHEK_CONSUMER / DOCUMENTATION_ONLY`.
A repository-wide non-documentation search finds no campaign-root or
turn-bundle tool caller, closed Java response type, adapter, validator or
runtime mapping in open_kaishek. Existing Operator MCP 1.1 envelopes and all
Paradox syntax/IR/runtime contracts are unchanged. No version or dependency
change is required here.

## Provider contract pins

The provider remains CK3 `1.19.0.6`, executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.
The changed provider inputs at the root commit are:

| Provider input | SHA-256 |
|---|---|
| campaign-root ABI | `38BC7CBFA66C10E0C0583B54F2F71748CAE9AAF29766E3D96B2D509EA0F78AE0` |
| campaign-root source contract | `1F9AF2AB92F188E5E99A1EB4A608071194F93D3F004B24F68D997B80B5F64737` |
| Python campaign-root contract | `87B36052179CA879678A4070AEA48D2AC58E578B071BB41208571B20BE494040` |
| turn-bundle contract | `C62F7A13D82A35043A75D4021F633A9F75584FFB29D942F6B6AE151B5B3A8707` |
| held-title partition interface document | `7677946877C0950252F5718DAD7312C50D43071468AA3ED400B589FD7E7C5354` |

These hashes identify provider source artifacts; they do not make
open_kaishek a second schema owner and do not certify CK3 live behavior. Root
reports the partition field as `static-ready / live=false` pending its shared
bounded two-scene paused read.

## Portability and future trigger

This compatibility decision uses only Git identities, exact-build identity and
content hashes. It does not require a fixed user, workstation path, CK3 process
or round. Any operator may provide an explicit clean root checkout to a future
verifier.

Reopen this decision if open_kaishek begins consuming either root tool, if the
provider removes or renames a field, changes field types or status semantics,
or versions the envelope. At that point the consumer must add a typed profile
and fixture before claiming compatibility.
