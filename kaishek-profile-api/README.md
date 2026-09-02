# kaishek-profile-api

Small, framework-neutral contracts for game/build profiles, scope types,
opcode descriptors, schema domains, and unsupported classifications. Concrete
CK3 and 361 profiles implement these contracts in downstream modules; the
validator has no dependency on a particular game profile.

`CapabilityDescriptor` is the immutable API projection for a higher-level,
read-only product query. It records required fields, invariants, exact profile
version, and separate native/runtime certification bits. Declaring a
capability does not register an opcode or certify an implementation.
