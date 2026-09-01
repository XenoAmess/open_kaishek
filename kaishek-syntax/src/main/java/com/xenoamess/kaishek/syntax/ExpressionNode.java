package com.xenoamess.kaishek.syntax;

import java.util.List;

/** Bracketed inline conditional/math/property expression. */
public final class ExpressionNode extends BracketNode {
    ExpressionNode(SourceSpan span, byte[] source, List<CstNode> children, BracketRole role) {
        super(kindFor(role), span, source, children, role);
    }

    private static SyntaxKind kindFor(BracketRole role) {
        return switch (role) {
            case INLINE_CONDITIONAL -> SyntaxKind.INLINE_CONDITIONAL;
            case INLINE_MATH -> SyntaxKind.INLINE_MATH;
            case INLINE_EXPRESSION, LIST -> SyntaxKind.INLINE_EXPRESSION;
        };
    }
}
