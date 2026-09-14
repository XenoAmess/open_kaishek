# CK3 campaign-root partial-readiness compatibility boundary

## Decision

Root provider revision `764e1c4fe0a33c670a4563c14ec98859390a091d` changes the failure boundary of
the existing `campaign-root-context-v1` response. If the provider cannot read
`selected_game_rule_tokens`, it now preserves the independently observed
campaign-root state and publishes:

- top-level `status=available`;
- `selected_game_rule_tokens=[]` and
  `native_selected_game_rule_token_count=0`;
- `readiness.selected_game_rule_tokens_ready=false` and
  `readiness.ready=false`;
- every other root field and readiness result from the same stable frame.

Consumers must use `readiness.selected_game_rule_tokens_ready` to distinguish
an unavailable token observation from a successfully observed empty token set.
Top-level `status=available` means the campaign-root frame is structurally
usable; `readiness.ready=false` continues to mean that the aggregate is not
fully ready. A turn bundle may therefore consume the independent root fields
and publish `status=partial` instead of losing ruler and succession state to an
unrelated optional token-reader failure.

The same provider boundary keeps celestial governments outside the standard
five-seat council reader. For `government_is_celestial`, the campaign root
remains available while `council.status=unavailable`,
`council.unavailable_reason=outside_standard_landed_non_nomadic_core_scope`,
and `readiness.council_ready=false`. Council readiness remains independent of
the aggregate `readiness.ready` calculation.

The compatibility verdict is
`EXISTING_ROOT_STATUS_AND_READINESS_SEMANTICS_CHANGED /
OPEN_KAISHEK_NO_ROOT_CONSUMER / DOCUMENTATION_ONLY`. A repository-wide
non-documentation search finds no `campaign-root-context-v1`,
`campaign_root_context`, `selected_game_rule_tokens`, or
`selected_game_rule_tokens_ready` caller, parser, Java response type, adapter,
validator, profile, or MCP endpoint in open_kaishek. No existing consumer
depends on `readiness.ready=true`, because no existing consumer reads this
response. The Operator MCP envelopes, Java API, profiles, dependencies,
endpoints, and version `1.1.0` need no code change.

## Consumer rule

A future typed consumer must treat the two readiness bits separately:

1. `status=available` admits the root envelope and its independently ready
   fields.
2. `selected_game_rule_tokens_ready=false` forbids interpreting the empty token
   array as an observed empty selection.
3. `readiness.ready=false` forbids claiming full aggregate readiness, but does
   not erase ruler, date, government, succession, or other ready components.
4. `council_ready=false` with the documented out-of-scope reason is valid for
   celestial government and must not be converted into a root failure.

## Portability and reopen trigger

This compatibility decision pins landed root provider commit
`764e1c4fe0a33c670a4563c14ec98859390a091d` and is independent of a machine
path, account, CK3 process, or run identifier.

Reopen this decision if open_kaishek starts consuming campaign-root or
turn-bundle responses, if the provider changes the field names or reason
vocabulary, if `readiness.ready` stops tracking full aggregate readiness, or if
celestial ministries receive a dedicated typed council schema.
