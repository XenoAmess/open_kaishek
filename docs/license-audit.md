# License audit and migration record

Date: 2026-09-01

The standalone project license is **GPL-3.0-only**. The complete GNU General
Public License version 3 text is in the repository root [LICENSE](../LICENSE),
and the Maven metadata points to the same license.

## Why the old Apache marker appeared

The parent repository's canonical master branch already contained a GPLv3 license
in commit ce010947805829ae24a7a4be4179758442389d42 (Add GNU GPL v3 license).
The Kaishek work was started on a divergent detached checkout
(bec853bb1a128d7f57583ab3152d2f6ec337ccfd), where that root file was not
present. More importantly, the initial Kaishek charter (commit
9c978b391c3ed59cfb961d980a804fbd75a48b5f) described Apache-2.0 as a
"preferred candidate", not as an approved decision. The implementation
incorrectly treated that candidate text as a final choice and created
open_kaishek/LICENSE and the parent POM metadata as Apache-2.0 in commit
dcbcba7120598cbaaa9c9d43ccc484a9bc82bb33.

That was an implementation error: the canonical project license should have
been checked before scaffolding a child project. It was not a requirement of
the CK3 project and it was not copied from a third-party source.

## Audit result

The audit covered tracked project files in the Kaishek tree and the canonical
parent branch:

* The only incorrect project-level Apache markers were the old Kaishek
  LICENSE, the parent POM <licenses> entry, and the historical charter/ADR
  statements. They are corrected in this repository.
* Apache-2.0 entries in THIRD_PARTY_LOCK describe Maven, Surefire, Jar, Shade,
  Clean, and other build artifacts. Those are legitimate licenses of build-time
  dependencies and must remain unchanged; they do not relicense this project
  and are not bundled runtime source.
* JUnit remains EPL-1.0 OR BSD-3-Clause in test scope. Referenced Chronicle,
  CWT, PDXTools, Jomini, and Tiger repositories are research inputs only; no
  third-party source was copied into this standalone tree.
* No Java or Python source file in the audited tree carried an Apache SPDX
  header. The standalone export contains no nested repository, gitlink, or CK3
  game/workshop files.

## Policy from this point

New authored code and project documentation are covered by GPL-3.0-only.
Third-party artifacts keep their upstream license and must be recorded with a
URL, fixed version, purpose, and attribution in THIRD_PARTY_LOCK before use.
The external mod_zhongguo_style corpus is not distributed by this repository;
its path and hash are recorded only as a reproducible validation input.
