package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.event.RestrictionsChanged;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RestrictionsTest extends GameBaseCase {
    private Restrictions restrictions;
    private GamePlayer player;
    private ExplorationPlayer exploration;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = gamePlayer();
        exploration = explorationPlayer();
        restrictions = new Restrictions(exploration);
    }

    @Test
    void defaults() {
        assertTrue(restrictions.canAssault());
        assertTrue(restrictions.canChallenge());
        assertTrue(restrictions.canExchange());
        assertTrue(restrictions.canAttack());
        assertFalse(restrictions.forceWalk());
        assertFalse(restrictions.isSlow());
        assertFalse(restrictions.isTomb());

        assertEquals(0, restrictions.toInt());
    }

    @Test
    void refreshDefaults() {
        restrictions.refresh();

        assertTrue(restrictions.canAssault());
        assertTrue(restrictions.canChallenge());
        assertFalse(restrictions.canExchange());
        assertFalse(restrictions.canAttack());
        assertFalse(restrictions.forceWalk());
        assertFalse(restrictions.isSlow());
        assertFalse(restrictions.isTomb());

        assertEquals(12, restrictions.toInt());
    }

    @Test
    void refreshWithoutChanges() {
        restrictions.refresh();

        AtomicReference<RestrictionsChanged> ref = new AtomicReference<>();

        exploration.map().dispatcher().add(RestrictionsChanged.class, ref::set);
        restrictions.refresh();

        assertNull(ref.get());
    }

    @Test
    void refreshWithChallenge() {
        restrictions.refresh();
        exploration.player().restrictions().set(fr.quatrevieux.araknemu.game.player.Restrictions.Restriction.DENY_CHALLENGE);

        AtomicReference<RestrictionsChanged> ref = new AtomicReference<>();

        exploration.map().dispatcher().add(RestrictionsChanged.class, ref::set);
        restrictions.refresh();

        assertSame(exploration, ref.get().player());
        assertSame(restrictions, ref.get().restrictions());

        assertFalse(restrictions.canChallenge());
    }

    @Test
    void refreshWithAssault() {
        restrictions.refresh();
        exploration.player().restrictions().set(fr.quatrevieux.araknemu.game.player.Restrictions.Restriction.DENY_ASSAULT);

        AtomicReference<RestrictionsChanged> ref = new AtomicReference<>();

        exploration.map().dispatcher().add(RestrictionsChanged.class, ref::set);
        restrictions.refresh();

        assertSame(exploration, ref.get().player());
        assertSame(restrictions, ref.get().restrictions());

        assertFalse(restrictions.canAssault());
    }

    @Test
    void refreshWithAttack() {
        restrictions.refresh();
        exploration.player().restrictions().set(fr.quatrevieux.araknemu.game.player.Restrictions.Restriction.ALLOW_ATTACK);

        AtomicReference<RestrictionsChanged> ref = new AtomicReference<>();

        exploration.map().dispatcher().add(RestrictionsChanged.class, ref::set);
        restrictions.refresh();

        assertSame(exploration, ref.get().player());
        assertSame(restrictions, ref.get().restrictions());

        assertTrue(restrictions.canAttack());
    }

    @Test
    void functionalRestrictionChangedChain() {
        requestStack.clear();

        player.restrictions().set(fr.quatrevieux.araknemu.game.player.Restrictions.Restriction.DENY_CHALLENGE);

        assertFalse(exploration.restrictions().canChallenge());
        requestStack.assertLast(new AddSprites(Collections.singleton(exploration.sprite())));
    }
}