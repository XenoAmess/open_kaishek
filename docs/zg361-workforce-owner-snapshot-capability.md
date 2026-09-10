# Played-owner Workforce snapshot

The CK3 1.19.0.6 companion exposes
`ck3_query_zhongguo_workforce_owner_snapshot_v1(request_nonce, expected_revision)`,
backed by `game.command.query-zhongguo-workforce-owner-snapshot-v1`.
`ZhongguoWorkforceOwnerSnapshotCapabilityProfile` records this additive public
contract and remains **static, native-uncertified and runtime-uncertified**.

The owner is the paused played character. The companion reads the actual
subject from the owner's Central binding, including a different character;
the caller supplies neither character ID and never needs to switch players.
An existing M360 source corroborates the Central and AL identities. Central
case serial and AL case serial remain separate namespaces.

The payload has five `workforce` groups: `central`, `source`, `al_case`,
`m360_receipt` and `portfolio`. The profile enumerates all 54 native/common
fields, treating each `{status, value, unavailable_reason}` script value as
one typed field. The public facade additionally requires `build`, `source`
and `binding`; its backend is `native-headless`, while the native payload uses
`ck3-1.19.0.6-native-zhongguo-workforce-owner-snapshot-v1`.

`readiness.ready` describes observability. `terminal_kind` separately reports
`none`, `success`, `history_accruing` or `not_applicable`; `terminal` identifies
the three closed branches. N/A requires explicit product terminal flags and
can be observable without an M360 source. Typed unavailability is neither N/A
nor a zero/false value. `central.stage11_status` exposes the later Central
callback independently from portfolio closure. This limited projection does
not certify full cohort conservation, a three-cycle history, M361 completion,
game scheduling or an autonomous loop.

The companion inputs are pinned as follows:

| Input | Path in the companion | SHA-256 |
|---|---|---|
| Schema | `ck3_autonomous_player/schemas/zhongguo-workforce-owner-snapshot-v1.schema.json` | `647C5252DFDC64D58F9FCF448DE8816B6B66EF6591485D7B7702ABD51A00A10C` |
| Synthetic fixture | `ck3_autonomous_player/tests/fixtures/zhongguo_workforce_owner_snapshot_v1.json` | `22774411C20DF0C0D65350183C54F4E1F5B9121CF720006DFE90F6EAB85AB66A` |

On 2026-09-11, the focused offline build
`mvn -o -q -pl kaishek-zg361-profile -am -DskipTests package` completed with
exit 0. A compiled Java descriptor check then confirmed both certification
flags are false, the two public arguments and four terminal kinds match,
and its 54 fields exactly equal the companion schema. Every declared field
was resolved against all four synthetic fixtures: pre-action, success,
history-accruing and explicit N/A without M360 source.

The local verification artifact is
`Z:/ck3_mod_rewrite/_runtime/d3-workforce-owner-t2sync-20260911/verification.json`.
This pass ran no full test suite and started no CK3 process. Native reading,
normalization, MCP transport and paused live acceptance remain the companion's
responsibility; there is no new Paradox opcode or finite-runtime implementation.
