# Standalone AF5 compensation snapshot

The CK3 1.19.0.6 companion exposes
`ck3_query_zhongguo_compensation_af5_snapshot_v1(request_nonce, expected_revision)`,
backed by `game.command.query-zhongguo-compensation-af5-snapshot-v1`.
`ZhongguoCompensationAf5SnapshotCapabilityProfile` records that additive public
contract. Its source schema is
`ck3_autonomous_player/schemas/zhongguo-compensation-af5-snapshot-v1.schema.json`
in the companion repository.
The schema SHA-256 is
`B8839712AE999E49C7B3EE5E20DBDA14C9DBD0BB8C0511CEA6F83FE903933EBB`.

The native backend identity is
`ck3-1.19.0.6-native-zhongguo-compensation-af5-snapshot-v1`. The public facade
uses `native-headless` and adds the `build`, `source` and `binding` envelopes.

The readback separates the portfolio, AF5 case, M299 receipt and M300 receipt.
`af5.case.result_case_serial` identifies the portfolio result; the AF5 internal
case serial remains separately available at `af5.case.identity.case_serial`.
Each script value retains its typed availability, value and unavailable reason.
The query binds the owner to the paused played character and observes the
portfolio subject. Its `readiness.ready` means the frame can be observed;
`terminal` separately reports the business outcome. Closing the portfolio may
remove its domain and stage while the AF5 receipts remain available. Neither
an active event nor the previous promotion receipt is required for this query.

The existing promotion/compensation postcondition descriptor remains available
for its original route. This new descriptor supplies static compatibility only;
the companion owns native reading, Python normalization and MCP transport.
There is no new Paradox opcode or finite-runtime implementation. CK3 event
scheduling and persisted native AF5 readback still require the companion's live
acceptance, so this profile remains uncertified.
