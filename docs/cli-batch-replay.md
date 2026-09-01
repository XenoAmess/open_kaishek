# CLI batch/replay contract

`kaishek-cli batch` and its `replay` alias consume UTF-8 JSON Lines. The
driver is deliberately thin: every request is dispatched to an existing CLI
command, so it does not duplicate parser, validator, IR, scope, or Runtime
semantics. This makes it useful for CI and offline acceptance while the
pseudo-CK Runtime evolves independently.

## Request

Each non-empty, non-comment line is one flat object:

```json
{"id":"fixture","command":"synthetic-361"}
```

`id` and `command` are required. Input commands may use `text`, `file`, and
`profile`; alternatively pass a string-array `args` to preserve normal CLI
option ordering. `args` cannot be combined with those convenience fields.

## Response and exit code

One response envelope is emitted per request. Its `result` member is the
original command's JSON object (not an escaped string):

```json
{"id":"fixture","line":1,"exitCode":0,"result":{"status":"SUCCESS","fixture":"zg361-synthetic-014","synthetic":true}}
```

The process exits `0` when all commands succeed, `1` when a command returns a
non-zero code, and `2` when any line is malformed JSONL. Processing continues
after command failures by default; `--stop-on-error` stops after the first
non-zero command. Malformed lines are always reported with `status: ERROR`.

`synthetic-361` is the checked-in 014 parser → validator → strict IR → finite
`RuntimeKernel` fixture. It is explicitly marked `synthetic: true` and is not
evidence of CK3 native execution or a native replay scheduler. `replay` names
the deterministic manifest driver only.
