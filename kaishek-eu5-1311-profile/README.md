# Kaishek EU5 1.3.11 / Build 24187685 profile

This module is a fail-closed static profile for the source slice used by
xenoamess.colonial_region_transfer 0.2.0. It binds the installed EU5 executable
and the relevant vanilla country-interaction/subject-type evidence, and
recognizes the candidate, Region, snapshot-list, Tusi-cap and bounded
country-event fixture shapes covered by its tests. Event-file details and
exact-build evidence are in `docs/eu5-1.3.11-event-grammar-slice.md`.

It does not execute EU5 effects or certify runtime behavior. Player-visible
interaction behavior, ownership mutation, save reload and Workshop acceptance
remain external Simplified Chinese EU5 tests.
