# Stellaris 4.4.6 habitat district-set trigger slice

## Scope

The Infinite Jobs companion mod needs a deposit-side
`triggered_planet_modifier` whose `potential` contains:

```text
uses_district_set = habitat
```

The local Stellaris 4.4.6 source uses this exact trigger in
`common/districts/03_habitat_districts.txt` for the housing, energy, mining,
and science habitat districts. The inspected file SHA-256 is
`466BF13CFA3574059D7AB675511C2A63091C278DA76B43AD84C33233E77012CBD`.
This addition expands only the version-pinned
static schema slice. It does not certify runtime semantics and does not start
Stellaris.

## Profile design

- Register `uses_district_set` as a trigger with zero structured parameters.
- Allow the observed carrier scopes `PLANET/planet` and `SHIP/ship`. The ship
  scope is required because Stellaris 4.4.6 carrier-backed colonies can route
  planet-style decisions and deposits through ship scope; accepting the scope
  does not assert that a particular non-habitat ship satisfies the trigger.
- Keep `triggered_planet_modifier` structural so its `potential` remains
  recursively fail-closed while its `modifier` payload remains an opaque map
  of dynamic modifier identifiers.
- Do not register a wildcard district-set trigger or any runtime behavior.

## Tests and acceptance

1. A minimal deposit containing the exact habitat potential validates without
   an error.
2. The registered descriptor is a trigger and exposes only planet/ship carrier
   scopes.
3. `uses_district_sets` remains `UNKNOWN_OPCODE`.
4. An explicit country scope remains `INVALID_SCOPE`.
5. Maven tests, packaged CLI smoke, deterministic packaging, whitespace, and
   remote `core-ci` all pass before the companion mod consumes the profile.
