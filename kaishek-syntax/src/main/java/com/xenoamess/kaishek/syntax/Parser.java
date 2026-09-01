package com.xenoamess.kaishek.syntax;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Small, dependency-free Paradox lexer/CST parser. The parser deliberately does
 * not apply game/schema semantics: unknown syntax is retained and reported, so
 * tooling can safely inspect and rewrite files without losing source bytes.
 */
public final class Parser {
    private Parser() { }

    public static ParseResult parse(byte[] bytes) {
        if (bytes == null) throw new NullPointerException("bytes");
        byte[] source = bytes.clone();
        State state = new State(source);
        List<Token> tokens = state.lex();
        List<CstNode> children = new ArrayList<>();
        state.parseSequence(tokens, 0, tokens.size(), children, false);
        return new ParseResult(source, new Document(source, children), state.diagnostics);
    }

    public static ParseResult parse(String text) { return parse(text.getBytes(java.nio.charset.StandardCharsets.UTF_8)); }

    public static ParseResult parse(InputStream input) throws IOException { return parse(input.readAllBytes()); }

    /** Lex without building a tree. Lexemes still reference copied source bytes. */
    public static List<Lexeme> lex(byte[] bytes) {
        if (bytes == null) throw new NullPointerException("bytes");
        byte[] source = bytes.clone();
        State state = new State(source);
        List<Lexeme> result = new ArrayList<>();
        for (Token t : state.lex()) {
            LexemeKind kind = switch (t.type) {
                case TRIVIA -> t.start == 0 && t.end == 3 ? LexemeKind.BOM : state.containsLineBreak(t) ? LexemeKind.NEWLINE : LexemeKind.WHITESPACE;
                case COMMENT -> LexemeKind.COMMENT; case BARE -> LexemeKind.BARE_VALUE; case STRING -> LexemeKind.STRING;
                case NUMBER -> LexemeKind.NUMBER; case VARIABLE -> LexemeKind.VARIABLE; case OP -> LexemeKind.OPERATOR;
                case LBRACE -> LexemeKind.LBRACE; case RBRACE -> LexemeKind.RBRACE;
                case LBRACKET -> LexemeKind.LBRACKET; case RBRACKET -> LexemeKind.RBRACKET;
                case LPAREN -> LexemeKind.LPAREN; case RPAREN -> LexemeKind.RPAREN;
                case COMMA -> LexemeKind.COMMA;
                case ARITHMETIC -> LexemeKind.ARITHMETIC_OPERATOR;
                case CONDITIONAL -> LexemeKind.CONDITIONAL_OPERATOR;
                case BAD -> LexemeKind.ERROR;
            };
            result.add(new Lexeme(kind, new SourceSpan(t.start, t.end), source, t.malformed));
        }
        return List.copyOf(result);
    }
    public static List<Lexeme> lex(String text) { return lex(text.getBytes(java.nio.charset.StandardCharsets.UTF_8)); }

    private enum T {
        TRIVIA, COMMENT, BARE, STRING, NUMBER, VARIABLE, OP,
        LBRACE, RBRACE, LBRACKET, RBRACKET, LPAREN, RPAREN, COMMA,
        ARITHMETIC, CONDITIONAL, BAD
    }
    private record Token(T type, int start, int end, String op, boolean malformed) { }
    private static final Pattern NUMBER = Pattern.compile("[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?");
    // These predicates run once per atom/bracket while scanning a large
    // generated corpus.  Keep the regular expressions compiled, rather than
    // using String.matches (which recompiles a Pattern on every invocation).
    private static final Pattern PARAMETER = Pattern.compile("\\$[^$\\r\\n]+\\$");
    private static final Pattern SCOPE_REFERENCE = Pattern.compile(
            "(?i)(?:scope|var|global_var|local_var|flag|event_target|saved_scope|saved_value):[A-Za-z0-9_$.-]+");
    private static final Pattern DOT_SCOPE_REFERENCE = Pattern.compile(
            "[A-Za-z_][A-Za-z0-9_]*\\.[A-Za-z_][A-Za-z0-9_.]*");
    private static final Pattern GUI_CONDITIONAL_CALL = Pattern.compile(
            "(?is).*\\b(?:And|Or|Not|EqualTo|NotEqualTo|GreaterThan|LessThan)[A-Za-z0-9_]*\\s*\\(.*");
    private static final Pattern GUI_IF_ELSE = Pattern.compile("(?is).*\\b(?:if|else)\\b.*");
    private static final Pattern GUI_TYPED_CALL = Pattern.compile(
            "(?s).*\\b(?:Is|Has|Can)[A-Z][A-Za-z0-9_]*\\s*\\(.*");

