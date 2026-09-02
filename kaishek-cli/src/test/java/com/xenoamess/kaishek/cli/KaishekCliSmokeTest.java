package com.xenoamess.kaishek.cli;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** No framework dependency: invoke this {@code main} as an explicit CLI smoke test. */
public final class KaishekCliSmokeTest {
  public static void main(String[] args) {
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    int ok = KaishekCli.run(new String[]{"parse"}, new PrintStream(b), System.err);
    check(ok == 0 && b.toString(StandardCharsets.UTF_8).contains("\"status\":\"PARSED\""), b);
    b.reset();
    int fixture = KaishekCli.run(new String[]{"synthetic-361"}, new PrintStream(b), System.err);
    check(fixture == 0 && b.toString(StandardCharsets.UTF_8).contains("\"synthetic\":true")
        && b.toString(StandardCharsets.UTF_8).contains("\"execution\":\"SUCCESS\""), b);
    b.reset();
    int unsupported = KaishekCli.run(new String[]{"profile", "--id", "future"}, new PrintStream(b), System.err);
    check(unsupported == 4 && b.toString(StandardCharsets.UTF_8).contains("UNSUPPORTED"), b);

    // Diagnostic offsets are numeric JSON fields (and byte offsets), not
    // quoted decimal strings.
    b.reset();
    int syntaxError = KaishekCli.run(new String[]{"parse", "x"}, new PrintStream(b), System.err);
    String syntaxJson = b.toString(StandardCharsets.UTF_8);
    check(syntaxError == 1 && syntaxJson.contains("\"offset\":0")
        && !syntaxJson.contains("\"offset\":\"0\""), syntaxJson);

    b.reset();
    int inlineSyntaxError = KaishekCli.run(new String[]{"validate", "x"},
        new PrintStream(b), System.err);
    // An inline syntax error must not be reported as an unsupported semantic
    // layer merely because no file path was supplied.
    check(inlineSyntaxError == 1 && b.toString(StandardCharsets.UTF_8)
        .contains("\"status\":\"INVALID\""), b);

    // A semantic diagnostic must make validate fail.  Put the fixture under a
    // recognised directory so the static CK3 profile is actually selected,
    // and put --profile before --file to exercise option-order handling.
    Path root = null;
    try {
      root = Files.createTempDirectory("kaishek-cli-smoke-");
      Path effects = Files.createDirectories(root.resolve("common/scripted_effects"));
      Path invalid = effects.resolve("invalid.txt");
      Files.writeString(invalid, "mystery_opcode = yes\n", StandardCharsets.UTF_8);
      b.reset();
      int semanticError = KaishekCli.run(new String[]{"validate", "--profile", "ck3-1.19.0.6",
          "--file", invalid.toString()}, new PrintStream(b), System.err);
      String semanticJson = b.toString(StandardCharsets.UTF_8);
      check(semanticError == 1 && semanticJson.contains("\"status\":\"INVALID\"")
          && semanticJson.contains("\"semanticDiagnostics\":1"), semanticJson);

      b.reset();
      int missing = KaishekCli.run(new String[]{"parse", "--file"}, new PrintStream(b), System.err);
      check(missing == 2 && b.toString(StandardCharsets.UTF_8).contains("\"status\":\"ERROR\""), b);

      b.reset();
      int trailingUnknown = KaishekCli.run(new String[]{"parse", "x = 1", "--unknown"},
          new PrintStream(b), System.err);
      check(trailingUnknown == 2 && b.toString(StandardCharsets.UTF_8).contains("\"status\":\"ERROR\""), b);

      Path manifest = root.resolve("cases.jsonl");
      Files.writeString(manifest,
          "{\"id\":\"ok\",\"command\":\"parse\",\"text\":\"x = 1\\n\"}\n"
              + "{\"id\":\"bad\",\"command\":\"parse\",\"text\":\"x\"}\n",
          StandardCharsets.UTF_8);
      b.reset();
      int batch = KaishekCli.run(new String[]{"batch", "--file", manifest.toString()},
          new PrintStream(b), System.err);
      String[] batchLines = b.toString(StandardCharsets.UTF_8).trim().split("\\R");
      check(batch == 1 && batchLines.length == 2 && batchLines[0].contains("\"id\":\"ok\"")
          && batchLines[0].contains("\"exitCode\":0")
          && batchLines[0].contains("\"result\":{\"status\":\"PARSED\"")
          && batchLines[1].contains("\"id\":\"bad\"")
          && batchLines[1].contains("\"exitCode\":1"), b);

      b.reset();
      int replay = KaishekCli.run(new String[]{"replay", "--file", manifest.toString(), "--stop-on-error"},
          new PrintStream(b), System.err);
      check(replay == 1 && b.toString(StandardCharsets.UTF_8).trim().split("\\R").length == 2, b);

      Path malformed = root.resolve("malformed.jsonl");
      Files.writeString(malformed, "{\"id\":\"oops\",\"command\":\"parse\",\"args\":1}\n", StandardCharsets.UTF_8);
      b.reset();
      int malformedExit = KaishekCli.run(new String[]{"batch", malformed.toString()}, new PrintStream(b), System.err);
      check(malformedExit == 2 && b.toString(StandardCharsets.UTF_8).contains("\"status\":\"ERROR\""), b);
      // The corpus-only flag must not silently broaden the other commands'
      // option grammar.
      b.reset();
      int misplacedCorpusFlag = KaishekCli.run(new String[]{"parse", "--require-corpus", "x = 1"},
          new PrintStream(b), System.err);
      check(misplacedCorpusFlag == 2 && b.toString(StandardCharsets.UTF_8).contains("\"status\":\"ERROR\""), b);

      // A required external corpus must never turn an empty checkout into a
      // passing inventory. The optional form remains backward compatible.
      Path empty = Files.createDirectories(root.resolve("empty-corpus"));
      b.reset();
      int optionalEmpty = KaishekCli.run(new String[]{"corpus", empty.toString()},
          new PrintStream(b), System.err);
      String optionalEmptyJson = b.toString(StandardCharsets.UTF_8);
      check(optionalEmpty == 0 && optionalEmptyJson.contains("\"status\":\"OK\"")
          && optionalEmptyJson.contains("\"files\":0"), optionalEmptyJson);

      b.reset();
      int requiredEmpty = KaishekCli.run(new String[]{"corpus", "--require-corpus", empty.toString()},
          new PrintStream(b), System.err);
      String requiredEmptyJson = b.toString(StandardCharsets.UTF_8);
      check(requiredEmpty == 1 && requiredEmptyJson.contains("\"status\":\"SKIP\"")
          && requiredEmptyJson.contains("\"reason\":\"corpus-empty\"")
          && requiredEmptyJson.contains("\"required\":true"), requiredEmptyJson);

      // The same gate catches a directory containing no supported script
      // extensions, regardless of option order.
      Path nonScript = Files.createDirectories(root.resolve("non-script-corpus"));
      Files.writeString(nonScript.resolve("README.md"), "not a Paradox script\n", StandardCharsets.UTF_8);
      b.reset();
      int requiredNonScript = KaishekCli.run(new String[]{"corpus", nonScript.toString(), "--require-corpus"},
          new PrintStream(b), System.err);
      String requiredNonScriptJson = b.toString(StandardCharsets.UTF_8);
      check(requiredNonScript == 1 && requiredNonScriptJson.contains("\"status\":\"SKIP\"")
          && requiredNonScriptJson.contains("\"reason\":\"corpus-empty\""), requiredNonScriptJson);

      // A missing required root is distinguishable from a malformed file and
      // remains a non-zero SKIP for acceptance orchestration.
      Path missingCorpus = root.resolve("missing-corpus");
      b.reset();
      int requiredMissing = KaishekCli.run(new String[]{"corpus", "--require-corpus", missingCorpus.toString()},
          new PrintStream(b), System.err);
      String requiredMissingJson = b.toString(StandardCharsets.UTF_8);
      check(requiredMissing == 1 && requiredMissingJson.contains("\"status\":\"SKIP\"")
          && requiredMissingJson.contains("\"reason\":\"corpus-root-absent\""), requiredMissingJson);

      // A real script still follows the ordinary OK path under the gate.
      Path validCorpus = Files.createDirectories(root.resolve("valid-corpus"));
      Files.writeString(validCorpus.resolve("valid.txt"), "foo = 1\n", StandardCharsets.UTF_8);
      b.reset();
      int requiredValid = KaishekCli.run(new String[]{"corpus", "--require-corpus", validCorpus.toString()},
          new PrintStream(b), System.err);
      String requiredValidJson = b.toString(StandardCharsets.UTF_8);
      check(requiredValid == 0 && requiredValidJson.contains("\"status\":\"OK\"")
          && requiredValidJson.contains("\"files\":1"), requiredValidJson);

      // The parent CK3 acceptance runner can use one stable, offline command
      // before it launches the game.  It combines a source-root parser /
      // validator scan with the checked-in synthetic parser -> IR -> runtime
      // fixture and emits one machine-readable report.
      Path preflightRoot = Files.createDirectories(root.resolve("preflight-root"));
      Path preflightEffects = Files.createDirectories(preflightRoot.resolve("common/scripted_effects"));
      Path preflightSource = preflightEffects.resolve("valid.txt");
      Files.writeString(preflightSource,
          "valid_effect = {\n"
              + "  set_variable = { name = preflight_value value = 1 }\n"
              + "}\n", StandardCharsets.UTF_8);
      b.reset();
      int preflight = KaishekCli.run(new String[]{"preflight", "--root", preflightRoot.toString(),
          "--profile", "ck3-1.19.0.6-zg361", "--fixture", "synthetic-361-014"},
          new PrintStream(b), System.err);
      String preflightJson = b.toString(StandardCharsets.UTF_8);
      check(preflight == 0 && preflightJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && preflightJson.contains("\"status\":\"GREEN\"")
          && preflightJson.contains("\"profile_id\":\"ck3-1.19.0.6-zg361\"")
          && preflightJson.contains("\"fixture_id\":\"synthetic-361-014\"")
          && preflightJson.contains("\"ck3_started\":\"false\"")
          && preflightJson.contains("\"save_mutated\":\"false\"")
          && preflightJson.contains("\"root_scan\":{\"parser\":{\"status\":\"GREEN\"")
          && preflightJson.contains("\"runtime\":{\"status\":\"GREEN\""), preflightJson);

      // A malformed external source is a hard RED and must not be hidden by
      // a passing fixture.  This is the gate that prevents CK3 launch.
      Files.writeString(preflightEffects.resolve("invalid.txt"), "broken = {\n",
          StandardCharsets.UTF_8);
      b.reset();
      int preflightRed = KaishekCli.run(new String[]{"preflight", "--root", preflightRoot.toString()},
          new PrintStream(b), System.err);
      String preflightRedJson = b.toString(StandardCharsets.UTF_8);
      check(preflightRed == 1 && preflightRedJson.contains("\"status\":\"RED\"")
          && preflightRedJson.contains("\"root_scan\"")
          && preflightRedJson.contains("\"parser\":{\"status\":\"RED\""), preflightRedJson);

      // The appeal replay is a separate runtime-fixture option; it remains
      // offline and does not silently claim CK3 semantic certification.
      b.reset();
      int appealPreflight = KaishekCli.run(new String[]{"preflight", "--fixture", "appeal-014"},
          new PrintStream(b), System.err);
      String appealJson = b.toString(StandardCharsets.UTF_8);
      check(appealPreflight == 0 && appealJson.contains("\"fixture_id\":\"appeal-014\"")
          && appealJson.contains("\"fixture_scope\":\"runtime-fixture\"")
          && appealJson.contains("\"runtime\":{\"status\":\"GREEN\""), appealJson);

      // The CK3 1.19.0.6 calculated-value regression fixture is intentionally
      // RED: range blocks (>=/<=) remain accepted, while the observed direct
      // equality emits a stable schema-only diagnostic.  It must never be
      // mistaken for executable/runtime certification.
      b.reset();
      int calculatedValuePreflight = KaishekCli.run(new String[]{"preflight",
          "--fixture", "ck3-calculated-value-014"},
          new PrintStream(b), System.err);
      String calculatedValueJson = b.toString(StandardCharsets.UTF_8);
      check(calculatedValuePreflight == 1
          && calculatedValueJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && calculatedValueJson.contains("\"status\":\"RED\"")
          && calculatedValueJson.contains("\"profile_id\":\"ck3-1.19.0.6-zg361\"")
          && calculatedValueJson.contains("\"fixture_id\":\"ck3-calculated-value-014\"")
          && calculatedValueJson.contains("\"parser\":{\"status\":\"GREEN\"")
          && calculatedValueJson.contains("\"validator\":{\"status\":\"RED\"")
          && calculatedValueJson.contains("CK3_TRIGGER_CALCULATED_VALUE_UNSUPPORTED")
          && calculatedValueJson.contains("\"fixture_scope\":\"schema-only-negative\"")
          && calculatedValueJson.contains("\"runtime\":{\"status\":\"SKIPPED\""),
          calculatedValueJson);

      // The war-days schema slice is a positive, static-only fixture.  Its
      // parser/validator stages are GREEN while IR/runtime remain explicitly
      // skipped because the native evaluator is not runtime-certified.
      b.reset();
      int warDaysPreflight = KaishekCli.run(new String[]{"preflight",
          "--fixture", "ck3-war-days-trigger-11906"},
          new PrintStream(b), System.err);
      String warDaysJson = b.toString(StandardCharsets.UTF_8);
      check(warDaysPreflight == 0
          && warDaysJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && warDaysJson.contains("\"status\":\"GREEN\"")
          && warDaysJson.contains("\"fixture_id\":\"ck3-war-days-trigger-11906\"")
          && warDaysJson.contains("\"fixture_scope\":\"schema-only\"")
          && warDaysJson.contains("\"parser\":{\"status\":\"GREEN\"")
          && warDaysJson.contains("\"validator\":{\"status\":\"GREEN\"")
          && warDaysJson.contains("\"runtime\":{\"status\":\"SKIPPED\"")
          && warDaysJson.contains("\"ck3_started\":\"false\""),
          warDaysJson);

      // The has-innovation schema slice is likewise static-only: parser and
      // validator are GREEN while IR/runtime remain explicitly skipped.
      b.reset();
      int hasInnovationPreflight = KaishekCli.run(new String[]{"preflight",
          "--fixture", "ck3-has-innovation-trigger-11906"},
          new PrintStream(b), System.err);
      String hasInnovationJson = b.toString(StandardCharsets.UTF_8);
      check(hasInnovationPreflight == 0
          && hasInnovationJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && hasInnovationJson.contains("\"status\":\"GREEN\"")
          && hasInnovationJson.contains("\"fixture_id\":\"ck3-has-innovation-trigger-11906\"")
          && hasInnovationJson.contains("\"fixture_scope\":\"schema-only\"")
          && hasInnovationJson.contains("\"parser\":{\"status\":\"GREEN\"")
          && hasInnovationJson.contains("\"validator\":{\"status\":\"GREEN\"")
          && hasInnovationJson.contains("\"runtime\":{\"status\":\"SKIPPED\"")
          && hasInnovationJson.contains("\"ck3_started\":\"false\""),
          hasInnovationJson);

      // The cultural-pillar schema slice is static-only as well: parser and
      // validator are GREEN while IR/runtime remain explicitly skipped.
      b.reset();
      int culturalPillarPreflight = KaishekCli.run(new String[]{"preflight",
          "--fixture", "ck3-has-cultural-pillar-trigger-11906"},
          new PrintStream(b), System.err);
      String culturalPillarJson = b.toString(StandardCharsets.UTF_8);
      check(culturalPillarPreflight == 0
          && culturalPillarJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && culturalPillarJson.contains("\"status\":\"GREEN\"")
          && culturalPillarJson.contains("\"fixture_id\":\"ck3-has-cultural-pillar-trigger-11906\"")
          && culturalPillarJson.contains("\"fixture_scope\":\"schema-only\"")
          && culturalPillarJson.contains("\"parser\":{\"status\":\"GREEN\"")
          && culturalPillarJson.contains("\"validator\":{\"status\":\"GREEN\"")
          && culturalPillarJson.contains("\"runtime\":{\"status\":\"SKIPPED\"")
          && culturalPillarJson.contains("\"ck3_started\":\"false\""),
          culturalPillarJson);

      // The cultural-tradition schema slice is likewise static-only:
      // parser/validator are GREEN while IR/runtime remain explicitly skipped.
      b.reset();
      int hasCulturalTraditionPreflight = KaishekCli.run(new String[]{"preflight",
          "--fixture", "ck3-has-cultural-tradition-trigger-11906"},
          new PrintStream(b), System.err);
      String hasCulturalTraditionJson = b.toString(StandardCharsets.UTF_8);
      check(hasCulturalTraditionPreflight == 0
          && hasCulturalTraditionJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && hasCulturalTraditionJson.contains("\"status\":\"GREEN\"")
          && hasCulturalTraditionJson.contains(
              "\"fixture_id\":\"ck3-has-cultural-tradition-trigger-11906\"")
          && hasCulturalTraditionJson.contains("\"fixture_scope\":\"schema-only\"")
          && hasCulturalTraditionJson.contains("\"parser\":{\"status\":\"GREEN\"")
          && hasCulturalTraditionJson.contains("\"validator\":{\"status\":\"GREEN\"")
          && hasCulturalTraditionJson.contains("\"runtime\":{\"status\":\"SKIPPED\"")
          && hasCulturalTraditionJson.contains("\"ck3_started\":\"false\""),
          hasCulturalTraditionJson);

      // The cultural-parameter schema slice is static-only as well: parser
      // and validator are GREEN while IR/runtime remain explicitly skipped.
      b.reset();
      int hasCulturalParameterPreflight = KaishekCli.run(new String[]{"preflight",
          "--fixture", "ck3-has-cultural-parameter-trigger-11906"},
          new PrintStream(b), System.err);
      String hasCulturalParameterJson = b.toString(StandardCharsets.UTF_8);
      check(hasCulturalParameterPreflight == 0
          && hasCulturalParameterJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && hasCulturalParameterJson.contains("\"status\":\"GREEN\"")
          && hasCulturalParameterJson.contains(
              "\"fixture_id\":\"ck3-has-cultural-parameter-trigger-11906\"")
          && hasCulturalParameterJson.contains("\"fixture_scope\":\"schema-only\"")
          && hasCulturalParameterJson.contains("\"parser\":{\"status\":\"GREEN\"")
          && hasCulturalParameterJson.contains("\"validator\":{\"status\":\"GREEN\"")
          && hasCulturalParameterJson.contains("\"runtime\":{\"status\":\"SKIPPED\"")
          && hasCulturalParameterJson.contains("\"ck3_started\":\"false\""),
          hasCulturalParameterJson);

      // The is-acclaimed schema slice is Character-scoped and static-only:
      // parser/validator are GREEN while IR/runtime remain explicitly
      // skipped because the native CAccolade reader is not runtime-certified.
      b.reset();
      int isAcclaimedPreflight = KaishekCli.run(new String[]{"preflight",
          "--fixture", "ck3-is-acclaimed-trigger-11906"},
          new PrintStream(b), System.err);
      String isAcclaimedJson = b.toString(StandardCharsets.UTF_8);
      check(isAcclaimedPreflight == 0
          && isAcclaimedJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && isAcclaimedJson.contains("\"status\":\"GREEN\"")
          && isAcclaimedJson.contains(
              "\"fixture_id\":\"ck3-is-acclaimed-trigger-11906\"")
          && isAcclaimedJson.contains("\"fixture_scope\":\"schema-only\"")
          && isAcclaimedJson.contains("\"parser\":{\"status\":\"GREEN\"")
          && isAcclaimedJson.contains("\"validator\":{\"status\":\"GREEN\"")
          && isAcclaimedJson.contains("\"runtime\":{\"status\":\"SKIPPED\"")
          && isAcclaimedJson.contains("\"ck3_started\":\"false\""),
          isAcclaimedJson);

      // Unsupported profile selections still use the preflight schema so a
      // parent runner never has to special-case the generic CLI error shape.
      b.reset();
      int unsupportedPreflight = KaishekCli.run(new String[]{"preflight", "--profile", "future-build"},
          new PrintStream(b), System.err);
      String unsupportedPreflightJson = b.toString(StandardCharsets.UTF_8);
      check(unsupportedPreflight == 4
          && unsupportedPreflightJson.contains("\"schema\":\"open_kaishek.preflight.v1\"")
          && unsupportedPreflightJson.contains("\"status\":\"UNSUPPORTED\"")
          && unsupportedPreflightJson.contains("\"profile_id\":\"future-build\"")
          && unsupportedPreflightJson.contains("\"ck3_started\":\"false\""),
          unsupportedPreflightJson);
    } catch (IOException ex) {
      throw new AssertionError("CLI smoke fixture setup failed", ex);
    } finally {
      if (root != null) {
        try (var walk = Files.walk(root)) {
          walk.sorted(java.util.Comparator.reverseOrder()).forEach(path -> {
            try { Files.deleteIfExists(path); }
            catch (IOException ex) { throw new UncheckedIOException(ex); }
          });
        } catch (IOException | UncheckedIOException ex) {
          throw new AssertionError("CLI smoke fixture cleanup failed", ex);
        }
      }
    }
  }

  private static void check(boolean condition, Object detail) {
    if (!condition) throw new AssertionError(String.valueOf(detail));
  }
}
