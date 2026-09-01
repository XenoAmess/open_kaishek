# kaishek-ck3-11906-profile

This is the version-pinned CK3 1.19.0.6 profile in the independent
[XenoAmess/open_kaishek](https://github.com/XenoAmess/open_kaishek) repository.
The project source is licensed under [GPL-3.0-only](../LICENSE). CK3 itself,
its executable, script documentation and any mod installation remain external
inputs and are not redistributed here.

Build it from the repository root with the offline-friendly command:

```powershell
mvn -o -ntp -pl kaishek-ck3-11906-profile -am test
```

Build fingerprint, directory schema, scope links, and certified semantics are
kept separate from the generic core. `Ck3Profile11906` exposes both the
validator-facing schema view and an immutable `GameProfile`/`Profile`
projection for IR and differential tooling. The Phase 0 opcode table is
syntax-level only: no entry is runtime-certified until an exact-build
differential artifact exists. The profile is not valid for another executable
hash without a new profile. The current exact-build fingerprint is CK3
executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86` and is
also recorded in the project verification record. A profile fixture or ACK
must not be reported as `fixture-live`, `differential-certified`, or
`product-live`.
