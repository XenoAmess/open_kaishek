package com.xenoamess.kaishek.runtime;

import java.util.*;

/**
 * Immutable scope frame used by the finite runtime.
 *
 * <p>A reference starts at ROOT, THIS, PREV or a saved alias and then follows
 * explicitly registered links (for example {@code scope:actor.liege}). A
 * missing base or link is unresolved and is deliberately returned as such;
 * this class does not infer links from ids or scope kinds.</p>
 */
public final class ScopeContext {
    private static final int MAX_CHAIN_DEPTH = 64;
    private final ScopeRef root, current, previous;
    private final Map<String, ScopeRef> saved;
    private final Map<ScopeRef, Map<String, ScopeRef>> links;

    public ScopeContext(ScopeRef root, ScopeRef current, ScopeRef previous) {
        this(root, current, previous, Map.of(), Map.of());
    }
    public ScopeContext(ScopeRef root, ScopeRef current, ScopeRef previous,
                        Map<String, ScopeRef> saved) {
        this(root, current, previous, saved, Map.of());
    }
    private ScopeContext(ScopeRef root, ScopeRef current, ScopeRef previous,
                         Map<String, ScopeRef> saved,
                         Map<ScopeRef, Map<String, ScopeRef>> links) {
        this.root = Objects.requireNonNull(root, "root");
        this.current = Objects.requireNonNull(current, "current");
        this.previous = previous;
        this.saved = Map.copyOf(saved == null ? Map.of() : saved);
        Map<ScopeRef, Map<String, ScopeRef>> copy = new LinkedHashMap<>();
        if (links != null) {
            for (Map.Entry<ScopeRef, Map<String, ScopeRef>> entry : links.entrySet()) {
                Map<String, ScopeRef> sourceLinks = entry.getValue();
                if (sourceLinks == null) continue;
                Map<String, ScopeRef> checked = new LinkedHashMap<>();
                for (Map.Entry<String, ScopeRef> link : sourceLinks.entrySet()) {
                    checked.put(validatePart(link.getKey(), "link name"),
                            Objects.requireNonNull(link.getValue(), "link target"));
                }
                copy.put(Objects.requireNonNull(entry.getKey(), "link source"), Map.copyOf(checked));
            }
        }
        this.links = Map.copyOf(copy);
    }
    public ScopeRef root() { return root; }
    public ScopeRef current() { return current; }
    public ScopeRef previous() { return previous; }
    public Map<String, ScopeRef> saved() { return saved; }
    public Map<ScopeRef, Map<String, ScopeRef>> links() { return links; }

    /** Resolve a parser/IR scope reference, returning null when unresolved. */
    public ScopeRef resolve(String token) { return resolveDetailed(token).value(); }

    /** Resolve with a machine-readable reason and traversed path. */
    public ScopeResolution resolveDetailed(String token) {
        if (token == null || token.isBlank())
            return ScopeResolution.unresolved(token, "EMPTY_REFERENCE", List.of());
        String raw = token.trim();
        String[] parts = raw.split("\\.", -1);
        if (parts.length == 0 || parts.length > MAX_CHAIN_DEPTH + 1
                || Arrays.stream(parts).anyMatch(String::isBlank))
            return ScopeResolution.unresolved(raw, "MALFORMED_CHAIN", List.of());
        String first = parts[0];
        if (first.regionMatches(true, 0, "scope:", 0, "scope:".length()))
            first = first.substring("scope:".length());
        if (first.isBlank()) return ScopeResolution.unresolved(raw, "EMPTY_BASE", List.of());
        ScopeRef resolved = resolveBase(first);
        List<String> path = new ArrayList<>(); path.add(first);
        if (resolved == null) return ScopeResolution.unresolved(raw, "UNRESOLVED_BASE", path);
        Set<ScopeRef> visited = new HashSet<>(); visited.add(resolved);
        for (int i = 1; i < parts.length; i++) {
            String linkName = parts[i]; path.add(linkName);
            ScopeRef next = links.getOrDefault(resolved, Map.of()).get(linkName);
            if (next == null) return ScopeResolution.unresolved(raw, "UNRESOLVED_LINK", path);
            if (!visited.add(next)) return ScopeResolution.unresolved(raw, "CYCLIC_CHAIN", path);
            resolved = next;
        }
        return ScopeResolution.resolved(raw, resolved, path);
    }

    private ScopeRef resolveBase(String token) {
        if (token.equalsIgnoreCase("ROOT")) return root;
        if (token.equalsIgnoreCase("THIS")) return current;
        if (token.equalsIgnoreCase("PREV")) return previous;
        return saved.get(token);
    }
    public ScopeContext withCurrent(ScopeRef next) {
        return new ScopeContext(root, Objects.requireNonNull(next, "current"), current, saved, links);
    }
    public ScopeContext save(String name, ScopeRef ref) { return withAlias(name, ref); }
    public ScopeContext withAlias(String name, ScopeRef ref) {
        Map<String, ScopeRef> copy = new LinkedHashMap<>(saved);
        copy.put(validatePart(name, "alias"), Objects.requireNonNull(ref, "alias target"));
        return new ScopeContext(root, current, previous, copy, links);
    }
    public ScopeContext withAlias(String name, String targetToken) {
        ScopeRef target = resolve(targetToken);
        if (target == null) throw new IllegalArgumentException("unresolved alias target: " + targetToken);
        return withAlias(name, target);
    }
    public ScopeContext withLink(String sourceToken, String linkName, ScopeRef target) {
        ScopeRef source = resolve(sourceToken);
        if (source == null) throw new IllegalArgumentException("unresolved link source: " + sourceToken);
        String part = validatePart(linkName, "link name");
        Map<ScopeRef, Map<String, ScopeRef>> copy = new LinkedHashMap<>(links);
        Map<String, ScopeRef> sourceLinks = new LinkedHashMap<>(copy.getOrDefault(source, Map.of()));
        sourceLinks.put(part, Objects.requireNonNull(target, "link target")); copy.put(source, sourceLinks);
        return new ScopeContext(root, current, previous, saved, copy);
    }
    private static String validatePart(String value, String label) {
        if (value == null || value.isBlank() || value.indexOf('.') >= 0 || value.indexOf(':') >= 0)
            throw new IllegalArgumentException("invalid " + label);
        return value;
    }

    public record ScopeResolution(String token, ScopeRef value, String reason, List<String> path) {
        public ScopeResolution {
            path = List.copyOf(path == null ? List.of() : path);
            if (value != null && reason != null) throw new IllegalArgumentException("resolved result has reason");
            if (value == null && (reason == null || reason.isBlank())) throw new IllegalArgumentException("unresolved result needs reason");
        }
        public boolean resolved() { return value != null; }
        private static ScopeResolution resolved(String token, ScopeRef value, List<String> path) { return new ScopeResolution(token, Objects.requireNonNull(value), null, path); }
        private static ScopeResolution unresolved(String token, String reason, List<String> path) { return new ScopeResolution(token, null, reason, path); }
    }
}
