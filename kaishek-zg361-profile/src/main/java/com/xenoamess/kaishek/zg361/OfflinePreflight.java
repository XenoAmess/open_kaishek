package com.xenoamess.kaishek.zg361;

import com.xenoamess.kaishek.ir.IrProgram;
import com.xenoamess.kaishek.profile.Ck3Profile11906;
import com.xenoamess.kaishek.profile.KaishekProfile;
import com.xenoamess.kaishek.runtime.ExecutionStatus;
import com.xenoamess.kaishek.syntax.ParseResult;
import com.xenoamess.kaishek.syntax.Parser;
import com.xenoamess.kaishek.validator.Diagnostic;
import com.xenoamess.kaishek.validator.Validator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Deterministic, no-launch preflight for CK3 source slices.
 *
 * <p>The preflight deliberately stops at the boundary of this repository:
 * it parses and validates an optional source root, then runs the checked-in
 * synthetic 361 fixture.  It never starts CK3, loads a DLL, opens MCP, or
 * mutates a save.  A report is therefore useful as an early acceptance gate,
 * but it is never evidence of native/live semantics.</p>
 */
public final class OfflinePreflight {
    public static final String SCHEMA = "open_kaishek.preflight.v1";
    public static final String TOOL_VERSION = "0.1.0-cli";
    public static final String DEFAULT_PROFILE = "ck3-1.19.0.6-zg361";
    public static final String DEFAULT_FIXTURE = "synthetic-361-014";
    private static final int SAMPLE_LIMIT = 8;

    private OfflinePreflight() { }

    /** Immutable command input shared by CLI and future service adapters. */
    public record Request(Path root, String profileId, String fixtureId) {
        public Request {
            profileId = profileId == null || profileId.isBlank()
                    ? DEFAULT_PROFILE : profileId;
            fixtureId = fixtureId == null || fixtureId.isBlank()
                    ? DEFAULT_FIXTURE : fixtureId;
            if (root != null) root = root.toAbsolutePath().normalize();
        }

        public static Request defaults() {
            return new Request(null, DEFAULT_PROFILE, DEFAULT_FIXTURE);
        }
    }

    /** Machine-readable result.  {@link #toJson()} is stable JSONL output. */
    public record Report(
            String status,
            String toolVersion,
            String profileId,
            String buildFingerprint,
            String fixtureId,
            Stage parser,
            Stage validator,
            Stage ir,
            Stage runtime,
            Stage rootParser,
            Stage rootValidator,
            Map<String, String> provenance) {
        public Report {
            status = requireText(status, "status");
            toolVersion = requireText(toolVersion, "toolVersion");
            profileId = requireText(profileId, "profileId");
            buildFingerprint = requireText(buildFingerprint, "buildFingerprint");
            fixtureId = requireText(fixtureId, "fixtureId");
            parser = Objects.requireNonNull(parser, "parser");
            validator = Objects.requireNonNull(validator, "validator");
            ir = Objects.requireNonNull(ir, "ir");
            runtime = Objects.requireNonNull(runtime, "runtime");
            rootParser = Objects.requireNonNull(rootParser, "rootParser");
            rootValidator = Objects.requireNonNull(rootValidator, "rootValidator");
            provenance = Map.copyOf(Objects.requireNonNull(provenance, "provenance"));
        }

        /** Emit one object with no dependency on a JSON library. */
        public String toJson() {
            StringBuilder out = new StringBuilder("{");
            field(out, "schema", SCHEMA);
            field(out, "status", status);
            field(out, "tool_version", toolVersion);
            field(out, "profile_id", profileId);
            fieldRaw(out, "build_fingerprint", buildFingerprint);
            field(out, "fixture_id", fixtureId);
            fieldRaw(out, "parser", parser.toJson());
            fieldRaw(out, "validator", validator.toJson());
            fieldRaw(out, "ir", ir.toJson());
            fieldRaw(out, "runtime", runtime.toJson());
            fieldRaw(out, "root_scan", "{\"parser\":" + rootParser.toJson()
                    + ",\"validator\":" + rootValidator.toJson() + "}");
            fieldRaw(out, "provenance", mapJson(provenance));
            return out.append('}').toString();
        }
    }

    /** One stage in the report; samples are intentionally bounded. */
    public record Stage(String status, int files, long bytes, int diagnostics,
                        String reason, String sha256, List<String> samples) {
        public Stage {
            status = requireText(status, "stage status");
            if (files < 0 || bytes < 0 || diagnostics < 0) {
                throw new IllegalArgumentException("stage counters cannot be negative");
            }
            reason = reason == null ? "" : reason;
            sha256 = sha256 == null ? "" : sha256;
            samples = List.copyOf(Objects.requireNonNull(samples, "samples"));
        }

        private String toJson() {
            StringBuilder out = new StringBuilder("{");
            field(out, "status", status);
            fieldRaw(out, "files", Integer.toString(files));
            fieldRaw(out, "bytes", Long.toString(bytes));
            fieldRaw(out, "diagnostics", Integer.toString(diagnostics));
            if (!reason.isBlank()) field(out, "reason", reason);
            if (!sha256.isBlank()) field(out, "sha256", sha256);
            fieldRaw(out, "samples", stringListJson(samples));
            return out.append('}').toString();
        }
    }

