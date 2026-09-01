package com.xenoamess.kaishek.runtime;

import com.xenoamess.kaishek.profile.ScopeType;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Synthetic contract for explicit ROOT/THIS/PREV, aliases and scope links. */
class ScopeContextChainContractTest {
    private static ScopeRef ref(ScopeType type, String id) { return new ScopeRef(type, id); }

    @Test
    void resolvesBaseAliasesAndMultiHopScopeChains() {
        ScopeRef root = ref(ScopeType.CHARACTER, "root-char");
        ScopeRef current = ref(ScopeType.CHARACTER, "actor");
        ScopeRef previous = ref(ScopeType.TITLE, "previous-title");
        ScopeRef liege = ref(ScopeType.TITLE, "liege");
        ScopeRef realm = ref(ScopeType.ORGANIZATION, "realm");
        ScopeContext context = new ScopeContext(root, current, previous)
                .withAlias("actor", current)
                .withLink("actor", "liege", liege)
                .withLink("scope:actor.liege", "realm", realm);

        assertEquals(root, context.resolve("ROOT"));
        assertEquals(current, context.resolve("this"));
        assertEquals(previous, context.resolve("PREV"));
        assertEquals(liege, context.resolve("scope:actor.liege"));
        assertEquals(realm, context.resolve("actor.liege.realm"));
        assertEquals(List.of("actor", "liege", "realm"),
                context.resolveDetailed("actor.liege.realm").path());
    }

    @Test
    void unresolvedBaseLinkAndCycleFailClosedWithStableReasons() {
        ScopeRef root = ref(ScopeType.CHARACTER, "root");
        ScopeContext context = new ScopeContext(root, root, null)
                .withLink("ROOT", "self", root);
        assertNull(context.resolve("missing"));
        assertEquals("UNRESOLVED_BASE", context.resolveDetailed("missing").reason());
        assertNull(context.resolve("ROOT.missing"));
        assertEquals("UNRESOLVED_LINK", context.resolveDetailed("ROOT.missing").reason());
        assertNull(context.resolve("ROOT.self"));
        assertEquals("CYCLIC_CHAIN", context.resolveDetailed("ROOT.self").reason());
        assertEquals("MALFORMED_CHAIN", context.resolveDetailed("ROOT..self").reason());
    }

    @Test
    void immutableUpdatesDoNotLeakLinksOrAliases() {
        ScopeRef root = ref(ScopeType.CHARACTER, "root");
        ScopeRef target = ref(ScopeType.TITLE, "target");
        ScopeContext original = new ScopeContext(root, root, null);
        ScopeContext updated = original.withAlias("saved", target).withLink("saved", "owner", root);
        assertNull(original.resolve("saved"));
        assertTrue(original.links().isEmpty());
        assertEquals(root, updated.resolve("saved.owner"));
    }
}
