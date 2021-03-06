/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterStateChanged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler fighter states
 */
final public class States {
    final private Fighter fighter;

    final private Map<Integer, Integer> states = new HashMap<>();

    public States(Fighter fighter) {
        this.fighter = fighter;
    }

    /**
     * Add new states to the fighter states
     * If the state is already set, alter the duration if and only if the new duration is higher than the old one
     *
     * @param state The state to push
     * @param duration The state duration. Set to -1 for indefinite duration
     */
    public void push(int state, int duration) {
        boolean defined = states.containsKey(state);

        if (defined && !higherDuration(states.get(state), duration)) {
            return;
        }

        states.put(state, duration);

        fighter.fight().dispatch(
            new FighterStateChanged(
                fighter,
                state,
                defined ? FighterStateChanged.Type.UPDATE : FighterStateChanged.Type.ADD
            )
        );
    }

    /**
     * Add new states to the fighter states with infinite duration
     *
     * @param state The state to push
     */
    public void push(int state) {
        push(state, -1);
    }

    /**
     * Remove the state
     *
     * @param state State to remove
     */
    public void remove(int state) {
        if (states.remove(state) != null) {
            fighter.fight().dispatch(new FighterStateChanged(fighter, state, FighterStateChanged.Type.REMOVE));
        }
    }

    /**
     * Check if the fighter has the given state
     *
     * @param state State to check
     */
    public boolean has(int state) {
        return states.containsKey(state);
    }

    /**
     * Check if the fighter has all the given states
     *
     * @param states States to check
     */
    public boolean hasAll(int[] states) {
        for (int state : states) {
            if (!has(state)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the fighter has at least one of the given states
     *
     * @param states States to check
     */
    public boolean hasOne(int[] states) {
        for (int state : states) {
            if (has(state)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Decrement remaining turns on states and remove expired states
     */
    public void refresh() {
        for (int state : new ArrayList<>(states.keySet())) {
            int turns = states.get(state);

            switch (turns) {
                case -1:
                    break;

                case 0:
                case 1:
                    remove(state);
                    break;

                default:
                    states.put(state, turns - 1);
            }
        }
    }

    private boolean higherDuration(int baseDuration, int newDuration) {
        if (baseDuration == -1) {
            return false;
        }

        if (newDuration == -1) {
            return true;
        }

        return newDuration > baseDuration;
    }
}
