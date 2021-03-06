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

package fr.quatrevieux.araknemu.network.game.out.exchange.movement.distant;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;

import java.util.List;

/**
 * Set the object quantity on the distant exchange
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L761
 */
final public class DistantExchangeObject {
    final static private Transformer<List<ItemTemplateEffectEntry>> effectsTransformer = new ItemEffectsTransformer();

    final private ItemEntry entry;
    final private int quantity;

    public DistantExchangeObject(ItemEntry entry, int quantity) {
        this.entry = entry;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return quantity > 0
            ? "EmKO+" + entry.id() + "|" + quantity + "|" + entry.templateId() + "|" + effectsTransformer.serialize(entry.effects())
            : "EmKO-" + entry.id()
        ;
    }
}