    private static final class State {
        final byte[] b; final List<Diagnostic> diagnostics = new ArrayList<>();
        State(byte[] b) { this.b = b; }

        List<Token> lex() {
            // Java's String(byte[], UTF_8) replaces malformed sequences with
            // U+FFFD.  That is useful for display, but unsafe for a lossless
            // parser: an invalid source byte must remain an explicit error so
            // the validator/IR cannot accidentally execute a repaired token.
            validateUtf8();
            List<Token> out = new ArrayList<>(); int i = 0;
            int bracketDepth = 0;
            while (i < b.length) {
                int s = i; int c = b[i] & 0xff;
                if (i == 0 && b.length >= 3 && (b[0] & 0xff) == 0xef && (b[1] & 0xff) == 0xbb && (b[2] & 0xff) == 0xbf) {
                    i = 3; out.add(new Token(T.TRIVIA, s, i, null, false)); continue;
                }
                if (c == '\r' || c == '\n') {
                    i++; if (c == '\r' && i < b.length && b[i] == '\n') i++;
                    out.add(new Token(T.TRIVIA, s, i, null, false)); continue;
                }
                if (c == ' ' || c == '\t' || c == '\f') { do { i++; } while (i < b.length && (b[i] == ' ' || b[i] == '\t' || b[i] == '\f')); out.add(new Token(T.TRIVIA,s,i,null,false)); continue; }
                if (c == '#') { i++; while (i < b.length && b[i] != '\r' && b[i] != '\n') i++; out.add(new Token(T.COMMENT,s,i,null,false)); continue; }
                if (c == '"') {
                    i++; boolean closed = false;
                    while (i < b.length) { if (b[i] == '\\') { i += Math.min(2, b.length - i); } else if (b[i++] == '"') { closed = true; break; } }
                    if (!closed) { diagnostics.add(new Diagnostic("UNTERMINATED_STRING", Diagnostic.Severity.ERROR, "unterminated quoted string", s, i)); }
                    out.add(new Token(T.STRING,s,i,null,!closed || decodeUtf8(s, i) == null)); continue;
                }
                if (c == '{') { i++; out.add(new Token(T.LBRACE,s,i,null,false)); continue; }
                if (c == '}') { i++; out.add(new Token(T.RBRACE,s,i,null,false)); continue; }
                // Square brackets are used by both GUI expressions
                // (`[Widget.GetValue]`) and finite data lists (`[one two]`).
                // Keep the delimiters as first-class tokens; parseBracket()
                // performs the conservative role classification later.
                if (c == '[') { i++; bracketDepth++; out.add(new Token(T.LBRACKET,s,i,null,false)); continue; }
                if (c == ']') {
                    i++;
                    if (bracketDepth > 0) bracketDepth--;
                    out.add(new Token(T.RBRACKET,s,i,null,false));
                    continue;
                }
                // Parentheses and commas have structural meaning inside a
                // square-bracket GUI/math expression.  Outside brackets they
                // remain part of a legacy bare token for compatibility.
                if (bracketDepth > 0 && c == '(') { i++; out.add(new Token(T.LPAREN,s,i,null,false)); continue; }
                if (bracketDepth > 0 && c == ')') { i++; out.add(new Token(T.RPAREN,s,i,null,false)); continue; }
                if (bracketDepth > 0 && c == ',') { i++; out.add(new Token(T.COMMA,s,i,null,false)); continue; }
                String op = null;
                if ((c == '!' || c == '<' || c == '>' || c == '?') && i + 1 < b.length && b[i+1] == '=') { op = new String(b,s,2,java.nio.charset.StandardCharsets.ISO_8859_1); i += 2; }
                else if (c == '=' || c == '<' || c == '>') { op = Character.toString((char)c); i++; }
                if (op != null) {
                    // Inside square brackets comparisons are expression
                    // operators rather than top-level assignment separators.
                    // Preserve the same source span while exposing the
                    // conditional token class to lexer-only consumers.
                    T operatorType = bracketDepth > 0 && isConditionalOperator(op)
                            ? T.CONDITIONAL : T.OP;
                    out.add(new Token(operatorType, s, i, op, false));
                    continue;
                }
                if (bracketDepth > 0 && c == '?') {
                    i++;
                    out.add(new Token(T.CONDITIONAL, s, i, "?", false));
                    continue;
                }
                if (bracketDepth > 0 && isArithmetic(c)) {
                    // A leading sign belongs to a numeric literal only at a
                    // token boundary (`-1`, `+0.5`).  In `foo-20` the minus
                    // is an arithmetic operator and is retained separately.
                    if ((c == '+' || c == '-') && signedNumberStart(i, out)) {
                        i = scanSignedNumber(i);
                        String text = decodeUtf8(s, i);
                        out.add(new Token(T.NUMBER, s, i, null,
                                text == null || !NUMBER.matcher(text).matches()));
                    } else {
                        i++;
                        out.add(new Token(T.ARITHMETIC, s, i,
                                Character.toString((char) c), false));
                    }
                    continue;
                }
                while (i < b.length && !isDelimiter(b[i] & 0xff)
                        && !(bracketDepth > 0 && (isArithmetic(b[i] & 0xff)
                        || isBracketPunctuation(b[i] & 0xff)))) i++;
                if (i == s) { i++; diagnostics.add(new Diagnostic("INVALID_BYTE", Diagnostic.Severity.ERROR, "invalid byte in token", s, i)); out.add(new Token(T.BAD,s,i,null,true)); continue; }
                String text = decodeUtf8(s, i);
                if (text == null) {
                    // validateUtf8() has already recorded the precise source
                    // error; retain a recovery token so byte spans and
                    // round-trip output remain available to callers.
                    out.add(new Token(T.BAD, s, i, null, true));
                    continue;
                }
                T kind = text.startsWith("$") || text.startsWith("@") ? T.VARIABLE : NUMBER.matcher(text).matches() ? T.NUMBER : T.BARE;
                out.add(new Token(kind,s,i,null,false));
            }
            return out;
        }

