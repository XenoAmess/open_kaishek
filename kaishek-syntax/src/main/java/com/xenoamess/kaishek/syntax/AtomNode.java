package com.xenoamess.kaishek.syntax;

/** A leaf CST node with a conservative Phase&nbsp;1 semantic/lexical role. */
public final class AtomNode extends Node {
    private final AtomRole role;

    AtomNode(SyntaxKind kind, SourceSpan span, byte[] source, AtomRole role) {
        super(kind, span, source);
        this.role = role == null ? AtomRole.PLAIN : role;
    }

    public AtomRole role() { return role; }
    public boolean isParameter() {
        return role == AtomRole.PARAMETER || role == AtomRole.INTERPOLATED_PARAMETER;
    }
    public boolean isScopeChain() { return role == AtomRole.SCOPE_CHAIN; }
    public boolean isReaderDirective() { return role == AtomRole.READER_DIRECTIVE; }
}
