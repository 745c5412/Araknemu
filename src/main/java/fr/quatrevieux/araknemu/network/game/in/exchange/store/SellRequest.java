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

package fr.quatrevieux.araknemu.network.game.in.exchange.store;

import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.ParsePacketException;
import fr.quatrevieux.araknemu.core.network.parser.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Sell an item to an NPC
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L70
 */
final public class SellRequest implements Packet {
    final static public class Parser implements SinglePacketParser<SellRequest> {
        @Override
        public SellRequest parse(String input) throws ParsePacketException {
            final String[] parts = StringUtils.split(input, "|", 2);

            if (parts.length != 2) {
                throw new ParsePacketException(code() + input, "Invalid parts number");
            }

            return new SellRequest(
                Integer.parseInt(parts[0]),
                Integer.parseUnsignedInt(parts[1])
            );
        }

        @Override
        public String code() {
            return "ES";
        }
    }

    final private int itemId;
    final private int quantity;

    public SellRequest(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    /**
     * The item entry id to sell
     */
    public int itemId() {
        return itemId;
    }

    /**
     * Quantity to sell
     */
    public int quantity() {
        return quantity;
    }
}