        /** Record every malformed UTF-8 sequence without rewriting input. */
        private void validateUtf8() {
            for (int i = 0; i < b.length;) {
                int first = b[i] & 0xff;
                int length;
                int secondMin = 0x80;
                int secondMax = 0xbf;
                if (first <= 0x7f) {
                    i++;
                    continue;
                } else if (first >= 0xc2 && first <= 0xdf) {
                    length = 2;
                } else if (first == 0xe0) {
                    length = 3;
                    secondMin = 0xa0;
                } else if ((first >= 0xe1 && first <= 0xec) || (first >= 0xee && first <= 0xef)) {
                    length = 3;
                } else if (first == 0xed) {
                    length = 3;
                    secondMax = 0x9f;
                } else if (first == 0xf0) {
                    length = 4;
                    secondMin = 0x90;
                } else if (first >= 0xf1 && first <= 0xf3) {
                    length = 4;
                } else if (first == 0xf4) {
                    length = 4;
                    secondMax = 0x8f;
                } else {
                    invalidUtf8(i, i + 1);
                    i++;
                    continue;
                }
                int end = Math.min(b.length, i + length);
                boolean valid = end - i == length;
                if (valid) {
                    int second = b[i + 1] & 0xff;
                    valid = second >= secondMin && second <= secondMax;
                    for (int j = 2; valid && j < length; j++) {
                        int continuation = b[i + j] & 0xff;
                        valid = continuation >= 0x80 && continuation <= 0xbf;
                    }
                }
                if (valid) {
                    i += length;
                } else {
                    invalidUtf8(i, end);
                    // Advance one byte so a following malformed lead is not
                    // hidden behind the first recovery diagnostic.
                    i++;
                }
            }
        }

        private void invalidUtf8(int start, int end) {
            diagnostics.add(new Diagnostic("INVALID_BYTE", Diagnostic.Severity.ERROR,
                    "invalid UTF-8 byte sequence", start, Math.max(start + 1, end)));
        }

        /** Decode a token strictly; null means malformed UTF-8. */
        private String decodeUtf8(int start, int end) {
            try {
                return java.nio.charset.StandardCharsets.UTF_8.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .decode(ByteBuffer.wrap(b, start, end - start)).toString();
            } catch (CharacterCodingException ex) {
                return null;
            }
        }

        private static boolean isDelimiter(int c) { return c == ' ' || c == '\t' || c == '\f' || c == '\r' || c == '\n' || c == '#' || c == '{' || c == '}' || c == '=' || c == '<' || c == '>' || c == '?' || c == '[' || c == ']'; }

        private static boolean isArithmetic(int c) {
            return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^';
        }

        private static boolean isBracketPunctuation(int c) {
            return c == '(' || c == ')' || c == ',';
        }

        private boolean signedNumberStart(int at, List<Token> tokens) {
            if (at + 1 >= b.length) return false;
            int next = b[at + 1] & 0xff;
            if (!((next >= '0' && next <= '9')
                    || (next == '.' && at + 2 < b.length
                    && b[at + 2] >= '0' && b[at + 2] <= '9'))) return false;
            // A sign after an operand is an operator, even when there is no
            // whitespace (`base-20`).  Inspect the already-emitted token first
            // so comments/trivia do not affect this boundary decision.
            for (int n = tokens.size() - 1; n >= 0; n--) {
                Token previous = tokens.get(n);
                if (previous.type == T.TRIVIA || previous.type == T.COMMENT) continue;
                if (previous.type != T.LBRACKET && previous.type != T.LPAREN
                        && previous.type != T.COMMA && previous.type != T.ARITHMETIC
                        && previous.type != T.CONDITIONAL && previous.type != T.OP) return false;
                break;
            }
            return true;
        }

