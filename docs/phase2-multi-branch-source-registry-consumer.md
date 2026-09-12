# Phase-2 multi-branch source registry consumer boundary

Date: 2026-09-12 (Asia/Shanghai)

Root commit `823d7a4b9a9c862398acdecfe72062e7b4739eab` corrects the
root-owned P2 capture planner to consume the existing schema-3 multi-branch
source registry by its recomputed `lineage_set_id`. The planner now binds the
Incident checkpoint to the handler-specific lineage published by the registry;
it requires an initial-seed owner match only for the legacy single-lineage
schema.

The compatibility decision is `NO_PUBLIC_DELTA`. This change touches a private
root capture-plan consumer. It adds no CK3 query, MCP tool, Java payload,
native ABI, dependency, product data, or Operator MCP control. `open_kaishek`
does not copy the 4/4 source registry and remains independent of its runtime
paths and CK3 rounds.

The producing evidence is the root focused capture-plan suite in normal and
optimized Python plus a read-only preflight against the real schema-3 registry:
all checks are GREEN, including the recomputed lineage set, strict received-self
Incident receipt, handler-specific Incident source binding, and ACK exclusion.
No CK3 process was launched for this compatibility decision.
