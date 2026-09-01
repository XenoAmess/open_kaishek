package com.xenoamess.kaishek.syntax;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Dependency-free regression for the first structured Phase 1 constructs.
 * Run explicitly; Maven compiles but deliberately excludes SelfTest mains.
 */
public final class Phase1SyntaxSelfTest {
    private Phase1SyntaxSelfTest() { }

    public static void main(String[] args) {
        byte[] source = ("\uFEFF@answer = @[base + 1]\r\n"
                + "items = [ one, two scope:actor.liege ]\r\n"
                + "target = $TARGET$\r\n"
                + "if = { limit = { scope:actor ?= root } x = 1 }\r\n"
                + "visible = [Widget.GetValue]\r\n").getBytes(StandardCharsets.UTF_8);
        ParseResult parsed = Parser.parse(source);
        check(!parsed.hasErrors(), "valid Phase 1 fixture produced diagnostics: " + parsed.diagnostics());
        check(Arrays.equals(source, parsed.emit()), "Phase 1 fixture changed source bytes");
        check(parsed.document().entries().size() == 5, "entry count changed");

        EntryNode directive = parsed.document().entries().get(0);
        check(directive.isReaderDirective(), "@ declaration was not typed as a reader directive");
        ExpressionNode math = findFirst(directive.value(), ExpressionNode.class);
        check(math != null && math.role() == BracketRole.INLINE_MATH,
                "@[] arithmetic was not represented as inline math");
        check(math.children().stream().anyMatch(n -> n.kind() == SyntaxKind.MATH_OPERATOR),
                "inline math operator token missing");
        check(math.children().stream().anyMatch(n -> n.atomRole() == AtomRole.MATH_OPERAND),
                "inline math operand role missing");

        CstNode listValue = parsed.document().entries().get(1).value();
        check(listValue instanceof ListNode, "explicit list was not represented by ListNode: " + listValue.kind());
        ListNode list = (ListNode) listValue;
        check(list.role() == BracketRole.LIST && list.items().size() == 3,
                "explicit list terms were not retained: " + list.items());
        check(list.items().get(2).atomRole() == AtomRole.SCOPE_CHAIN,
                "scope chain list term was not typed");

        CstNode parameter = parsed.document().entries().get(2).value();
        check(parameter.kind() == SyntaxKind.VARIABLE && parameter.atomRole() == AtomRole.PARAMETER,
                "script parameter did not preserve VARIABLE compatibility and PARAMETER role");
        CstNode quoted = Parser.parse("literal = \"$P$ root.culture\"")
                .document().entries().get(0).value();
        check(quoted.kind() == SyntaxKind.STRING && quoted.atomRole() == AtomRole.PLAIN,
                "parameter-looking text inside a quoted string was typed as a reference");
        check(parsed.document().entries().get(3).isConditional(),
                "conditional block entry was not typed");
        CstNode inlineConditional = Parser.parse("check = [left = right]")
                .document().entries().get(0).value();
        check(inlineConditional instanceof ExpressionNode expression
                        && expression.role() == BracketRole.INLINE_CONDITIONAL
                        && expression.children().stream()
                        .anyMatch(n -> n.kind() == SyntaxKind.CONDITIONAL_OPERATOR),
                "inline equality was not represented as a conditional expression");

        CstNode guiValue = parsed.document().entries().get(4).value();
        check(guiValue instanceof ExpressionNode expression
                        && expression.role() == BracketRole.INLINE_EXPRESSION,
                "GUI bracket expression was not structured");
        check(((ExpressionNode) guiValue).items().get(0).atomRole() == AtomRole.SCOPE_CHAIN,
                "dotted GUI reader chain was not typed");

        List<LexemeKind> kinds = Lexer.lex("x=[a,b]").stream().map(Lexeme::kind).toList();
        check(kinds.containsAll(List.of(LexemeKind.LBRACKET, LexemeKind.COMMA, LexemeKind.RBRACKET)),
                "square-bracket lexer tokens missing: " + kinds);
        List<Lexeme> typedLexemes = Lexer.lex("$P$ scope:actor.liege @value");
        check(typedLexemes.get(0).isParameter()
                        && typedLexemes.get(2).isScopeChain(),
                "lexer-only atom role helpers lost parameter/scope typing: " + typedLexemes);
        check(Lexer.lex("[left=right]").stream()
                        .anyMatch(lexeme -> lexeme.kind() == LexemeKind.CONDITIONAL_OPERATOR),
                "conditional comparison token was not exposed by the lexer");
        CstNode emptyList = Parser.parse("x = []").document().entries().get(0).value();
        check(emptyList instanceof ListNode && ((ListNode) emptyList).items().isEmpty(),
                "empty explicit list was not represented as an empty ListNode");

        assertDiagnosticAndRoundTrip("x = [ one", "UNCLOSED_BRACKET");
        assertDiagnosticAndRoundTrip("x = one ]", "UNEXPECTED_RBRACKET");
        System.out.println("Phase1SyntaxSelfTest: OK");
    }

    private static void assertDiagnosticAndRoundTrip(String text, String expectedCode) {
        byte[] source = text.getBytes(StandardCharsets.UTF_8);
        ParseResult result = Parser.parse(source);
        check(result.hasErrors(), "malformed fixture was silently accepted: " + text);
        check(result.diagnostics().stream().anyMatch(d -> d.code().equals(expectedCode)),
                "missing " + expectedCode + ": " + result.diagnostics());
        check(Arrays.equals(source, result.emit()), "malformed fixture changed source bytes");
    }

    private static <T> T findFirst(CstNode root, Class<T> type) {
        if (type.isInstance(root)) return type.cast(root);
        for (CstNode child : root.children()) {
            T found = findFirst(child, type);
            if (found != null) return found;
        }
        return null;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
