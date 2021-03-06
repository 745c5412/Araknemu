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

package fr.quatrevieux.araknemu.game.player.inventory.event;

import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

/**
 * Event trigger when equipment is changed
 */
final public class EquipmentChanged {
    final private ItemEntry entry;
    final private int slot;
    final private boolean equiped;

    public EquipmentChanged(ItemEntry entry, int slot, boolean equiped) {
        this.entry   = entry;
        this.slot    = slot;
        this.equiped = equiped;
    }

    /**
     * Get the item entry
     */
    public ItemEntry entry() {
        return entry;
    }

    /**
     * Get the changed slot id
     */
    public int slot() {
        return slot;
    }

    /**
     * Does the item is equiped or not ?
     */
    public boolean equiped() {
        return equiped;
    }
}
