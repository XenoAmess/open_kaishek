# kaishek-runtime

Pure-Java Phase 3 kernel primitives. The module has no Quarkus or persistence
dependency. `ScopeContext` keeps typed ROOT/THIS/PREV references, `CaseRegistry`
and `DeadlineTicket` bind delayed work to a serial/owner/subject/revision, and
`ReceiptJournal` applies debits/refunds while enforcing conservation. Randomness
is consumed only from an explicit finite `DrawTape`; exhaustion is converted to
an `UNSUPPORTED` result by `RuntimeKernel`/`HookDispatcher`.

```java
var owner = new ScopeRef(ScopeType.CHARACTER, "c1");
var ctx = new ExecutionContext(new ScopeContext(owner, owner, null), 0,
                               DrawTape.of(3, 9));
```

Unregistered or uncertified opcodes and hooks never become silent no-ops. They
return `ExecutionStatus.UNSUPPORTED` with a trace entry. `ExecutionTrace` keeps
ordered operations plus read/write sets for differential replay.

## Scope-aware variable boundary

`ScopedVariableStore` is the finite-runtime variable boundary used by
`ExecutionContext`. Variables written on `ROOT`/`THIS`/iterator scopes are
keyed by the complete `ScopeRef`; they do not leak between the owner and
subject scopes used by the 361 phase-two fixtures. Global variables are a
separate namespace and must be accessed through the explicit `getGlobal`,
`setGlobal`, `changeGlobal`, and `removeGlobal` methods. `change` and
`changeGlobal` reject an unset variable rather than manufacturing zero, and
publish a numeric update only after overflow/type checks succeed. The immutable
`variableSnapshot()` is intended for deterministic fixture/replay adapters.

This boundary is a static/fixture contract derived from observed CK3
1.19.0.6 script behavior (including the `posted_serial = 0` initialization
pattern). It does not certify CK3 execution or promote the profile beyond
`static-ready`.
