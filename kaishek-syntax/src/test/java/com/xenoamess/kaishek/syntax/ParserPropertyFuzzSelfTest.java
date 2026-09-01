package com.xenoamess.kaishek.syntax;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.SplittableRandom;

/**
 * Deterministic, dependency-free property/fuzz-seed harness for the parser.
 *
 * <p>This is intentionally a small executable self-test rather than a
 * framework-bound fuzz test.  It exercises fixed fixtures, byte mutations,
 * and a seeded stream of random bytes.  Every input must be total (no parser
 * exception) and {@link ParseResult#emit()} must return the exact input
 * bytes.  Valid UTF-8 and arbitrary bytes are both accepted as opaque input
 * and must remain byte-preserving.  Malformed UTF-8 must produce at least one
 * {@code INVALID_BYTE} diagnostic; syntax recovery diagnostics are otherwise
 * allowed (including for valid UTF-8 containing unsupported punctuation).</p>
 *
 * <p>The seed and case limits are command-line configurable so a failure can
 * be reproduced offline without a network service or a new dependency:</p>
 *
 * <pre>
 * java -cp target/classes com.xenoamess.kaishek.syntax.ParserPropertyFuzzSelfTest
 * java -cp target/classes com.xenoamess.kaishek.syntax.ParserPropertyFuzzSelfTest \
 *     --seed 0x4b41495348454b31 --cases 2000 --max-bytes 1024
 * </pre>
 */
public final class ParserPropertyFuzzSelfTest {
    private static final long DEFAULT_SEED = 0x4b41495348454b31L;
    private static final int DEFAULT_RANDOM_CASES = 512;
    private static final int DEFAULT_MAX_BYTES = 384;

    /** Valid-UTF-8 syntax seeds include the forms most likely to expose a
     * lexer boundary bug (BOM, mixed newlines, quotes, GUI headers, and CJK).
     */
    private static final List<String> TEXT_SEEDS = List.of(
            "",
            "x = 1\n",
            "# comment\r\nfoo = { bar = \"quoted \\\"value\\\"\" }\n",
            "\uFEFFtypes ZG361ScoreboardTypes { type columns = hbox { layoutpolicy_horizontal = expanding } }\r\n",
            "scope = $PARAM$\nvalue = -1.25e+2\n",
            "事件 = { 名称 = \"琉焰卿\" emoji = \"😀\" }\r",
            "blockoverride\n  \"header_text\" { text = \"title\" }\n",
            "a = { nested = { one = 1 two = 2 } }\n",
            "duplicate = 1\nduplicate = 2\n",
            "unterminated = \"still valid UTF-8\n"
    );

    /** Deliberately arbitrary byte fixtures. */
    private static final List<byte[]> BYTE_SEEDS = List.of(
            new byte[] {(byte) 0xff},
            new byte[] {'x', ' ', '=', ' ', (byte) 0xc3, 0x28},
            new byte[] {(byte) 0xe0, (byte) 0x80, (byte) 0x80},
            new byte[] {'{', (byte) 0xf0, (byte) 0x28, (byte) 0x8c, (byte) 0xbc, '}'},
            new byte[] {0x00, 0x01, 0x02, (byte) 0x80, (byte) 0xfe},
            new byte[] {'=', '{', '}', '#', '\r', '\n'}
    );

    private static final byte[] STRUCTURAL_BYTES = {
            '=', '{', '}', '#', '"', '\\', '\r', '\n', ' ', '\t', '$', '<', '>', '?'
    };

    private static final String[] VALID_PARTS = {
            "x", "value", "1", "-2.5", "=", "{", "}", " ", "\n", "\r\n",
            "# c\n", "\"text\"", "$var$", "@scope", "中", "焰", "😀", "é", "\\\""
    };

    private ParserPropertyFuzzSelfTest() { }

