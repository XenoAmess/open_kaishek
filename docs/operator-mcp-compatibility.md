# Portable operator MCP compatibility boundary

Date: 2026-09-10 (Asia/Shanghai)

Companion commit `e684fd05f94290d58e9fb7009046eb95d60468f4`
publishes target-side operator MCP contract `1.0.0` with profile schema `1`.
open_kaishek records the four-tool contract and provides a transport-neutral
consumer adapter; it does not duplicate the target server or its deployment
profile.

| tool | request fields | semantics |
| --- | --- | --- |
| `operator_get_capabilities` | none | read server/profile identity, configured jobs and tools |
| `operator_get_status` | `target_id` | read token, desktop, machine, process gates and handed-off jobs |
| `operator_preflight_job` | `target_id`, `job_name` | read-only identity/input/output/process-gate check with GREEN/RED evidence |
| `operator_handoff_job` | `target_id`, `job_name`, `request_id` | launch only the profile-frozen job; request ID is idempotent per server instance |

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

The client never accepts a command, working directory or process name. A new
request ID cannot bypass RED; only a retry of the same portable identifier can
reach the server's idempotent replay branch. The target server remains the
authority for binding one request ID to one configured job.

## Frozen provider evidence

| companion input | SHA-256 |
| --- | --- |
| `operator_mcp.py` | `8CFB6AACD81854BF44FFCEC228D3CD91C6222262513467759231D0D3E6DC5BA5` |
| `operator_mcp_server.py` | `43F7B451A8EBE63A4C3C89B3516343DEB0F7CD6D6ED30A40623653E41A089834` |
| profile example | `8B3EA7DAFACE3ABEB313813CB5A4984531E5B89FD516C43B8148858160AE397E` |
| provider contract tests | `6B539D809D835A1D16D2690DF9124D10B327B28EE37EDD05DF61FF16D0133CC1` |

## Scope and readiness

The three inspection capabilities are read-only. Handoff is non-destructive
at the MCP contract level but may start the configured open-world process; it
is therefore deliberately not marked read-only. No capability is described as
native- or runtime-certified by open_kaishek. This integration adds no Paradox
opcode, parser vocabulary, IR instruction, default endpoint, credential,
target bootstrap, CK3 launch, or promotional-video action.

## Verification

- Provider `test_operator_mcp.py`: 6/6 PASS in normal mode and 6/6 PASS with
  Python `-O`, including the official MCP client listing/calling all four tools.
- Adapter/profile focused Java suite: 9/9 PASS.
- Full offline Maven reactor: 176 tests, zero failures, errors or skips;
  `BUILD SUCCESS`.
- Python domain suite: 8/8 PASS in normal mode and 8/8 PASS with `-O`.
- Metadata check and domain validator: PASS in normal and optimized modes.
- Portability literal audit found no configured operator account, R-number,
  sandbox account or Windows absolute path in the new client/profile code.
- No CK3 process was started or attached and no companion/P2 file was changed.
