# CK3 native heartbeat pump-telemetry compatibility

Date: 2026-09-14 (Asia/Shanghai)

## Upstream candidate

The pending G2 PUMP1 root-provider change adds five diagnostic integer fields
to the existing native heartbeat `main_thread_query_mailbox_v1` object:

- `published_sequence`;
- `completed_sequence`;
- `executor_started_requests`;
- `executor_started_sequence`;
- `executor_started_pump_epoch`.

It also appends `mailbox_probe_v1` diagnostics to campaign-root failure error
text. The change does not add these fields to a successful campaign-root or MCP
payload, does not turn the heartbeat into an MCP capability, and does not alter
the existing campaign-root success schema. The final upstream commit is still
pending; this companion work package must remain recorded as `PAIRING_PENDING`
until that commit lands.

## Consumer audit and decision

A repository-wide non-documentation search found no consumer for native
heartbeat frames, `main_thread_query_mailbox_v1`, campaign-root responses,
turn-bundle responses, `mailbox_probe_v1`, or any of the five new field names.
open_kaishek has no closed heartbeat DTO, JSON schema, parser, adapter,
validator, Java profile, or MCP endpoint that can reject the additive members.

The only non-documentation `error` response reader is the existing Operator MCP
client. It validates the Operator MCP handoff/control failure envelope and does
not receive native bridge heartbeat frames or campaign-root provider errors, so
the appended diagnostic text does not affect it.

The compatibility result is
`ADDITIVE_NATIVE_HEARTBEAT_DIAGNOSTICS /
FAILURE_TEXT_DIAGNOSTICS_EXTENDED /
NO_OPEN_KAISHEK_CONSUMER / DOCUMENTATION_ONLY`.
No Java, contract-test fixture, profile, Operator MCP surface, dependency,
endpoint, or version change is required. Adding a dormant consumer or schema
would create a second, unowned contract without improving compatibility.

## Field interpretation

If open_kaishek later consumes this heartbeat, it must treat the five members as
optional additive diagnostics while supporting bridge builds that predate
PUMP1. Their names distinguish publication, completion, and executor-start
progress; their presence alone must not be interpreted as a successful MCP
query or a completed campaign-root observation. `mailbox_probe_v1` remains
free-form failure evidence unless the upstream provider later publishes and
versions a typed diagnostic object.

## Verification and pairing

The compatibility audit is source-only and starts no CK3 process. It is based
on open_kaishek baseline
`de7fc082b280df0f1234b56b612f5065a39da50b`. The final pairing state is:

- companion branch: `wp-ok-g2-pump1-20260914`;
- upstream provider commit: pending;
- interface adaptation: none required;
- CK3/live readiness: unchanged.

Reopen this decision when the upstream commit lands so its identity can be
paired with this note. Reopen the implementation boundary if open_kaishek adds
a heartbeat/campaign-root consumer, the heartbeat protocol version changes, a
new field becomes mandatory for availability, or `mailbox_probe_v1` becomes a
typed success-payload member.