    /** Execute a parser/validator/fixture preflight without side effects. */
    public static Report run(Request request) throws IOException {
        Objects.requireNonNull(request, "request");
        ProfileSelection profile;
        try {
            profile = selectProfile(request.profileId());
        } catch (IllegalArgumentException exception) {
            // Keep even an unsupported selection inside the stable report
            // contract.  A parent runner can archive one JSON shape and use
            // exit code 4 without parsing the CLI's generic error envelope.
            return unsupportedReport(request, exception.getMessage());
        }
        RootResult root = scanRoot(request.root(), profile.validatorProfile());
        FixtureResult fixture = runFixture(request.fixtureId());

        Stage parser = combine(root.parser(), fixture.parser());
        Stage validator = combine(root.validator(), fixture.validator());
        Stage ir = fixture.ir();
        Stage runtime = fixture.runtime();
        String status = overallStatus(parser, validator, ir, runtime);

        Map<String, String> provenance = new LinkedHashMap<>();
        provenance.put("mode", "offline");
        provenance.put("root", request.root() == null ? "" : request.root().toString());
        provenance.put("root_sha256", root.parser().sha256());
        provenance.put("fixture_scope", fixture.scope());
        provenance.put("ck3_started", "false");
        provenance.put("save_mutated", "false");
        provenance.put("network_used", "false");
        provenance.put("source", "open_kaishek");
        return new Report(status, TOOL_VERSION, request.profileId(),
                profile.buildFingerprint(), request.fixtureId(), parser, validator,
                ir, runtime, root.parser(), root.validator(), provenance);
    }

    private static Report unsupportedReport(Request request, String reason) {
        Stage unsupported = stage("UNSUPPORTED", 0, 0, 1,
                reason == null || reason.isBlank() ? "unsupported-selection" : reason,
                "", List.of(request.profileId()));
        Stage rootSkipped = stage("SKIPPED", 0, 0, 0,
                "profile-not-registered", "", List.of());
        Map<String, String> provenance = new LinkedHashMap<>();
        provenance.put("mode", "offline");
        provenance.put("root", request.root() == null ? "" : request.root().toString());
        provenance.put("root_sha256", "");
        provenance.put("fixture_scope", "unsupported");
        provenance.put("ck3_started", "false");
        provenance.put("save_mutated", "false");
        provenance.put("network_used", "false");
        provenance.put("source", "open_kaishek");
        return new Report("UNSUPPORTED", TOOL_VERSION, request.profileId(),
                "{\"unsupported\":true}", request.fixtureId(), unsupported, unsupported,
                unsupported, unsupported, rootSkipped, rootSkipped, provenance);
    }

    private static ProfileSelection selectProfile(String id) {
        if (id.equals(Ck3Profile11906.ID) || id.equals("ck3-1.19.0.6-zg361")) {
            Ck3Profile11906 profile = new Ck3Profile11906();
            return new ProfileSelection(profile, "{\"game_id\":\"ck3\",\"version\":\""
                    + Ck3Profile11906.GAME_VERSION + "\",\"exe_sha256\":\""
                    + Ck3Profile11906.EXE_SHA256 + "\"}");
        }
        if (id.equals(Synthetic361Profile.ID)) {
            Synthetic361Profile profile = new Synthetic361Profile();
            return new ProfileSelection(profile, "{\"game_id\":\"zg361-synthetic\",\"version\":\""
                    + Synthetic361Profile.GAME_VERSION + "\",\"exe_sha256\":\""
                    + Synthetic361Profile.SYNTHETIC_EXE_SHA256 + "\"}");
        }
        throw new IllegalArgumentException("unsupported profile: " + id);
    }

