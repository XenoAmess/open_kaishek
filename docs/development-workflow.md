# Development workflow

`main` is the only integration line for this repository.  Start ordinary
work from the latest `origin/main`; do not create a branch merely to park a
patch or evidence.

Use a short-lived `wip/<topic>` branch only when a real concurrent change,
isolated live experiment, or other concrete boundary requires it.  Record the
reason and base commit, run the smallest relevant checks, and merge the
finished change into `main` as soon as it is ready.  Push the resulting main
commit, then delete the local and remote temporary branch.  Detached evidence
worktrees and build artifacts may be retained, but they must not leave a
long-lived branch ref.

This mainline-first rule also applies to changes made while this repository is
used as the offline preflight/runtime accelerator for the companion CK3
project: a temporary branch is a delivery aid, not a second product line.
