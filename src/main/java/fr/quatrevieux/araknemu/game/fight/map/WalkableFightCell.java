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

package fr.quatrevieux.araknemu.game.fight.map;

import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.fight.exception.FightMapException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;

import java.util.Optional;

/**
 * Base fight cell
 */
final public class WalkableFightCell implements FightCell {
    final private FightMap map;
    final private MapTemplate.Cell template;
    final private int id;

    private Fighter fighter;

    public WalkableFightCell(FightMap map, MapTemplate.Cell template, int id) {
        this.map = map;
        this.template = template;
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public FightMap map() {
        return map;
    }

    @Override
    public boolean walkable() {
        return fighter == null;
    }

    @Override
    public boolean walkableIgnoreFighter() {
        return true;
    }

    @Override
    public boolean sightBlocking() {
        return !template.lineOfSight() || fighter != null;
    }

    @Override
    public Optional<Fighter> fighter() {
        return Optional.ofNullable(fighter);
    }

    @Override
    public void set(Fighter fighter) {
        if (this.fighter != null) {
            throw new FightMapException("A fighter is already set on this cell (" + id + ")");
        }

        this.fighter = fighter;
    }

    @Override
    public void removeFighter() {
        if (this.fighter == null) {
            throw new FightMapException("No fighter found on cell " + id);
        }

        this.fighter = null;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WalkableFightCell && equals((FightCell) obj);
    }
}
