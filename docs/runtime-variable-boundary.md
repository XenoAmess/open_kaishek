# Runtime variable boundary (offline fixture contract)

The phase-two CK3 scripts use the same variable names on many different
characters while iterating owner, subject, and manager scopes. The finite
runtime therefore keys a variable by the complete `ScopeRef` instead of one
process-wide string map. `ScopedVariableStore` exposes the smallest reusable
contract:

* `set/get/has/remove(scope, name)` address one explicit scope;
* `setGlobal/getGlobal/hasGlobal/removeGlobal` address the separate global
  namespace;
* `change(scope, name, delta)` and `changeGlobal(name, delta)` require an
  existing numeric value and fail with a typed exception when it is absent or
  not numeric;
* arithmetic is checked before publication, so overflow and invalid values do
  not leave a partial write;
* `snapshot()` returns immutable, deterministically ordered maps for replay or
  differential-fixture adapters.

This mirrors two observed 1.19.0.6 rules used by the Tianchao phase-two
fixtures: a `change_variable` cannot initialize an unset value, and a new case
must initialize `posted_serial = 0` before changing it. The Java tests exercise
those rules with separate owner/subject scopes and a shared global cycle.

The implementation is intentionally finite and profile-neutral. It does not
evaluate arbitrary Paradox expressions, infer scope links, or claim that the
CK3 game and VM are equivalent. The evidence level remains offline
`static-ready`/`runtime-fixture`; a real paused CK3 post-state is still required
for `differential-certified` or `product-live`.
