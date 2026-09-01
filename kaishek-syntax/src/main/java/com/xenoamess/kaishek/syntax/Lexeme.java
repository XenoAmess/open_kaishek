package com.xenoamess.kaishek.syntax;

/** A lexical item retaining its exact source span and bytes. */
public record Lexeme(LexemeKind kind, SourceSpan span, byte[] source, boolean malformed) {
    public Lexeme { source = source.clone(); }
    /** Do not expose the mutable backing array retained by this record. */
    @Override public byte[] source() { return source.clone(); }
    /** Copy/decode only this lexeme span; source() remains a defensive copy. */
    public byte[] raw() { return java.util.Arrays.copyOfRange(source, span.start(), span.end()); }
    public String text() {
        return new String(source, span.start(), span.length(),
                java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Return the conservative Phase 1 role inferred from this lexical item.
     * The lexer intentionally keeps {@link LexemeKind#VARIABLE} as the stable
     * compatibility bucket; callers that need parser context should prefer
     * {@link CstNode#atomRole()} on the CST.  This helper is useful for tools
     * that only consume the lexer stream and never evaluates a value.
     */
    public AtomRole atomRole() {
        String value = text();
        if (kind == LexemeKind.VARIABLE) {
            if (value.startsWith("@")) return AtomRole.SCRIPTED_VARIABLE;
            if (value.matches("\\$[^$\\r\\n]+\\$")
                    || (value.startsWith("$") && value.endsWith("$"))) {
                return AtomRole.PARAMETER;
            }
            if (value.indexOf('$') >= 0
                    && value.indexOf('$') != value.lastIndexOf('$')) {
                return AtomRole.INTERPOLATED_PARAMETER;
            }
        }
        if (kind == LexemeKind.BARE_VALUE && isScopeChain(value)) {
            return AtomRole.SCOPE_CHAIN;
        }
        return AtomRole.PLAIN;
    }

    public boolean isParameter() {
        AtomRole role = atomRole();
        return role == AtomRole.PARAMETER || role == AtomRole.INTERPOLATED_PARAMETER;
    }

    public boolean isScopeChain() { return atomRole() == AtomRole.SCOPE_CHAIN; }

    private static boolean isScopeChain(String value) {
        if (value.equalsIgnoreCase("root") || value.equalsIgnoreCase("this")
                || value.equalsIgnoreCase("prev")) return true;
        return value.matches("(?i)(?:scope|var|global_var|local_var|flag|event_target|saved_scope|saved_value):[A-Za-z0-9_$.-]+")
                || value.matches("[A-Za-z_][A-Za-z0-9_]*\\.[A-Za-z_][A-Za-z0-9_.]*");
    }
}
