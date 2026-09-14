# CK3 steward Develop County candidate compatibility

## Candidate boundary

`open_kaishek` freezes the candidate public query supplied by the G2 provider:

- MCP tool: `ck3_query_steward_develop_county_candidates_v1(expected_revision)`;
- capability: `game.command.query-steward-develop-county-candidates-v1`;
- native step: `query-steward-develop-county-candidates-v1`;
- schema version: `1`;
- contract stage: `exact_build_contract_fixture_pending_live_reader`;
- backend: `ck3-1.19.0.6-native-steward-develop-county-candidates-v1`;
- exact build: CK3 `1.19.0.6`, executable SHA-256
  `2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.

The Java capability profile pins the one-field request, command envelope,
complete payload and candidate rows, provenance, MCP response additions, and
typed unavailable vocabulary. The contract admits an available payload only
after complete two-sample equality on one paused native frame. Candidate rows
contain native-legal counties; `target_selection_mode=engine_random_unscored`
records that the original native AI task does not provide a target score.
The command envelope uses `backend_id=native-headless`; the nested provenance
uses the exact provider backend shown above. In an unavailable payload,
`observed_date_raw` may be `null` or the bound paused-frame date, while all
other observations remain null or empty.

The current production reader remains fail-closed with
`reader_not_implemented`. An offline contract fixture may exercise an
available response, but that fixture does not establish native or runtime
certification. Accordingly the companion descriptor is read-only and
deterministic while both certification flags remain false. The query grants no
authority to start the steward task or select a county.

## Consumer and version impact

Before adding the static profile, a repository-wide non-documentation search
found no exact tool/capability/step identity, caller, response parser, closed
Java record, endpoint, or MCP forwarding layer in `open_kaishek`. No existing
runtime consumer therefore requires an adapter. This package adds the contract
to the version-pinned CK3 profile without changing Operator MCP envelopes,
dependencies, endpoints, or the repository version.

The compatibility verdict is
`ROOT_PUBLIC_CANDIDATE_QUERY_ADDED / OPEN_KAISHEK_STATIC_PROFILE_ADDED /
RUNTIME_CONSUMER_ABSENT`. The static profile is portable: it contains no
machine path, account, CK3 process, or round identifier.

## Pairing status and reopen conditions

The profile is paired to landed upstream main commit
`f0f10bf1863519881d9bd88c64a8ace4ec6abc41`. Its command identity, exact field
lists, backend split, unavailable vocabulary and date boundary match that
provider. The current pairing status is `paired_to_upstream_main`.

Reopen this boundary when the exact-build reader lands, a live paused artifact
changes either certification flag, the public v1 field set or unavailable
vocabulary changes, or `open_kaishek` gains a runtime consumer for this query.