        private int scanSignedNumber(int at) {
            int i = at + 1;
            while (i < b.length) {
                int c = b[i] & 0xff;
                if ((c >= '0' && c <= '9') || c == '.'
                        || c == 'e' || c == 'E'
                        || ((c == '+' || c == '-') && i > at
                        && ((b[i - 1] & 0xff) == 'e' || (b[i - 1] & 0xff) == 'E'))) i++;
                else break;
            }
            return i;
        }

        int parseSequence(List<Token> ts, int pos, int end, List<CstNode> out, boolean inBlock) {
            while (pos < end) {
                Token t = ts.get(pos);
                if (isTrivia(t)) { out.add(node(t)); pos++; continue; }
                if (t.type == T.RBRACE) {
                    if (inBlock) return pos;
                    diagnostics.add(new Diagnostic("UNEXPECTED_RBRACE", Diagnostic.Severity.ERROR,
                            "closing brace without matching block", t.start, t.end));
                    out.add(node(SyntaxKind.ERROR, t)); pos++; continue;
                }
                if (t.type == T.LBRACE) {
                    diagnostics.add(new Diagnostic("UNEXPECTED_LBRACE", Diagnostic.Severity.ERROR,
                            "opening brace without a declaration", t.start, t.end));
                    out.add(node(SyntaxKind.ERROR, t)); pos++; continue;
                }
                if (t.type == T.RBRACKET) {
                    diagnostics.add(new Diagnostic("UNEXPECTED_RBRACKET", Diagnostic.Severity.ERROR,
                            "closing bracket without matching list or expression", t.start, t.end));
                    out.add(node(SyntaxKind.ERROR, t)); pos++; continue;
                }
                // A bracket can occur as a standalone value in GUI files and
                // in generated data.  It is not an assignment key, so parse it
                // directly instead of manufacturing a missing-operator error.
                if (t.type == T.LBRACKET) {
                    BracketResult bracket = parseBracket(ts, pos, end);
                    out.add(bracket.node());
                    pos = bracket.next();
                    continue;
                }

                int entryStart = t.start;
                List<CstNode> ec = new ArrayList<>();
                CstNode key = node(keyKind(t), t); ec.add(key); pos++;
                pos = appendTrivia(ts, pos, end, ec);

                CstNode operator = null;
                CstNode value;
                // A few GUI productions use a declaration header without an
                // equals sign (`types Name {}` and
                // `blockoverride "name" {}`).  Parse the header losslessly
                // instead of turning its braces into unmatched top-level
                // tokens.
                if (pos >= end || ts.get(pos).type != T.OP) {
                    int blockAt = isNoEqualsHeaderKey(t) ? findHeaderBlock(ts, pos, end) : -1;
                    if (blockAt >= 0) {
                        while (pos < blockAt) { ec.add(node(ts.get(pos))); pos++; }
                        BlockResult block = parseBlock(ts, pos, end);
                        value = block.node(); ec.add(value); pos = block.next();
                        out.add(new EntryNode(new SourceSpan(entryStart, value.span().end()), b, ec, key, null, value));
                        continue;
                    }
                    // `type Name = hbox { ... }` has a declaration prefix
                    // before its operator.  Restrict this recovery to the
                    // known header keyword so ordinary list items remain
                    // unambiguous.
                    int prefixOp = isHeaderKey(t) ? findHeaderOperator(ts, pos, end) : -1;
                    if (prefixOp >= 0) {
                        while (pos < prefixOp) { ec.add(node(ts.get(pos))); pos++; }
                    } else {
                        if (inBlock) {
                            out.add(new Node(SyntaxKind.LIST_ITEM,
                                    new SourceSpan(entryStart, t.end), b, ec));
                            continue;
                        }
                        diagnostics.add(new Diagnostic("MISSING_OPERATOR", Diagnostic.Severity.ERROR,
                                "expected '=' or comparison operator after key", t.start, t.end));
                        out.add(new EntryNode(new SourceSpan(entryStart, t.end), b, ec, key, null, null));
                        continue;
                    }
                }

                if (pos < end && ts.get(pos).type == T.OP) {
                    Token ot = ts.get(pos++);
                    operator = node(SyntaxKind.OPERATOR, ot); ec.add(operator);
                }
                pos = appendTrivia(ts, pos, end, ec);
                if (pos >= end || (inBlock && ts.get(pos).type == T.RBRACE)) {
                    int at = operator == null ? entryStart : operator.span().end();
                    diagnostics.add(new Diagnostic("MISSING_VALUE", Diagnostic.Severity.ERROR,
                            "operator has no value", at, at));
                    out.add(new EntryNode(new SourceSpan(entryStart, Math.max(at, t.end)), b, ec, key, operator, null));
                    continue;
                }

                ValueResult parsedValue = parseValue(ts, pos, end);
                value = parsedValue.node(); pos = parsedValue.next(); ec.add(value);
                out.add(new EntryNode(new SourceSpan(entryStart, value.span().end()), b, ec, key, operator, value));
            }
            return pos;
        }