    private static RootResult scanRoot(Path root, KaishekProfile profile) throws IOException {
        if (root == null) {
            Stage skipped = stage("SKIPPED", 0, 0, 0, "root-not-supplied", "", List.of());
            return new RootResult(skipped, skipped);
        }
        if (!Files.exists(root)) {
            Stage missing = stage("RED", 0, 0, 1, "root-not-found", "", List.of(root.toString()));
            return new RootResult(missing, missing);
        }
        List<Path> files = sourceFiles(root);
        if (files.isEmpty()) {
            Stage empty = stage("RED", 0, 0, 1, "no-supported-source-files", "", List.of(root.toString()));
            return new RootResult(empty, empty);
        }

        MessageDigest digest = sha256();
        long bytes = 0;
        int parserDiagnostics = 0;
        int validatorDiagnostics = 0;
        List<String> parserSamples = new ArrayList<>();
        List<String> validatorSamples = new ArrayList<>();
        for (Path file : files) {
            byte[] source = Files.readAllBytes(file);
            bytes += source.length;
            String relative = rootIsFile(root) ? file.getFileName().toString()
                    : root.relativize(file).toString().replace('\\', '/');
            digest.update(relative.getBytes(StandardCharsets.UTF_8));
            digest.update((byte) 0);
            digest.update(source);
            ParseResult parsed = Parser.parse(source);
            parserDiagnostics += parsed.diagnostics().size();
            addSamples(parserSamples, relative, parsed.diagnostics());
            List<Diagnostic> validation = Validator.validate(parsed, file.toString(), profile);
            validatorDiagnostics += validation.size();
            addSamples(validatorSamples, relative, validation);
        }
        String hash = hex(digest.digest());
        String parserStatus = parserDiagnostics == 0 ? "GREEN" : "RED";
        String validatorStatus = validatorDiagnostics == 0 ? "GREEN" : "RED";
        return new RootResult(
                stage(parserStatus, files.size(), bytes, parserDiagnostics, "", hash, parserSamples),
                stage(validatorStatus, files.size(), bytes, validatorDiagnostics, "", hash, validatorSamples));
    }

