# Zhongguo B3 manager-governance capability boundary

Date: 2026-09-04 (Asia/Shanghai)

The companion project now exposes the fixed read-only capability
`game.command.query-zhongguo-manager-governance-snapshot-v1` through its
native mailbox, driver, service, and MCP layers.  The open_kaishek
`ZhongguoManagerGovernanceCapabilityProfile` records that public identity,
the minimum product fields, and its cross-field invariants as an immutable
`CapabilityDescriptor`.

The companion driver step is
`query-zhongguo-manager-governance-snapshot-v1`; its fixed case kind is
`zhongguo.manager-governance`.

This is a static compatibility projection.  It does not execute the MCP
query, validate arbitrary JSON instances, or certify CK3 behavior.  Both
`nativeCertified` and `runtimeCertified` remain `false` until a production
paused artifact proves the B3 provider and result choreography.

## Hash-bound companion inputs

| companion input | identity |
| --- | --- |
| transport integration commit | `fc8be4aa4a06c5234747b01fcc188f2f7239961e` |
| purpose-split/ABI commit | `4890b17998df1c5586beb36011d283c1a111f388` |
| public JSON schema SHA-256 | `1487d9dc129baa31a9f2990466fbbb6e935caafdcf1fb2f16358a6b85c68f072` |
| Python contract SHA-256 | `c7f07a53c4752cc11e859e47176124e092a791b8d87880ee3882ac88f31e767a` |
| native ABI ledger SHA-256 | `697994d3c3d798aaeebabd63e891cf7007e67f5cef0c4b0f14405ac7ec646328` |
| exact game build | CK3 `1.19.0.6` / EXE `2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86` |

The descriptor preserves the product's bounded-subject policy: an AI subject
requires the typed direct-manager dependency, the caller cannot assert its
own eligibility, and the interface is not an arbitrary character-variable
reader.  It also records the same-frame double-read, F035 distribution, and
F032 component-8 receipt invariants.

## Parser, validator, IR, runtime, and CLI decision

No parser, opcode profile, IR handler, runtime handler, or CLI command changes
are required for this increment.  The new command is a native/MCP data-plane
capability, not Paradox script syntax.  The companion effect change only splits
43 existing effects into seven purpose files with byte-identical blocks, while
the loader-liveness classifier is runner-only.  Neither is an API or language
change.

The existing full-corpus validator RED remains a bounded vocabulary-coverage
result.  This capability descriptor must not be used to suppress those
diagnostics or promote a parser success to runtime certification.

## Offline verification

From the open_kaishek repository root:

```powershell
mvn -o -ntp -pl kaishek-zg361-profile -am test
java -jar kaishek-cli/target/kaishek-cli-0.1.0-SNAPSHOT.jar corpus --require-corpus <product-root>
```

These checks never launch CK3 or mutate a save.