    public static void main(String[] args) {
        Options options = Options.parse(args);
        Runner runner = new Runner(options);
        int index = 0;

        for (int i = 0; i < TEXT_SEEDS.size(); i++) {
            index = runner.check(TEXT_SEEDS.get(i).getBytes(StandardCharsets.UTF_8),
                    "text-seed-" + i, index);
        }
        for (int i = 0; i < BYTE_SEEDS.size(); i++) {
            index = runner.check(BYTE_SEEDS.get(i), "byte-seed-" + i, index);
        }

        SplittableRandom random = new SplittableRandom(options.seed());
        for (int i = 0; i < TEXT_SEEDS.size(); i++) {
            byte[] seed = TEXT_SEEDS.get(i).getBytes(StandardCharsets.UTF_8);
            // Each mutation class has a stable index.  Keeping these cases
            // separate from random bytes makes a failure easy to replay.
            for (int variant = 0; variant < 8; variant++) {
                index = runner.check(mutate(seed, variant, random, options.maxBytes()),
                        "mutation-" + i + "-" + variant, index);
            }
        }
        for (int i = 0; i < BYTE_SEEDS.size(); i++) {
            for (int variant = 0; variant < 4; variant++) {
                index = runner.check(mutate(BYTE_SEEDS.get(i), variant, random, options.maxBytes()),
                        "byte-mutation-" + i + "-" + variant, index);
            }
        }
        for (int i = 0; i < options.randomCases(); i++) {
            index = runner.check(randomBytes(random, options.maxBytes()),
                    "random-bytes-" + i, index);
        }
        // A separate valid stream proves that the arbitrary-byte property is
        // not merely relying on the random generator's invalid-byte path.
        int validCases = Math.max(32, options.randomCases() / 4);
        for (int i = 0; i < validCases; i++) {
            index = runner.check(randomValidUtf8(random, options.maxBytes()),
                    "random-utf8-" + i, index);
        }

        System.out.printf(Locale.ROOT,
                "ParserPropertyFuzzSelfTest: OK seed=0x%x cases=%d validUtf8=%d invalidUtf8=%d "
                        + "diagnostics=%d maxBytes=%d%n",
                options.seed(), runner.cases(), runner.validUtf8(), runner.invalidUtf8(),
                runner.diagnostics(), options.maxBytes());
    }

    private static byte[] mutate(byte[] input, int variant, SplittableRandom random, int maxBytes) {
        byte[] source = input.clone();
        return switch (variant & 7) {
            case 0 -> flip(source, random, maxBytes);
            case 1 -> replaceStructural(source, random, maxBytes);
            case 2 -> insert(source, (byte) random.nextInt(256), random, maxBytes);
            case 3 -> delete(source, random, maxBytes);
            case 4 -> truncate(source, random, maxBytes);
            case 5 -> duplicate(source, random, maxBytes);
            case 6 -> injectInvalidUtf8(source, maxBytes);
            default -> randomOverwrite(source, random, maxBytes);
        };
    }

    private static byte[] flip(byte[] source, SplittableRandom random, int maxBytes) {
        if (source.length == 0) return insert(source, (byte) 0xff, random, maxBytes);
        byte[] result = source.clone();
        int at = random.nextInt(result.length);
        result[at] ^= (byte) (1 << random.nextInt(8));
        return result;
    }

    private static byte[] replaceStructural(byte[] source, SplittableRandom random, int maxBytes) {
        if (source.length == 0) return insert(source, STRUCTURAL_BYTES[0], random, maxBytes);
        byte[] result = source.clone();
        result[random.nextInt(result.length)] =
                STRUCTURAL_BYTES[random.nextInt(STRUCTURAL_BYTES.length)];
        return result;
    }

    private static byte[] insert(byte[] source, byte value, SplittableRandom random, int maxBytes) {
        if (maxBytes == 0) return new byte[0];
        int at = random.nextInt(source.length + 1);
        int length = Math.min(maxBytes, source.length + 1);
        byte[] result = new byte[length];
        int before = Math.min(at, length);
        System.arraycopy(source, 0, result, 0, before);
        if (at < length) result[at] = value;
        int copied = Math.min(source.length - at, length - at - 1);
        if (copied > 0) System.arraycopy(source, at, result, at + 1, copied);
        return result;
    }

    private static byte[] delete(byte[] source, SplittableRandom random, int maxBytes) {
        if (source.length == 0) return source;
        int at = random.nextInt(source.length);
        byte[] result = new byte[source.length - 1];
        System.arraycopy(source, 0, result, 0, at);
        System.arraycopy(source, at + 1, result, at, source.length - at - 1);
        return result;
    }

    private static byte[] truncate(byte[] source, SplittableRandom random, int maxBytes) {
        int length = source.length == 0 ? 0 : random.nextInt(source.length + 1);
        return Arrays.copyOf(source, Math.min(length, maxBytes));
    }

    private static byte[] duplicate(byte[] source, SplittableRandom random, int maxBytes) {
        if (source.length == 0 || source.length >= maxBytes) {
            return insert(source, (byte) '{', random, maxBytes);
        }
        int start = random.nextInt(source.length);
        int count = 1 + random.nextInt(source.length - start);
        int length = Math.min(maxBytes, source.length + count);
        byte[] result = new byte[length];
        int first = Math.min(start, length);
        System.arraycopy(source, 0, result, 0, first);
        int copied = Math.min(count, length - first);
        System.arraycopy(source, start, result, first, copied);
        int tail = Math.min(source.length - start, length - first - copied);
        if (tail > 0) System.arraycopy(source, start, result, first + copied, tail);
        int suffixAt = first + copied + tail;
        if (suffixAt < length) {
            System.arraycopy(source, start + tail, result, suffixAt,
                    Math.min(source.length - start - tail, length - suffixAt));
        }
        return result;
    }