    private static List<Path> sourceFiles(Path root) throws IOException {
        if (rootIsFile(root)) return isScript(root) ? List.of(root) : List.of();
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(Files::isRegularFile)
                    .filter(OfflinePreflight::isScript)
                    .sorted(Comparator.comparing(path -> root.relativize(path).toString()
                            .replace('\\', '/')))
                    .toList();
        }
    }

    private static FixtureResult runFixture(String requested) {
        String id = requested == null || requested.isBlank() ? DEFAULT_FIXTURE : requested;
        if (id.equals("none")) {
            Stage skipped = stage("SKIPPED", 0, 0, 0, "fixture-not-requested", "", List.of());
            return new FixtureResult(skipped, skipped, skipped, skipped, "none");
        }
        if (id.equals("synthetic") || id.equals("zg361-synthetic-014")
                || id.equals("synthetic-361-014")) {
            Synthetic361Pipeline.Result result = Synthetic361Pipeline.runGenerated();
            int parserDiagnostics = result.parsed().diagnostics().size();
            int validationDiagnostics = result.validation().size();
            int irDiagnostics = result.program().diagnostics().size();
            String parserStatus = parserDiagnostics == 0 ? "GREEN" : "RED";
            String validatorStatus = result.validationPassed() ? "GREEN" : "RED";
            String irStatus = result.program().executable() ? "GREEN" : "RED";
            String runtimeStatus = result.execution().status() == ExecutionStatus.SUCCESS
                    ? "GREEN" : "RED";
            String hash = hex(sha256().digest(result.source()));
            return new FixtureResult(
                    stage(parserStatus, 1, result.source().length, parserDiagnostics, "", hash,
                            diagnosticSamples(result.parsed().diagnostics())),
                    stage(validatorStatus, 1, result.source().length, validationDiagnostics, "", hash,
                            diagnosticSamples(result.validation())),
                    stage(irStatus, 1, result.source().length, irDiagnostics,
                            result.program().executable() ? "" : "strict-ir-not-executable", hash,
                            irSamples(result.program())),
                    stage(runtimeStatus, 1, result.source().length,
                            result.execution().isSuccess() ? 0 : 1,
                            result.execution().isSuccess() ? "" : result.execution().reason(), hash,
                            List.of("execution=" + result.execution().status())),
                    "synthetic-only");
        }
        if (id.equals(Appeal014DifferentialFixture.FIXTURE_ID) || id.equals("appeal-014")) {
            List<String> mismatches = new ArrayList<>();
            for (Appeal014DifferentialFixture.CaseFixture fixture :
                    Appeal014DifferentialFixture.cases()) {
                Appeal014DifferentialFixture.Comparison comparison =
                        Appeal014DifferentialFixture.compare(fixture);
                if (!comparison.match()) mismatches.addAll(comparison.mismatches());
            }
            Stage parser = stage("SKIPPED", 0, 0, 0, "appeal-replay-has-no-script-input", "", List.of());
            Stage validator = parser;
            Stage ir = parser;
            Stage runtime = stage(mismatches.isEmpty() ? "GREEN" : "RED",
                    Appeal014DifferentialFixture.cases().size(), 0, mismatches.size(),
                    mismatches.isEmpty() ? "" : "differential-vector-mismatch", "",
                    bounded(mismatches));
            return new FixtureResult(parser, validator, ir, runtime, "runtime-fixture");
        }
        Stage error = stage("RED", 0, 0, 1, "unknown-fixture", "", List.of(id));
        return new FixtureResult(error, error, error, error, "unsupported");
    }

    private static Stage combine(Stage left, Stage right) {
        String status = combineStatus(left.status(), right.status());
        // A skipped optional root must not leak its explanatory reason into a
        // GREEN fixture-only aggregate.  Preserve a reason only when the
        // aggregate is not GREEN, and prefer a real root hash when present.
        String reason = status.equals("GREEN") ? ""
                : (!left.reason().isBlank() ? left.reason() : right.reason());
        String hash = !left.sha256().isBlank() ? left.sha256() : right.sha256();
        List<String> samples = new ArrayList<>(left.samples());
        for (String sample : right.samples()) if (samples.size() < SAMPLE_LIMIT) samples.add(sample);
        return stage(status, left.files() + right.files(), left.bytes() + right.bytes(),
                left.diagnostics() + right.diagnostics(), reason, hash, samples);
    }

    private static String combineStatus(String left, String right) {
        if (left.equals("RED") || right.equals("RED")) return "RED";
        if (left.equals("UNSUPPORTED") || right.equals("UNSUPPORTED")) return "UNSUPPORTED";
        if (left.equals("GREEN") || right.equals("GREEN")) return "GREEN";
        return "SKIPPED";
    }

    private static String overallStatus(Stage... stages) {
        boolean anyGreen = false;
        for (Stage stage : stages) {
            if (stage.status().equals("RED") || stage.status().equals("UNSUPPORTED")) return "RED";
            if (stage.status().equals("GREEN")) anyGreen = true;
        }
        return anyGreen ? "GREEN" : "SKIPPED";
    }

    private static void addSamples(List<String> target, String path,
                                   List<? extends Object> diagnostics) {
        for (Object diagnostic : diagnostics) {
            if (target.size() >= SAMPLE_LIMIT) return;
            if (diagnostic instanceof com.xenoamess.kaishek.syntax.Diagnostic d) {
                target.add(path + ":" + d.code());
            } else if (diagnostic instanceof Diagnostic d) {
                target.add(path + ":" + d.code());
            } else {
                target.add(path + ":" + diagnostic);
            }
        }
    }

    private static List<String> diagnosticSamples(List<?> diagnostics) {
        List<String> result = new ArrayList<>();
        addSamples(result, "fixture", diagnostics);
        return result;
    }

    private static List<String> irSamples(IrProgram program) {
        List<String> result = new ArrayList<>();
        for (var diagnostic : program.diagnostics()) {
            if (result.size() >= SAMPLE_LIMIT) break;
            result.add("fixture:" + diagnostic.code());
        }
        return result;
    }

    private static List<String> bounded(List<String> values) {
        return values.size() <= SAMPLE_LIMIT ? List.copyOf(values) : List.copyOf(values.subList(0, SAMPLE_LIMIT));
    }

    private static Stage stage(String status, int files, long bytes, int diagnostics,
                               String reason, String sha256, List<String> samples) {
        return new Stage(status, files, bytes, diagnostics, reason, sha256, bounded(samples));
    }

    private static boolean rootIsFile(Path path) {
        return Files.isRegularFile(path);
    }

    private static boolean isScript(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".txt") || name.endsWith(".gui");
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (java.security.NoSuchAlgorithmException exception) {
            throw new AssertionError(exception);
        }
    }

    private static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) result.append(String.format(Locale.ROOT, "%02x", value));
        return result.toString();
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " is blank");
        return value;
    }

    private record ProfileSelection(KaishekProfile validatorProfile, String buildFingerprint) { }
    private record RootResult(Stage parser, Stage validator) { }
    private record FixtureResult(Stage parser, Stage validator, Stage ir, Stage runtime, String scope) { }

    private static void field(StringBuilder out, String key, String value) {
        if (out.length() > 1) out.append(',');
        out.append(q(key)).append(':').append(q(value));
    }

    private static void fieldRaw(StringBuilder out, String key, String value) {
        if (out.length() > 1) out.append(',');
        out.append(q(key)).append(':').append(value);
    }

    private static String mapJson(Map<String, String> values) {
        StringBuilder out = new StringBuilder("{");
        values.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(entry -> field(out, entry.getKey(), entry.getValue()));
        return out.append('}').toString();
    }

    private static String stringListJson(List<String> values) {
        StringBuilder out = new StringBuilder("[");
        for (String value : values) {
            if (out.length() > 1) out.append(',');
            out.append(q(value));
        }
        return out.append(']').toString();
    }

    private static String q(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n") + "\"";
    }
}
