# CK3 steward Develop County task action compatibility

## Candidate public action

The G2 provider candidate adds:

- MCP tool `ck3_change_steward_develop_county_task_v1`;
- capability `game.command.change-steward-develop-county-task-v1`;
- transitional transport capability
  `game.contract.change-steward-develop-county-task-v1-fail-closed`;
- native step `change-steward-develop-county-task-v1`;
- schema version `1` on both acknowledgement and receipt records.

The MCP request contains `councillor_character_id`,
`target_county_title_id`, `expected_revision`, and
`replace_existing_task`. The task key is fixed internally to
`task_develop_county`; callers cannot supply another council task. Replacing a
different active task requires the explicit boolean request flag.

This candidate is static/fixture only. The production provider does not
advertise it, and its exact-build action ABI has not been certified. The
companion profile consequently marks the action stateful, nondeterministic,
native-uncertified, and runtime-uncertified.

The transitional transport capability exists only while the production action
is uncertified and unadvertised. It lets the typed executor return
`rejected_before_submit` through the closed ACK shape. It cannot submit the
native command. A `submitted_verification_pending` ACK attributed to this
transport is a RED contract violation at the service boundary; consumers must
not reinterpret it as degraded success.

## ACK is not an outcome

The immediate ACK has the exact fields recorded by
`StewardDevelopCountyTaskActionCapabilityProfile.ACK_FIELDS`. Its status is
only `submitted_verification_pending` or `rejected_before_submit`. A submitted
ACK sets `verification_pending=true`; a pre-submit rejection sets it false.
The ACK field set deliberately excludes `applied` and `success`.

The closed `failure_class` vocabulary is:

- `request_contract`;
- `snapshot_binding`;
- `councillor_binding`;
- `task_or_target_legality`;
- `native_command_dispatch`.

`native_reason_key` preserves an exact native reason when one exists. Neither
a request ID nor `submitted_verification_pending` proves that the task changed
or that development progress began.

## Independent receipt

An outcome requires a separate receipt derived from a newer paused snapshot.
The receipt status is `applied`, `rejected`, or `postcondition_failed`. It
records both post revisions and date, the observed councillor, active task,
county title and province target, progress kind/current/max/frozen state, and
`postcondition_verified`.

`applied` is valid only when the fresh observation proves the requested
`task_develop_county` binding and target with
`postcondition_verified=true`. Dispatch followed by a missing or conflicting
postcondition remains `postcondition_failed`; it cannot be promoted from ACK
contents. This receipt proves task placement, not a future increase in county
development.

## Companion impact

At baseline `7652f8f49a8e73429d41581441067e37d3799747`, a repository-wide
non-documentation search finds no exact tool, capability or step identity; no
caller, ACK/receipt parser, closed Java response type, endpoint, or MCP
forwarding layer exists in `open_kaishek`. This package therefore adds a
version-pinned static capability profile and tests without adding a runtime
adapter, endpoint, dependency, or version change.

The compatibility verdict is
`ROOT_PUBLIC_ACTION_CANDIDATE_ADDED / OPEN_KAISHEK_STATIC_PROFILE_ADDED /
RUNTIME_CONSUMER_ABSENT`. It adds no CK3 process authority to `open_kaishek`.

## Pairing status and reopen conditions

The static contract is paired to landed upstream main commit
`bf5a4032440eae5651dc4c5c481c24b4be27cff1`. Its formal action capability,
fail-closed transport capability, MCP parameters, ACK, receipt, status, and
failure-class sets match this profile. The current pairing status is
`paired_to_upstream_main`; production remains unadvertised and the command ABI
remains uncertified.

Reopen this boundary if production advertises the capability, exact-build ABI
or paused live evidence changes either certification flag, the ACK or receipt
shape changes, or `open_kaishek` gains a runtime consumer. The profile contains
no machine path, account, CK3 process, or round identifier.