    private static byte[] injectInvalidUtf8(byte[] source, int maxBytes) {
        if (maxBytes <= 0) return new byte[0];
        int length = Math.min(maxBytes, source.length + 2);
        if (length < 2) return new byte[] {(byte) 0xc3};
        byte[] result = Arrays.copyOf(source, length);
        result[length - 2] = (byte) 0xc3; // lead byte followed by a non-continuation
        result[length - 1] = 0x28;
        return result;
    }

    private static byte[] randomOverwrite(byte[] source, SplittableRandom random, int maxBytes) {
        byte[] result = source.clone();
        if (result.length == 0) return randomBytes(random, Math.min(maxBytes, 8));
        int count = 1 + random.nextInt(result.length);
        int start = random.nextInt(result.length - count + 1);
        for (int i = 0; i < count; i++) result[start + i] = (byte) random.nextInt(256);
        return result;
    }

    private static byte[] randomBytes(SplittableRandom random, int maxBytes) {
        int length = random.nextInt(maxBytes + 1);
        byte[] result = new byte[length];
        for (int i = 0; i < result.length; i++) result[i] = (byte) random.nextInt(256);
        return result;
    }

    private static byte[] randomValidUtf8(SplittableRandom random, int maxBytes) {
        if (maxBytes == 0) return new byte[0];
        StringBuilder text = new StringBuilder();
        while (text.toString().getBytes(StandardCharsets.UTF_8).length < maxBytes) {
            String part = VALID_PARTS[random.nextInt(VALID_PARTS.length)];
            String candidate = text + part;
            if (candidate.getBytes(StandardCharsets.UTF_8).length > maxBytes) break;
            text.append(part);
            if (random.nextInt(12) == 0) break;
        }
        return text.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static boolean isValidUtf8(byte[] source) {
        try {
            StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(source));
            return true;
        } catch (CharacterCodingException ex) {
            return false;
        }
    }

    private static String hexPreview(byte[] source) {
        int limit = Math.min(source.length, 32);
        StringBuilder out = new StringBuilder(limit * 3 + 3);
        for (int i = 0; i < limit; i++) {
            if (i > 0) out.append(' ');
            out.append(String.format(Locale.ROOT, "%02x", source[i] & 0xff));
        }
        if (source.length > limit) out.append(" ...");
        return out.toString();
    }

    private static final class Runner {
        private final Options options;
        private int cases;
        private int validUtf8;
        private int invalidUtf8;
        private int diagnostics;

        Runner(Options options) { this.options = options; }

        int check(byte[] input, String label, int index) {
            byte[] source = input.clone();
            boolean valid = isValidUtf8(source);
            if (valid) validUtf8++; else invalidUtf8++;
            ParseResult first;
            List<Lexeme> lexemes;
            try {
                first = Parser.parse(source);
                lexemes = Parser.lex(source);
            } catch (Throwable ex) {
                rethrowVmError(ex);
                throw failure(label, index, source, "parser threw " + ex, ex);
            }
            assertResult(label, index, source, first, lexemes, valid);
            ParseResult second;
            try {
                second = Parser.parse(source);
            } catch (Throwable ex) {
                rethrowVmError(ex);
                throw failure(label, index, source, "second parse threw " + ex, ex);
            }
            if (!first.diagnostics().equals(second.diagnostics())
                    || first.hasErrors() != second.hasErrors()
                    || !first.newlineStyle().equals(second.newlineStyle())) {
                throw failure(label, index, source,
                        "diagnostics or derived parse state is not deterministic: first="
                                + first.diagnostics() + " second=" + second.diagnostics(), null);
            }
            diagnostics += first.diagnostics().size();
            cases++;

            // Verify both the parser's defensive source copy and emit's
            // defensive result copy, without changing the next assertion.
            if (source.length > 0) {
                byte old = source[0];
                source[0] ^= 0x55;
                if (!Arrays.equals(input, first.emit())) {
                    throw failure(label, index, input,
                            "mutating caller input changed ParseResult.emit()", null);
                }
                source[0] = old;
                byte[] emitted = first.emit();
                emitted[0] ^= 0x55;
                if (!Arrays.equals(input, first.emit())) {
                    throw failure(label, index, input,
                            "mutating emitted copy changed ParseResult", null);
                }
            }
            return index + 1;
        }

