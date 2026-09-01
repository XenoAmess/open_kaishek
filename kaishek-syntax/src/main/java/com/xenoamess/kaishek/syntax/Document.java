package com.xenoamess.kaishek.syntax;

import java.util.List;

public final class Document implements CstNode {
    private final byte[] source; private final SourceSpan span; private final List<CstNode> children;
    Document(byte[] source, List<CstNode> children) {
        this.source = source; this.span = new SourceSpan(0, source.length); this.children = List.copyOf(children);
    }
    @Override public SyntaxKind kind() { return SyntaxKind.DOCUMENT; }
    @Override public SourceSpan span() { return span; }
    /** Keep raw/text access span-local; source() remains a defensive full copy. */
    @Override public byte[] raw() {
        return java.util.Arrays.copyOfRange(source, span.start(), span.end());
    }
    @Override public String text() {
        return new String(source, span.start(), span.length(),
                java.nio.charset.StandardCharsets.UTF_8);
    }
    @Override public byte[] source() { return source.clone(); }
    @Override public List<CstNode> children() { return children; }
    public List<EntryNode> entries() { return children.stream().filter(EntryNode.class::isInstance).map(EntryNode.class::cast).toList(); }
}
