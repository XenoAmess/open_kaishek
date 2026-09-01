package com.xenoamess.kaishek.syntax;

/**
 * Conservative lexical roles for atoms which otherwise retain their original
 * {@link SyntaxKind}.  Keeping the syntax kind stable is important to clients
 * written against the Phase&nbsp;0 API (for example, a parameter is still a
 * {@code VARIABLE} to the strict IR compiler), while the role gives Phase&nbsp;1
 * tooling enough information to distinguish references without re-parsing
 * source text.
 */
public enum AtomRole {
    PLAIN,
    PARAMETER,
    SCRIPTED_VARIABLE,
    SCOPE_CHAIN,
    READER_DIRECTIVE,
    INTERPOLATED_PARAMETER,
    CONDITIONAL_KEY,
    MATH_OPERAND
}
