# Phase 0 corpus manifest

`corpus-manifest.json` is the reproducible Phase 0 inventory for the
`ck3-1.19.0.6/mod_zhongguo_style` profile. It stores only relative paths,
byte counts, SHA-256 hashes, and a lightweight syntax census. The CK3/mod
source tree is an external, read-only input and is deliberately not copied
into this standalone repository; its own distribution and license terms remain
outside this project. The toolchain itself is GPL-3.0-only (see `../LICENSE`).

From the standalone repository root (for example,
`Z:\workspace\open_kaishek`):

```powershell
$corpus = 'Z:\ck3_mod_rewrite\mod_zhongguo_style'  # external local corpus; replace as needed
py -3 tools/generate_corpus_manifest.py `
  --root $corpus --output docs/corpus-manifest.json
```

The checked-in manifest was generated from the external corpus at the path
above. A clone on another machine must supply its own exact-build corpus path;
the relative paths and hashes in the manifest are the comparison contract, not
an embedded copy of CK3 files.

The inventory includes all `.txt` and `.gui` files, sorted by POSIX relative
path. Each entry records `sha256`, size, line/comment counts, BOM presence,
construct counts, and preliminary assignment/block frequencies. Frequencies
are case-folded identifier counts after line-comment removal; they are useful
for parser-spike sampling only, not a semantic opcode registry. Profile
validation remains authoritative for supported semantics.

Regenerate and review path/hash diffs whenever the external corpus bytes change,
and record the resulting manifest hash in later Phase 0/1 test artifacts. If the
corpus is unavailable, record the check as `SKIP` rather than treating an empty
or absent directory as a passing inventory.