        /** Consume trivia into an entry/header while retaining exact spans. */
        private int appendTrivia(List<Token> ts, int pos, int end, List<CstNode> out) {
            while (pos < end && isTrivia(ts.get(pos))) { out.add(node(ts.get(pos))); pos++; }
            return pos;
        }

        /**
         * Find the brace used by a no-equals GUI declaration.  GUI headers
         * permit trivia (including a line break and comments) between the
         * label and the opening brace, so the look-ahead is deliberately
         * trivia-tolerant.  We still require exactly one label token; this
         * keeps ordinary list items from absorbing an unrelated later block.
         */
        private int findHeaderBlock(List<Token> ts, int pos, int end) {
            int q = pos;
            boolean labelSeen = false;
            while (q < end) {
                Token x = ts.get(q);
                if (isTrivia(x)) {
                    q++; continue;
                }
                if (x.type == T.LBRACE) return labelSeen ? q : -1;
                if (!labelSeen && isHeaderLabel(x)) {
                    labelSeen = true;
                    q++;
                    continue;
                }
                return -1;
            }
            return -1;
        }

        private static boolean isHeaderLabel(Token t) {
            return t.type == T.BARE || t.type == T.STRING || t.type == T.NUMBER ||
                    t.type == T.VARIABLE;
        }

        /**
         * No-equals declarations are a small, known GUI grammar production.
         * Keeping the keyword allow-list here prevents malformed ordinary
         * script such as `foo bar { ... }` from being silently accepted as a
         * declaration while still covering the forms used by CK3 GUI files.
         */
        private boolean isNoEqualsHeaderKey(Token t) {
            if (t.type != T.BARE) return false;
            return Set.of("types", "template", "block", "blockoverride")
                    .contains(tokenText(t));
        }

        private int findHeaderOperator(List<Token> ts, int pos, int end) {
            int q = pos;
            while (q < end) {
                Token x = ts.get(q);
                if (isTrivia(x)) {
                    if (containsLineBreak(x)) return -1;
                    q++; continue;
                }
                if (x.type == T.OP) return q;
                if (x.type == T.LBRACE || x.type == T.RBRACE) return -1;
                q++;
            }
            return -1;
        }

        private boolean isHeaderKey(Token t) {
            return t.type == T.BARE && "type".equals(tokenText(t));
        }

        private String tokenText(Token t) {
            return new String(b, t.start, t.end - t.start, java.nio.charset.StandardCharsets.UTF_8);
        }

        private ValueResult parseValue(List<Token> ts, int pos, int end) {
            Token first = ts.get(pos);
            if (first.type == T.LBRACE) {
                BlockResult block = parseBlock(ts, pos, end);
                return new ValueResult(block.node(), block.next());
            }
            if (first.type == T.LBRACKET) {
                BracketResult bracket = parseBracket(ts, pos, end);
                return new ValueResult(bracket.node(), bracket.next());
            }
            List<CstNode> parts = new ArrayList<>();
            CstNode firstNode = node(valueKind(first), first); parts.add(firstNode);
            int valueEnd = first.end; pos++;
            if (first.malformed)
                diagnostics.add(new Diagnostic("MALFORMED_VALUE", Diagnostic.Severity.ERROR,
                        "malformed value", first.start, first.end));

            // Inline math/conditional expressions are token sequences.  A
            // same-line token followed by an operator starts the next entry;
            // a same-line brace is a typed-block suffix (`= hbox { ... }`).
            while (pos < end) {
                int q = pos; boolean lineBreak = false; List<CstNode> pending = new ArrayList<>();
                while (q < end && isTrivia(ts.get(q))) {
                    Token tr = ts.get(q); pending.add(node(tr));
                    if (containsLineBreak(tr)) { lineBreak = true; break; }
                    q++;
                }
                if (q >= end || lineBreak || ts.get(q).type == T.RBRACE) break;
                if (ts.get(q).type == T.LBRACE) {
                    parts.addAll(pending);
                    BlockResult block = parseBlock(ts, q, end);
                    parts.add(block.node()); pos = block.next(); valueEnd = block.node().span().end();
                    break;
                }
                if (ts.get(q).type == T.LBRACKET) {
                    parts.addAll(pending);
                    BracketResult bracket = parseBracket(ts, q, end);
                    parts.add(bracket.node()); pos = bracket.next(); valueEnd = bracket.node().span().end();
                    // A reader/math suffix is retained as a compound VALUE;
                    // callers can inspect the nested ExpressionNode while
                    // existing consumers remain fail-closed.
                    continue;
                }
                if (ts.get(q).type == T.RBRACKET) break;
                int look = q + 1; boolean lookBreak = false;
                while (look < end && isTrivia(ts.get(look))) {
                    if (containsLineBreak(ts.get(look))) { lookBreak = true; break; }
                    look++;
                }
                if (lookBreak || (look < end && ts.get(look).type == T.OP)) break;
                parts.addAll(pending);
                Token more = ts.get(q); parts.add(node(valueKind(more), more));
                valueEnd = more.end; pos = q + 1;
                if (more.malformed)
                    diagnostics.add(new Diagnostic("MALFORMED_VALUE", Diagnostic.Severity.ERROR,
                            "malformed value", more.start, more.end));
            }
            CstNode value = parts.size() == 1 ? parts.get(0) :
                    new Node(SyntaxKind.VALUE, new SourceSpan(first.start, valueEnd), b, parts);
            return new ValueResult(value, pos);
        }

