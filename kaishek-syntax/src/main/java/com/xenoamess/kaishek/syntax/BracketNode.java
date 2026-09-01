package com.xenoamess.kaishek.syntax;

import java.util.List;

/**
 * Lossless square-bracket CST node.  Brackets and all trivia remain children;
 * callers can use {@link #items()} for the non-trivia terms without losing the
 * original source.  The role is intentionally conservative and never implies
 * that an expression is safe to evaluate.
 */
public class BracketNode extends Node {
    private final BracketRole role;

    BracketNode(SyntaxKind kind, SourceSpan span, byte[] source,
                List<CstNode> children, BracketRole role) {
        super(kind, span, source, children);
        this.role = role == null ? BracketRole.INLINE_EXPRESSION : role;
    }

    public BracketRole role() { return role; }
    public boolean isList() { return role == BracketRole.LIST; }
    public List<CstNode> items() {
        return children().stream()
                .filter(node -> switch (node.kind()) {
                    case LBRACKET, RBRACKET, LPAREN, RPAREN, COMMA,
                            BOM, COMMENT, WHITESPACE, NEWLINE -> false;
                    default -> true;
                })
                .toList();
    }
    public CstNode opening() {
        return children().stream().filter(node -> node.kind() == SyntaxKind.LBRACKET).findFirst().orElse(null);
    }
    public CstNode closing() {
        return children().stream().filter(node -> node.kind() == SyntaxKind.RBRACKET).reduce((a, b) -> b).orElse(null);
    }
}
