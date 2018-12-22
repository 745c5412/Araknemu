package fr.quatrevieux.araknemu.game.fight.turn.order;

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffList;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.States;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlternateTeamFighterOrderTest extends TestCase {
    private AlternateTeamFighterOrder strategy = new AlternateTeamFighterOrder();

    class TeamStub implements FightTeam {
        final private List<Fighter> fighters;

        public TeamStub(List<Fighter> fighters) {
            this.fighters = fighters;
        }

        @Override
        public Fighter leader() {
            return fighters.get(0);
        }

        @Override
        public int id() {
            return fighters.get(0).id();
        }

        @Override
        public int cell() {
            return 123;
        }

        @Override
        public int type() {
            return 0;
        }

        @Override
        public Alignment alignment() {
            return Alignment.NONE;
        }

        @Override
        public int number() {
            return 0;
        }

        @Override
        public List<Integer> startPlaces() {
            return null;
        }

        @Override
        public Collection<? extends Fighter> fighters() {
            return fighters;
        }

        @Override
        public void send(Object packet) {}

        @Override
        public boolean alive() { return true; }

        @Override
        public void join(Fighter fighter) throws JoinFightException {
            throw new JoinFightException(JoinFightError.CANT_DO_TOO_LATE);
        }

        @Override
        public void kick(Fighter fighter) {}
    }

    class FighterStub implements Fighter {
        final private int id;
        final private int init;

        public FighterStub(int id, int init) {
            this.id = id;
            this.init = init;
        }

        @Override
        public int level() {
            return 0;
        }

        @Override
        public int id() {
            return id;
        }

        @Override
        public FightCell cell() {
            return null;
        }

        @Override
        public Direction orientation() {
            return Direction.SOUTH_EAST;
        }

        @Override
        public void setOrientation(Direction orientation) {

        }

        @Override
        public void move(FightCell cell) {}

        @Override
        public Sprite sprite() {
            return null;
        }

        @Override
        public void init() {}

        @Override
        public FighterLife life() {
            return null;
        }

        @Override
        public boolean dead() {
            return false;
        }

        @Override
        public CastableWeapon weapon() {
            return null;
        }

        @Override
        public boolean isOnFight() {
            return true;
        }

        @Override
        public FighterCharacteristics characteristics() {
            return new FighterCharacteristics() {
                @Override
                public int initiative() {
                    return init;
                }

                @Override
                public int get(Characteristic characteristic) {
                    return 0;
                }

                @Override
                public void alter(Characteristic characteristic, int value) {

                }
            };
        }

        @Override
        public SpellList spells() {
            return null;
        }

        @Override
        public FightTeam team() {
            return null;
        }

        @Override
        public Fight fight() {
            return null;
        }

        @Override
        public void joinFight(Fight fight, FightCell startCell) {}

        @Override
        public boolean ready() {
            return false;
        }

        @Override
        public void dispatch(Object event) {}

        @Override
        public void play(FightTurn turn) {}

        @Override
        public void stop() {}

        @Override
        public BuffList buffs() {
            return null;
        }

        @Override
        public States states() {
            return null;
        }

        @Override
        public void attach(Object key, Object value) {

        }

        @Override
        public Object attachment(Object key) {
            return null;
        }
    }

    @Test
    void computeWithTwoFighters() {
        List<Fighter> fighters = strategy.compute(
            Arrays.asList(
                new TeamStub(Arrays.asList(new FighterStub(1, 100))),
                new TeamStub(Arrays.asList(new FighterStub(2, 500)))
            )
        );

        assertCount(2, fighters);
        assertEquals(2, fighters.get(0).id());
        assertEquals(1, fighters.get(1).id());
    }

    @Test
    void computeWithMultipleFightersWithOnePerTeam() {
        List<Fighter> fighters = strategy.compute(
            Arrays.asList(
                new TeamStub(Arrays.asList(new FighterStub(1, 100))),
                new TeamStub(Arrays.asList(new FighterStub(2, 500))),
                new TeamStub(Arrays.asList(new FighterStub(3, 110))),
                new TeamStub(Arrays.asList(new FighterStub(4, 950))),
                new TeamStub(Arrays.asList(new FighterStub(5, 550)))
            )
        );

        assertCount(5, fighters);
        assertEquals(4, fighters.get(0).id());
        assertEquals(5, fighters.get(1).id());
        assertEquals(2, fighters.get(2).id());
        assertEquals(3, fighters.get(3).id());
        assertEquals(1, fighters.get(4).id());
    }

    @Test
    void computeWithMultipleFightersWithTwoTeamOfSameSize() {
        List<Fighter> fighters = strategy.compute(
            Arrays.asList(
                new TeamStub(Arrays.asList(
                    new FighterStub(1, 100),
                    new FighterStub(3, 450),
                    new FighterStub(4, 220)
                )),
                new TeamStub(Arrays.asList(
                    new FighterStub(2, 500),
                    new FighterStub(5, 100),
                    new FighterStub(6, 120)
                ))
            )
        );

        assertCount(6, fighters);
        assertEquals(2, fighters.get(0).id());
        assertEquals(3, fighters.get(1).id());
        assertEquals(6, fighters.get(2).id());
        assertEquals(4, fighters.get(3).id());
        assertEquals(5, fighters.get(4).id());
        assertEquals(1, fighters.get(5).id());
    }

    @Test
    void computeWithMultipleFightersWithTwoTeamWithDifferentSizeWillSetFighterOfBiggestTeamAtTheEnd() {
        List<Fighter> fighters = strategy.compute(
            Arrays.asList(
                new TeamStub(Arrays.asList(
                    new FighterStub(1, 100),
                    new FighterStub(2, 500),
                    new FighterStub(3, 450),
                    new FighterStub(4, 220)
                )),
                new TeamStub(Arrays.asList(
                    new FighterStub(5, 100),
                    new FighterStub(6, 120)
                ))
            )
        );

        assertCount(6, fighters);
        assertEquals(2, fighters.get(0).id());
        assertEquals(6, fighters.get(1).id());
        assertEquals(3, fighters.get(2).id());
        assertEquals(5, fighters.get(3).id());
        assertEquals(4, fighters.get(4).id());
        assertEquals(1, fighters.get(5).id());
    }
}