        /** Parse a balanced square-bracket construct, retaining every token. */
        private BracketResult parseBracket(List<Token> ts, int openPos, int end) {
            Token open = ts.get(openPos);
            List<CstNode> children = new ArrayList<>();
            children.add(node(SyntaxKind.LBRACKET, open));
            int pos = openPos + 1;
            boolean closed = false;
            while (pos < end) {
                Token token = ts.get(pos);
                if (token.type == T.RBRACKET) {
                    children.add(node(SyntaxKind.RBRACKET, token));
                    pos++;
                    closed = true;
                    break;
                }
                if (token.type == T.LBRACKET) {
                    BracketResult nested = parseBracket(ts, pos, end);
                    children.add(nested.node());
                    pos = nested.next();
                    continue;
                }
                // Braces are legal in a mixed value; retain them as a normal
                // nested block instead of dropping or rewriting bytes.
                if (token.type == T.LBRACE) {
                    BlockResult block = parseBlock(ts, pos, end);
                    children.add(block.node());
                    pos = block.next();
                    continue;
                }
                children.add(node(bracketTokenKind(token), token));
                pos++;
            }
            if (!closed) {
                diagnostics.add(new Diagnostic("UNCLOSED_BRACKET", Diagnostic.Severity.ERROR,
                        "square-bracket expression is not closed with ']'", open.start,
                        Math.min(b.length, open.end)));
            }
            int endOffset = children.isEmpty() ? open.end
                    : children.get(children.size() - 1).span().end();
            BracketRole role = classifyBracket(children, open, endOffset);
            // Keep the legacy atom kinds, but expose the additional semantic
            // role for plain operands in an arithmetic expression.  Existing
            // references (parameters/scope chains/scripted variables) retain
            // their more useful role instead of being flattened to
            // MATH_OPERAND.
            if (role == BracketRole.INLINE_MATH) {
                children = markMathOperands(children);
            }
            CstNode result = role == BracketRole.LIST
                    ? new ListNode(new SourceSpan(open.start, endOffset), b, children)
                    : new ExpressionNode(new SourceSpan(open.start, endOffset), b, children, role);
            return new BracketResult(result, pos);
        }

        private List<CstNode> markMathOperands(List<CstNode> children) {
            List<CstNode> typed = new ArrayList<>(children.size());
            for (CstNode child : children) {
                if (child instanceof AtomNode atom
                        && atom.role() == AtomRole.PLAIN
                        && isMathOperandKind(atom.kind())) {
                    typed.add(new AtomNode(atom.kind(), atom.span(), b, AtomRole.MATH_OPERAND));
                } else {
                    typed.add(child);
                }
            }
            return List.copyOf(typed);
        }

        private static boolean isMathOperandKind(SyntaxKind kind) {
            return switch (kind) {
                case KEY, STRING, NUMBER, BARE_VALUE, VARIABLE,
                        PARAMETER, SCOPE_CHAIN, READER_DIRECTIVE -> true;
                default -> false;
            };
        }

