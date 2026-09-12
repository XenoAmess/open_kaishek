# Portable operator MCP compatibility boundary

Date: 2026-09-10 (Asia/Shanghai)

Companion commit `5610de6bae4f5ac7e04ba3bedb25084837608183`
publishes target-side operator MCP contract `1.1.0` with profile schema `1`.
open_kaishek records the five-tool contract and provides a transport-neutral
consumer adapter; it does not duplicate the target server or its deployment
profile.

| tool | request fields | semantics |
| --- | --- | --- |
| `operator_get_capabilities` | none | read server/profile identity, configured jobs and tools |
| `operator_get_status` | `target_id` | read token, desktop, machine, process gates and handed-off jobs |
| `operator_preflight_job` | `target_id`, `job_name` | read-only identity/input/output/process-gate check with GREEN/RED evidence |
| `operator_handoff_job` | `target_id`, `job_name`, `request_id` | launch only the profile-frozen job; request ID is idempotent per server instance |
| `operator_control_job` | `target_id`, `job_name`, `job_id`, `control_name`, `request_id` | send only a named, profile-frozen stdin control to the matching live job; request ID is idempotent per server instance |

`OperatorMcpTarget` receives endpoint, target ID and job name from deployment
configuration. `OperatorMcpTransport` is supplied by the embedding client, so
HTTP/stdio transport, credentials and session lifecycle remain outside the
profile API. Operator account, desktop, machine, command, working directory,
input/output paths and exclusive processes remain target-side JSON profile
data. No user, machine, run number, campaign turn or filesystem path is
compiled into this adapter.

`OperatorMcpClientAdapter.inspectReadiness()` performs the documented
capabilities -> status -> preflight sequence and rejects version, target,
profile, server-instance or identity mismatches. For a first request,
`handoffJob(requestId)` requires that read-only sequence to be GREEN; the
server still repeats preflight atomically inside the handoff. The adapter marks
the request attempted before transport invocation. If the response is lost,
the same method retries directly with the same request ID instead of being
blocked by the now-active-process preflight.
`retryHandoffJob(requestId, expectedServerInstanceId)` makes that recovery
explicit for a recreated client and refuses to reuse the request ID after the
server instance changes. A received ACCEPTED response is deeply detached and
cached, preventing duplicate transport calls in that adapter instance.

`discoverControls()` reads `job_controls` for the selected job. Contract
`1.0.0` remains a valid four-tool baseline and returns an empty control list;
an existing `contractV1` target accepts the compatible `1.1.0` server upgrade.
Clients that require controls can use `contractV11`, which rejects a 1.0
server or missing 1.1 metadata. `controlJob(...)` refuses unadvertised names
locally and sends only job/control/request identifiers, never payload bytes.
Its attempt is bound to the observed server instance and exact job/control;
an ambiguous response retries the same request, while a received `RED` or
`ACCEPTED` result is detached and cached so it is not silently written twice.

The client never accepts a command, working directory or process name. A new
request ID cannot bypass RED; only a retry of the same portable identifier can
reach the server's idempotent replay branch. The target server remains the
authority for binding one request ID to one configured job.

## Frozen provider evidence

| companion input | SHA-256 |
| --- | --- |
| `operator_mcp.py` | `E604EF3980A789B71D622581E73C32A15E1F90BA8123C5DCEAC0AAA89FAF8CC5` |
| `operator_mcp_server.py` | `43F7B451A8EBE63A4C3C89B3516343DEB0F7CD6D6ED30A40623653E41A089834` |
| profile example | `E5C051544A6DDE92B249BC1972E8A8AA278654A63C6C20B99C65C532684B6678` |
| provider contract tests | `538331C9261730F555DF76083BD46B0C32493186DEC2FAC97573D973932BC149` |

## Scope and readiness

The three inspection capabilities are read-only. Handoff and control are
non-destructive at the MCP contract level but can start a configured process
or send its configured stdin payload; both are deliberately not marked
read-only. No capability is described as native- or runtime-certified by
open_kaishek. This integration adds no Paradox
opcode, parser vocabulary, IR instruction, default endpoint, credential,
target bootstrap, CK3 launch, or promotional-video action.

## Verification

- Provider `test_operator_mcp.py`: 9/9 PASS in normal mode and 9/9 PASS with
  Python `-O`, including the official MCP client listing/calling all five tools.
- Adapter/profile focused Java suite: 15/15 PASS.
- Full offline Maven reactor: 182 tests, zero failures, errors or skips;
  `BUILD SUCCESS`.
- Python domain suite: 8/8 PASS in normal mode and 8/8 PASS with `-O`.
- Metadata check and domain validator: PASS in normal and optimized modes.
- Portability literal audit found no configured operator account, R-number,
  sandbox account or Windows absolute path in the new client/profile code.
- No CK3 process was started or attached and no companion/P2 file was changed.

## Phase-2 bounded endgame-source job

The root target now defines a reusable `phase2-endgame-source` job whose
advertised controls are `status`, `run-source`, and `cleanup`. Its frozen
activation binds the product tree, code commit, exact game build, bridge,
near-boundary checkpoint, three-row source prefix, owner/date, and an enforced
maximum of 30 game days. The action stops on the first real `zg361we.356` and
then delegates same-frame save and 4/4 registry assembly to the existing source
capture primitive.

This is a new target profile job and control name, not a change to operator MCP
contract `1.1.0`. `OperatorMcpClientAdapter` already discovers target-owned
`job_controls` and sends only an advertised control identifier, so no Java
schema or transport change is needed. Deployment profiles may add this job
without compiling an account, machine path, CK3 round, or endpoint into the
consumer. Root action/operator source SHA-256 values are
`1E0052800E9941A493CBCDEFABEB9E6D0B0E4A75108AF4F0802CFE908DFC6DE8`
and `E1A17728EA958EFBF16272ACCC532AD293A1F217A03211BA8A2CCFD7BE6673B9`.

The root focused tests pass `4/4` in normal mode and `4/4` with Python `-O`.
No companion test is repeated because the generic discovery/control consumer
and its request/response schema are unchanged. Live source capture remains a
root target responsibility.

The first root no-launch preflight preserved a target-side lineage RED because
the fourth row supplied an EXE SHA without the game version required by the
existing schema-3 prefix. Root commit `b1a272151ef4e313bf68dae1824ce63a6c09cc2b`
adds the exact version/SHA pair. Operator MCP `1.1.0` remains unchanged.
