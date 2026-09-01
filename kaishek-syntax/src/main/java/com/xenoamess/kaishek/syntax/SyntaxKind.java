package com.xenoamess.kaishek.syntax;

public enum SyntaxKind {
    DOCUMENT, ENTRY, BLOCK, LIST, LIST_ITEM,
    KEY, OPERATOR, VALUE, STRING, NUMBER, BARE_VALUE, VARIABLE,
    /** A {@code $NAME$} scripted parameter reference (usually exposed through AtomNode.role()). */
    PARAMETER,
    /** A bracketed/inline expression whose exact semantic domain is not yet known. */
    INLINE_EXPRESSION,
    /** A bracketed expression containing a conditional/comparison operation. */
    INLINE_CONDITIONAL,
    /** A bracketed expression containing arithmetic. */
    INLINE_MATH,
    /** A scope/reference chain such as {@code scope:actor.liege}. */
    SCOPE_CHAIN,
    /** A preprocessor/reader declaration beginning with {@code @}. */
    READER_DIRECTIVE,
    MATH_OPERATOR, CONDITIONAL_OPERATOR,
    BOM, COMMENT, WHITESPACE, NEWLINE,
    LBRACE, RBRACE, LBRACKET, RBRACKET, LPAREN, RPAREN, COMMA, ERROR
}
