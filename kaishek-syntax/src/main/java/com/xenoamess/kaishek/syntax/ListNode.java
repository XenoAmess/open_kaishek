package com.xenoamess.kaishek.syntax;

import java.util.List;

/** Explicit square-bracket list node ({@code [ ... ]}). */
public final class ListNode extends BracketNode {
    ListNode(SourceSpan span, byte[] source, List<CstNode> children) {
        super(SyntaxKind.LIST, span, source, children, BracketRole.LIST);
    }
}