        private BracketRole classifyBracket(List<CstNode> children, Token open, int endOffset) {
            String inner = new String(b, open.end, Math.max(0, endOffset - open.end),
                    java.nio.charset.StandardCharsets.UTF_8);
            // Arithmetic and comparison tokens are unambiguous. A GUI
            // expression generally has one dotted term or a function call;
            // a data list has two or more whitespace/comma-separated terms.
            boolean math = children.stream().anyMatch(n -> n.kind() == SyntaxKind.MATH_OPERATOR)
                    || inner.indexOf('*') >= 0 || inner.indexOf('/') >= 0
                    || inner.indexOf('%') >= 0 || inner.indexOf('^') >= 0;
            boolean conditional = children.stream().anyMatch(n -> n.kind() == SyntaxKind.CONDITIONAL_OPERATOR)
                    || inner.contains("!=") || inner.contains("<=") || inner.contains(">=")
                    || inner.indexOf('?') >= 0
                    || GUI_CONDITIONAL_CALL.matcher(inner).matches()
                    || GUI_IF_ELSE.matcher(inner).matches()
                    // GUI data-function predicates commonly carry a type
                    // suffix (`IsValid`, `HasFoo`, `CanClose`).  Treat a
                    // call-shaped occurrence as an inline conditional while
                    // leaving property chains such as `[Widget.GetValue]`
                    // in the generic expression role.
                    || GUI_TYPED_CALL.matcher(inner).matches();
            if (math) return BracketRole.INLINE_MATH;
            if (conditional) return BracketRole.INLINE_CONDITIONAL;
            long terms = children.stream().filter(this::isBracketTerm).count();
            boolean comma = children.stream().anyMatch(n -> n.kind() == SyntaxKind.COMMA);
            boolean callable = inner.indexOf('(') >= 0 || inner.indexOf(')') >= 0;
            // An empty bracket pair has no callable/property signal and is
            // the unambiguous empty-list spelling.  Non-empty single-term
            // brackets stay expressions because that is the common CK3 GUI
            // form (`[Widget.GetValue]`).
            if ((terms == 0 && !callable) || comma || (terms > 1 && !callable)) {
                return BracketRole.LIST;
            }
            return BracketRole.INLINE_EXPRESSION;
        }

        private boolean isBracketTerm(CstNode n) {
            return switch (n.kind()) {
                case LBRACKET, RBRACKET, LPAREN, RPAREN, COMMA,
                        BOM, COMMENT, WHITESPACE, NEWLINE,
                        MATH_OPERATOR, CONDITIONAL_OPERATOR -> false;
                default -> true;
            };
        }

        private record BracketResult(CstNode node, int next) { }

        private BlockResult parseBlock(List<Token> ts, int openPos, int end) {
            Token open = ts.get(openPos); List<CstNode> children = new ArrayList<>();
            children.add(node(SyntaxKind.LBRACE, open));
            int pos = parseSequence(ts, openPos + 1, end, children, true);
            if (pos < end && ts.get(pos).type == T.RBRACE) {
                children.add(node(SyntaxKind.RBRACE, ts.get(pos))); pos++;
            } else {
                int at = pos < end ? ts.get(pos).start : b.length;
                diagnostics.add(new Diagnostic("UNCLOSED_BLOCK", Diagnostic.Severity.ERROR,
                        "block is not closed with '}'", open.start, at));
            }
            int blockEnd = children.isEmpty() ? open.end : children.get(children.size() - 1).span().end();
            return new BlockResult(new BlockNode(new SourceSpan(open.start, blockEnd), b, children), pos);
        }

        private record BlockResult(BlockNode node, int next) { }
        private record ValueResult(CstNode node, int next) { }

