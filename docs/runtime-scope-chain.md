# Runtime scope-chain contract

`ScopeContext` is the executable boundary for parser/IR scope references. It
recognizes the native roots `ROOT`, `THIS`, and `PREV`, plus saved aliases. A
`scope:` prefix is accepted on the first segment, so `scope:actor.liege` and
`actor.liege` use the same alias. Every following segment must be present in
the explicit link table; ids and scope types are never guessed.

Resolution is immutable and fail-closed. `resolve(String)` returns `null` for
an unresolved reference. `resolveDetailed(String)` returns a stable reason
code and the traversed path for CLI/MCP serialization. The machine-readable
fixture is [runtime-scope-chain-contract.json](runtime-scope-chain-contract.json).

The synthetic contract test covers ROOT/THIS/PREV, aliases, a two-hop chain,
the `scope:` prefix, missing bases/links, cycles, and immutable updates. This
is an offline runtime fixture; it is not evidence of a live CK3 scope graph.
