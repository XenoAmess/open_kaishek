# G2 truce evaluated-days capability boundary

Date: 2026-09-04 (Asia/Shanghai)

The public capability remains
`game.command.query-g2-truce-evaluated-days-v1` with profile
`ck3-1.19.0.6-g2-truce-evaluator-v1`. Its eight required fields and five
invariants are unchanged: two evaluator reads must agree on one paused frame,
an observed duration is non-negative, and persisted expiry is never inferred
from that duration.

## Provider transition

Companion commit `a3c13246ef32b35e117b08dbb86f61986c1dabe3`
moves the privately proven synchronous leaf-context reader into the default
production build. The installed exact-build preview-entry hook supplies the
transient leaf wrapper and verifies that it is the expected CAddTruce effect
before evaluating duration twice. All private capture/diagnostic build options
remain off in the frozen production candidate at
`0b0fbc047610a8ef25f47a59f7b42c83c176d69e`.

This is a provider/readiness change, not a public schema change. After the
private leaf-context proof, the default production binary completed a paused,
read-only public-wire acceptance. Two consecutive queries (`query_sequence`
1 and 2) returned the same normalized `1825`-day result on snapshot revision
4/native revision 3 at `date_raw = 53223936`. The duration observation
primitive is therefore `nativeCertified`, `runtimeCertified`, and `certified`.

This certification is deliberately narrow. `expiry_observable`, termination
action, full-decision readiness, automatic surrender, and GEN-034 closure all
remain false.

## Production-live evidence

The external artifact is
`Z:\ck3_mod_rewrite_process_assets\zg361\g2-production-leaf-1941c56-20260904\live-production-leaf-dual-query-r1`.
Its 151.766-second report is GREEN, records no time advance or mutation
command, preserves the source checkpoint and driver-state hashes, and proves
process-tree cleanup with CK3 absent after the run.

| live input or artifact | SHA-256 |
| --- | --- |
| report | `ad6eef83dcca07c3ae280f01cade6bbd0c1912ff0e086d797604d5f06c99f7c2` |
| production tree | `f4e63fffa6cf9332ba41eb5985d1cb72f280f4bf375a15473f4638f43cf944be` |
| bridge DLL | `1acc24db476a7b1ecb4f0a98ef2e9a74d0e932cb74f5884622530d77246e3244` |
| bridge injector | `03ed1ee07ac58e1e6f7adde31518c732c1d60cdbffc3b50938d7e1cf84c877c5` |

## Hash-bound companion inputs

| companion input | SHA-256 |
| --- | --- |
| source contract | `df720cd33d3606634378a5cff20d77227b82a35265269789bde4a51cff988e0d` |
| production candidate manifest | `6b5783bca00a1b082aa5fec834ee73a95860535549b7525a616c82f178265c58` |
| provider source | `5299c88f4cd7b27959e4518d5a48061ae0ef39ae629a2590c269a8fe912f397a` |
| provider header | `e49d31f35fbb3f5bc713ea94cb9ff3e83ec9fa713772968a0dcffefd20200b2a` |

The exact game binding remains CK3 `1.19.0.6`, executable SHA-256
`2D00FF3101EF70B566F2FCBAE292F09263199C80E9DC8F139B82D7D96F83DB86`.
No parser, validator vocabulary, IR/runtime handler, or CLI command changes
are required for this native provider transition.

## 2026-09-11 current-input contradiction and bounded diagnostic

Root R450 reached a different real `raiktor_claim_cb` input, WarID `33554473`,
and queried the public terms wire twice on the same paused frame. Both calls
returned the other term domains but reported
`truce.evaluated_days_observable=false`. This does not erase the earlier
production-live proof that the primitive can return 1,825 days; it proves that
the provider is not ready for every current input. Full terms, decision,
action, automatic surrender, and GEN-034 therefore remain unavailable.

Root commit `ad3404d268c6a2a86d80ee2cb2b73ae5c36213a1` adds an explicitly
selected read-only pre-termination probe and a default-OFF diagnostic build.
The probe reuses the natural-source and same-PID query path, then requires zero
checkpoint creation, zero mutation commands, and zero postwar polling. Its
`PROBE_COMPLETE` status records execution completion separately from
`terms_ready`; it cannot certify the capability by itself.

The diagnostic CMake option
`XAR_CK3_ENABLE_G2_TRUCE_DEFAULT_LEAF_DIAGNOSTICS_V1` adds only failure-stage,
callback, enum-count, and context telemetry around the unchanged default leaf
reader. It is mutually exclusive with the earlier private capture modes and
does not change the public bridge ABI. The root live-adapter manifest and
no-launch receipt SHA-256 values are
`7C66EAAA51AEAD8E020CB8E3D41D3F8794DB4BFF8742C6E182ADA6B7E95733E8`
and `E10DA2DBF2DDD3F7B427972223F4162BA886875F1445F47C4320BD375FC6B56D`.
The diagnostic DLL and injector SHA-256 values are
`AB1BF87A1C4C20BC488F857BA0454E070225F0027259DDC6282530D92AAB1705`
and `B5353527B56B4C99A7AF11A09C59A0850C481A45ED7CDA4C519AF956D97B39EC`.
No open_kaishek parser, runtime, MCP envelope, Java descriptor, or command is
changed by this private diagnostic dependency.

### R451 pause-confirmation runner repair

R451 reached the natural source with six valid rows and detached its debugger,
then stopped before bridge attach because a top-center in-game notification
occluded the transient pause OCR marker. It produced no default-reader row and
made no mutation. Root commit
`d486e7066ff65e1d3c8abeeb337346bf3f0e2f48` adds one narrow fallback after
the pause click: the HUD date must be readable and unchanged across three
seconds. An unreadable or advancing date still fails. The next no-launch
receipt is SHA-256
`F89EE9D57A34038B822B74E2B75DD1F2BE9CCF1ED4798B7E636C78C033F02CCF`.
This changes only private orchestration and does not alter this repository's
public capability descriptor or its existing readiness limits.