        private boolean isTrivia(Token t) { return t.type == T.TRIVIA || t.type == T.COMMENT; }
        private CstNode node(Token t) {
            SyntaxKind k = t.type == T.COMMENT ? SyntaxKind.COMMENT : t.type == T.TRIVIA ? ((t.start == 0 && t.end == 3) ? SyntaxKind.BOM : (containsLineBreak(t) ? SyntaxKind.NEWLINE : SyntaxKind.WHITESPACE)) : valueKind(t);
            return node(k, t);
        }
        private boolean containsLineBreak(Token t) { for (int i=t.start; i<t.end; i++) if (b[i]=='\r' || b[i]=='\n') return true; return false; }
        private CstNode node(SyntaxKind k, Token t) {
            SourceSpan span = new SourceSpan(t.start, t.end);
            if (k == SyntaxKind.KEY || k == SyntaxKind.READER_DIRECTIVE
                    || k == SyntaxKind.STRING || k == SyntaxKind.NUMBER
                    || k == SyntaxKind.BARE_VALUE || k == SyntaxKind.VARIABLE
                    || k == SyntaxKind.PARAMETER || k == SyntaxKind.SCOPE_CHAIN) {
                return new AtomNode(k, span, b, atomRole(k, t));
            }
            return new Node(k, span, b);
        }
        private SyntaxKind keyKind(Token t) {
            if (t.type == T.VARIABLE && tokenText(t).startsWith("@")) return SyntaxKind.READER_DIRECTIVE;
            return t.type == T.VARIABLE ? SyntaxKind.VARIABLE : SyntaxKind.KEY;
        }
        private static SyntaxKind valueKind(Token t) {
            return switch (t.type) {
                case STRING -> SyntaxKind.STRING;
                case NUMBER -> SyntaxKind.NUMBER;
                case VARIABLE -> SyntaxKind.VARIABLE;
                case BAD -> SyntaxKind.ERROR;
                case ARITHMETIC -> SyntaxKind.MATH_OPERATOR;
                case CONDITIONAL -> SyntaxKind.CONDITIONAL_OPERATOR;
                case LBRACKET -> SyntaxKind.LBRACKET;
                case RBRACKET -> SyntaxKind.RBRACKET;
                case LPAREN -> SyntaxKind.LPAREN;
                case RPAREN -> SyntaxKind.RPAREN;
                case COMMA -> SyntaxKind.COMMA;
                default -> SyntaxKind.BARE_VALUE;
            };
        }
        private SyntaxKind bracketTokenKind(Token t) {
            return switch (t.type) {
                case TRIVIA -> t.start == 0 && t.end == 3
                        ? SyntaxKind.BOM
                        : containsLineBreak(t) ? SyntaxKind.NEWLINE : SyntaxKind.WHITESPACE;
                case COMMENT -> SyntaxKind.COMMENT;
                case LBRACKET -> SyntaxKind.LBRACKET;
                case RBRACKET -> SyntaxKind.RBRACKET;
                case LPAREN -> SyntaxKind.LPAREN;
                case RPAREN -> SyntaxKind.RPAREN;
                case COMMA -> SyntaxKind.COMMA;
                case ARITHMETIC -> SyntaxKind.MATH_OPERATOR;
                case CONDITIONAL -> SyntaxKind.CONDITIONAL_OPERATOR;
                case OP -> isConditionalOperator(t.op)
                        ? SyntaxKind.CONDITIONAL_OPERATOR : SyntaxKind.OPERATOR;
                default -> valueKind(t);
            };
        }

        private static boolean isConditionalOperator(String operator) {
            return "=".equals(operator) || "!=".equals(operator) || "<".equals(operator)
                    || "<=".equals(operator) || ">".equals(operator)
                    || ">=".equals(operator) || "?=".equals(operator);
        }

        private AtomRole atomRole(SyntaxKind kind, Token t) {
            String text = tokenText(t);
            if (kind == SyntaxKind.READER_DIRECTIVE
                    || (kind == SyntaxKind.KEY && text.startsWith("@"))) return AtomRole.READER_DIRECTIVE;
            // Quoted strings and numeric literals are atoms too, but a '$'
            // or ':' inside their payload is data rather than a script
            // reference.  Restrict semantic role inference to identifier-like
            // token kinds so a localized string such as `"$NAME$"` remains a
            // plain STRING node.
            boolean referenceLike = kind == SyntaxKind.KEY
                    || kind == SyntaxKind.BARE_VALUE
                    || kind == SyntaxKind.VARIABLE
                    || kind == SyntaxKind.PARAMETER
                    || kind == SyntaxKind.SCOPE_CHAIN;
            if (!referenceLike) return AtomRole.PLAIN;
            if (PARAMETER.matcher(text).matches() || (text.startsWith("$") && text.endsWith("$"))) return AtomRole.PARAMETER;
            if (text.indexOf('$') >= 0 && text.indexOf('$') != text.lastIndexOf('$')) return AtomRole.INTERPOLATED_PARAMETER;
            if (text.startsWith("@")) return AtomRole.SCRIPTED_VARIABLE;
            if (isScopeChainText(text)) return AtomRole.SCOPE_CHAIN;
            if (isConditionalKeyText(text)) return AtomRole.CONDITIONAL_KEY;
            return AtomRole.PLAIN;
        }

        private static boolean isScopeChainText(String text) {
            if (text.equalsIgnoreCase("root") || text.equalsIgnoreCase("this") || text.equalsIgnoreCase("prev")) return true;
            return SCOPE_REFERENCE.matcher(text).matches()
                    || DOT_SCOPE_REFERENCE.matcher(text).matches();
        }

        private static boolean isConditionalKeyText(String text) {
            return switch (text.toLowerCase(java.util.Locale.ROOT)) {
                case "if", "else", "else_if", "limit", "trigger_if", "effect_if",
                        "random", "random_list", "switch", "while", "unless",
                        "not", "and", "or", "any", "all", "any_of", "all_of" -> true;
                default -> false;
            };
        }
    }
}
