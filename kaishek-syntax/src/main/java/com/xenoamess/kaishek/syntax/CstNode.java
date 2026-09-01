package com.xenoamess.kaishek.syntax;

import java.util.List;

/** Lossless concrete syntax node. Children are in source order and include trivia. */
public interface CstNode {
    SyntaxKind kind();
    SourceSpan span();
    /**
     * Return a defensive copy of the original source bytes.  Nodes retain a
     * private immutable snapshot; callers must not be able to mutate a parse
     * result by editing an array returned from the tree.
     */
    byte[] source();
    List<CstNode> children();

    /** Conservative Phase 1 atom role without changing the Phase 0 syntax kind. */
    default AtomRole atomRole() {
        return this instanceof AtomNode atom ? atom.role() : AtomRole.PLAIN;
    }

    /** Conservative bracket role, or {@code null} for a non-bracket node. */
    default BracketRole bracketRole() {
        return this instanceof BracketNode bracket ? bracket.role() : null;
    }

    default byte[] raw() {
        SourceSpan s = span();
        byte[] source = source();
        byte[] result = new byte[s.length()];
        System.arraycopy(source, s.start(), result, 0, result.length);
        return result;
    }
    default String text() { return new String(raw(), java.nio.charset.StandardCharsets.UTF_8); }
}
