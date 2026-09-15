# CK3 councillor assignment action compatibility

## Frozen static contract

This package mirrors the `game.action.assign-councillor-v1` contract from the
companion repository at commit
`c9ae3ceb3fe1cb8f37fd412e071e2cdd0c97f0f5`. It is pinned to CK3 `1.19.0.6`
and
executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.
The only v1 position is `councillor_steward`; the covered routes are vacant
assignment and incumbent replacement. Position swaps, guest recruitment,
current-councillor reassignment, and every other council seat are outside the
contract.

The strict request has the ten fields frozen in
`CouncilAssignCouncillorActionCapabilityProfile.REQUEST_FIELDS`. It binds the
candidate and expected incumbent state to one paused Council composition
snapshot, including its public/native revisions, date and owner. The native
action must re-run the exact candidate and final-legality checks immediately
before submitting. Replacement additionally requires the native incumbent
fireability result.

## ACK and receipt boundary

The immediate ACK is either `rejected_before_submit` or
`native_helper_invoked_verification_pending`. The CK3 helper returns no command
manager result, so `native_helper_invoked=true` never means that the command was
queued, accepted or applied. `queue_acceptance_observed` remains false and an
ACK cannot prove a successful assignment.

A result is established only by the separate receipt. `applied` requires a
different, newer paused snapshot whose public and native revisions increased
and whose steward incumbent is the requested candidate with a valid identity
round trip. A rejected ACK yields `rejected`; a submitted ACK without the
required postcondition yields `postcondition_failed`. The Java profile pins the
complete request, ACK, receipt, route, failure and receipt-reason vocabularies
without widening them.

## Readiness and compatibility boundary

This is a passive compatibility profile. The paired upstream package has a
static action core and exact-build submit seam, but its shared capture and
final-legality callbacks are not yet bound. It has no public bridge command,
MCP registration, native capability advertisement, paused action proof or
formal-strategy live proof. Consequently:

- `NATIVE_CAPABILITY_ADVERTISED=false`;
- `MCP_REGISTERED=false`;
- `RUNTIME_CERTIFIED=false`;
- `NATIVE_LIVE_READY=false`;
- `FORMAL_STRATEGY_LIVE=false`;
- `FALLBACK_AVAILABLE=false`.

`open_kaishek` gains no CK3 process authority and cannot submit or emulate the
action. There is no visual, synthetic, hybrid or fail-open fallback. Reopen
this profile when the upstream request, ACK or receipt changes, or when a
public runtime/MCP route and paused postcondition evidence land. The profile
contains no machine path, account identity or CK3 round identifier.
