package com.xenoamess.kaishek.syntax;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/** Dependency-free smoke tests; run with {@code java ...ParserSelfTest}. */
public final class ParserSelfTest {
    public static void main(String[] args) {
        byte[] input = ("\uFEFF# keep\r\n" +
                "foo != { alpha 2 beta = \"quoted \\\"value\\\"\" }\n" +
                "foo = $PARAM$").getBytes(StandardCharsets.UTF_8);
        ParseResult result = Parser.parse(input);
        check(Arrays.equals(input, result.emit()), "round-trip changed bytes");
        check(!result.hasErrors(), "valid fixture produced errors: " + result.diagnostics());
        check(result.document().entries().size() == 2, "entry order/repetition lost");
        check(result.document().children().get(0).kind() == SyntaxKind.BOM, "BOM not retained");

        ParseResult malformed = Parser.parse("x = { y = 1".getBytes(StandardCharsets.UTF_8));
        check(malformed.hasErrors(), "unclosed block was not diagnosed");
        check(Arrays.equals(malformed.source(), malformed.emit()), "malformed input was rewritten");

        byte[] invalidUtf8 = {'x', ' ', '=', ' ', (byte) 0xc3, (byte) 0x28};
        ParseResult invalid = Parser.parse(invalidUtf8);
        check(invalid.hasErrors(), "invalid UTF-8 was silently accepted");
        check(invalid.diagnostics().stream().anyMatch(d -> d.code().equals("INVALID_BYTE")),
                "invalid UTF-8 diagnostic missing: " + invalid.diagnostics());
        check(Arrays.equals(invalidUtf8, invalid.emit()), "invalid UTF-8 input was rewritten");

        ParseResult mixedNewlines = Parser.parse("a = 1\r\nb = 2\r\nc = 3\n".getBytes(StandardCharsets.UTF_8));
        check("\r\n".equals(mixedNewlines.newlineStyle()),
                "newline style did not select the dominant sequence");

        // Node raw/text access is span-local, while source() remains a
        // defensive full-source snapshot.  Keep these assertions alongside
        // the byte-preservation contract so a future optimization cannot
        // accidentally expose mutable parser storage.
        byte[] accessInput = "foo = { bar = \"值\" }\n".getBytes(StandardCharsets.UTF_8);
        ParseResult accessResult = Parser.parse(accessInput);
        CstNode accessDocument = accessResult.document();
        check(Arrays.equals(accessInput, accessDocument.raw()), "document raw span changed bytes");
        check(new String(accessInput, StandardCharsets.UTF_8).equals(accessDocument.text()),
                "document text span changed text");
        EntryNode accessEntry = accessResult.document().entries().get(0);
        CstNode accessKey = accessEntry.key();
        check("foo".equals(accessKey.text()), "node text did not decode its span");
        check(Arrays.equals("foo".getBytes(StandardCharsets.UTF_8), accessKey.raw()),
                "node raw did not select its span");
        byte[] nodeRaw = accessKey.raw();
        nodeRaw[0] ^= 0x55;
        check("foo".equals(accessKey.text()), "mutating node raw changed CST storage");
        byte[] nodeSource = accessKey.source();
        check(Arrays.equals(accessInput, nodeSource), "node source is not the full source snapshot");
        nodeSource[0] ^= 0x55;
        check("foo".equals(accessKey.text()), "mutating node source changed CST storage");

        Lexeme accessLexeme = Parser.lex(accessInput).stream()
                .filter(item -> item.kind() == LexemeKind.BARE_VALUE)
                .findFirst().orElseThrow();
        check("foo".equals(accessLexeme.text()), "lexeme text did not decode its span");
        System.out.println("ParserSelfTest: OK");
    }
    private static void check(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
}