        private static void rethrowVmError(Throwable throwable) {
            // Preserve VM/process-level errors; ordinary parser Errors are
            // intentionally converted to a replayable AssertionError below.
            if (throwable instanceof VirtualMachineError error) throw error;
        }

        private void assertResult(String label, int index, byte[] source, ParseResult result,
                                  List<Lexeme> lexemes, boolean validUtf8) {
            if (!Arrays.equals(source, result.emit())) {
                throw failure(label, index, source, "emit changed source bytes", null);
            }
            boolean invalidDiagnostic = result.diagnostics().stream()
                    .anyMatch(d -> "INVALID_BYTE".equals(d.code()));
            if (!validUtf8 && !invalidDiagnostic) {
                throw failure(label, index, source,
                        "malformed UTF-8 did not produce INVALID_BYTE: " + result.diagnostics(), null);
            }
            for (Diagnostic diagnostic : result.diagnostics()) {
                SourceSpan span = diagnostic.span();
                if (span.start() < 0 || span.end() < span.start() || span.end() > source.length) {
                    throw failure(label, index, source,
                            "diagnostic span outside input: " + diagnostic, null);
                }
            }
            int previousEnd = 0;
            for (Lexeme lexeme : lexemes) {
                SourceSpan span = lexeme.span();
                if (span.start() < previousEnd || span.end() > source.length) {
                    throw failure(label, index, source,
                            "lexeme span is not ordered/in bounds: " + lexeme, null);
                }
                previousEnd = span.end();
                byte[] expected = Arrays.copyOfRange(source, span.start(), span.end());
                if (!Arrays.equals(expected, lexeme.raw())) {
                    throw failure(label, index, source,
                            "lexeme raw bytes differ at " + span, null);
                }
            }
            checkNode(result.document(), source.length, label, index, source);
        }

        private void checkNode(CstNode node, int length, String label, int index, byte[] source) {
            SourceSpan span = node.span();
            if (span.start() < 0 || span.end() < span.start() || span.end() > length) {
                throw failure(label, index, source, "CST span outside input: " + span, null);
            }
            for (CstNode child : node.children()) checkNode(child, length, label, index, source);
        }

        private AssertionError failure(String label, int index, byte[] source, String message,
                                       Throwable cause) {
            String detail = String.format(Locale.ROOT,
                    "%s (case=%d, seed=0x%x, bytes=%d, hex=%s)",
                    message, index, options.seed(), source.length, hexPreview(source));
            return cause == null ? new AssertionError(label + ": " + detail)
                    : new AssertionError(label + ": " + detail, cause);
        }

        int cases() { return cases; }
        int validUtf8() { return validUtf8; }
        int invalidUtf8() { return invalidUtf8; }
        int diagnostics() { return diagnostics; }
    }

    private record Options(long seed, int randomCases, int maxBytes) {
        static Options parse(String[] args) {
            long seed = DEFAULT_SEED;
            int cases = DEFAULT_RANDOM_CASES;
            int maxBytes = DEFAULT_MAX_BYTES;
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equals("--seed")) {
                    if (++i >= args.length) throw new IllegalArgumentException("--seed requires a value");
                    seed = Long.decode(args[i]);
                } else if (arg.startsWith("--seed=")) {
                    seed = Long.decode(arg.substring("--seed=".length()));
                } else if (arg.equals("--cases")) {
                    if (++i >= args.length) throw new IllegalArgumentException("--cases requires a value");
                    cases = Integer.parseInt(args[i]);
                } else if (arg.startsWith("--cases=")) {
                    cases = Integer.parseInt(arg.substring("--cases=".length()));
                } else if (arg.equals("--max-bytes")) {
                    if (++i >= args.length) throw new IllegalArgumentException("--max-bytes requires a value");
                    maxBytes = Integer.parseInt(args[i]);
                } else if (arg.startsWith("--max-bytes=")) {
                    maxBytes = Integer.parseInt(arg.substring("--max-bytes=".length()));
                } else {
                    throw new IllegalArgumentException("unknown option: " + arg);
                }
            }
            if (cases < 0 || cases > 100_000) {
                throw new IllegalArgumentException("--cases must be between 0 and 100000");
            }
            if (maxBytes < 1 || maxBytes > 1_048_576) {
                throw new IllegalArgumentException("--max-bytes must be between 1 and 1048576");
            }
            return new Options(seed, cases, maxBytes);
        }
    }
}
