package fr.quatrevieux.araknemu.game.fight.turn;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.turn.event.NextTurnInitiated;
import fr.quatrevieux.araknemu.game.fight.turn.event.TurnStarted;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FightTurnListTest extends FightBaseCase {
    private Fight fight;
    private FightTurnList turnList;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        turnList = new FightTurnList(fight);
    }

    @Test
    void initWillOrderFighters() {
        turnList.init(new AlternateTeamFighterOrder());

        assertEquals(
            Arrays.asList(player.fighter(), other.fighter()),
            turnList.fighters()
        );
    }

    @Test
    void initAlreadyInitialised() {
        turnList.init(new AlternateTeamFighterOrder());

        assertThrows(IllegalStateException.class, () -> turnList.init(new AlternateTeamFighterOrder()));
    }

    @Test
    void currentNotStarted() {
        assertFalse(turnList.current().isPresent());
    }

    @Test
    void start() {
        turnList.init(new AlternateTeamFighterOrder());
        turnList.start();

        assertTrue(turnList.current().isPresent());
        assertSame(player.fighter(), turnList.current().get().fighter());
        assertSame(turnList.current().get(), player.fighter().turn());
    }

    @Test
    void nextWillStartNextFighterTurn() {
        turnList.init(new AlternateTeamFighterOrder());
        turnList.start();

        AtomicReference<TurnStarted> ref1 = new AtomicReference<>();
        AtomicReference<NextTurnInitiated> ref2 = new AtomicReference<>();
        fight.dispatcher().add(
            new Listener<TurnStarted>() {
                @Override
                public void on(TurnStarted event) {
                    ref1.set(event);
                }

                @Override
                public Class<TurnStarted> event() {
                    return TurnStarted.class;
                }
            }
        );
        fight.dispatcher().add(
            new Listener<NextTurnInitiated>() {
                @Override
                public void on(NextTurnInitiated event) {
                    ref2.set(event);
                }

                @Override
                public Class<NextTurnInitiated> event() {
                    return NextTurnInitiated.class;
                }
            }
        );

        turnList.next();

        assertSame(other.fighter(), turnList.current().get().fighter());
        assertSame(other.fighter().turn(), turnList.current().get());
        assertSame(other.fighter().turn(), ref1.get().turn());
        assertSame(other.fighter(), turnList.currentFighter());
        assertNotNull(ref2.get());
    }

    @Test
    void nextOnEndOfListWillRestartToFirst() {
        turnList.init(new AlternateTeamFighterOrder());
        turnList.start();

        turnList.next();
        turnList.next();

        assertSame(player.fighter(), turnList.current().get().fighter());
    }

    @Test
    void nextWillSkipDeadFighter() {
        fight.fighters().forEach(Fighter::init);
        turnList.init(new AlternateTeamFighterOrder());
        turnList.start();

        other.fighter().life().alter(other.fighter(), -1000);
        assertTrue(other.fighter().dead());

        turnList.next();
        assertSame(player.fighter(), turnList.current().get().fighter());
        assertSame(player.fighter(), turnList.currentFighter());
    }

    @Test
    void stop() {
        turnList.init(new AlternateTeamFighterOrder());
        turnList.start();

        turnList.stop();

        assertFalse(turnList.current().isPresent());
        assertSame(player.fighter(), turnList.currentFighter());
    }

    @Test
    void stopNotActive() {
        turnList.stop();
    }
}
