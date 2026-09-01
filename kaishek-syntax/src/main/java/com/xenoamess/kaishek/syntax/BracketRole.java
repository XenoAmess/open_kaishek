package com.xenoamess.kaishek.syntax;

/** Conservative classification of a square-bracket construct. */
public enum BracketRole {
    /** A data list, e.g. {@code [one two three]}. */
    LIST,
    /** A GUI/property expression, e.g. {@code [Widget.GetValue]}. */
    INLINE_EXPRESSION,
    /** A conditional/comparison expression. */
    INLINE_CONDITIONAL,
    /** An arithmetic expression, e.g. {@code @[base + 1]}. */
    INLINE_MATH
}
