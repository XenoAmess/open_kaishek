package com.xenoamess.kaishek.syntax;

import java.util.List;

class Node implements CstNode {
    private final SyntaxKind kind; private final SourceSpan span; private final byte[] source; private final List<CstNode> children;
    Node(SyntaxKind kind, SourceSpan span, byte[] source) { this(kind, span, source, List.of()); }
    Node(SyntaxKind kind, SourceSpan span, byte[] source, List<CstNode> children) {
        this.kind = kind; this.span = span; this.source = source; this.children = List.copyOf(children);
    }
    @Override public SyntaxKind kind() { return kind; }
    @Override public SourceSpan span() { return span; }
    /**
     * Return only this node's bytes.  {@link CstNode#raw()} has to remain
     * defensive, but going through {@link #source()} first would clone the
     * complete source file for every leaf visited by a validator.  Nodes all
     * retain the immutable parser snapshot, so copy just the requested span.
     */
    @Override public byte[] raw() {
        return java.util.Arrays.copyOfRange(source, span.start(), span.end());
    }
    /** Decode this node's span without allocating an intermediate full-file copy. */
    @Override public String text() {
        return new String(source, span.start(), span.length(),
                java.nio.charset.StandardCharsets.UTF_8);
    }
    @Override public byte[] source() { return source.clone(); }
    @Override public List<CstNode> children() { return children; }
}
