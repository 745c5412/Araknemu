package fr.quatrevieux.araknemu.game.handler.exchange.movement;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.exchange.movement.ItemsMovement;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Set items into the exchange
 */
final public class SetExchangeItems implements PacketHandler<GameSession, ItemsMovement> {
    @Override
    public void handle(GameSession session, ItemsMovement packet) {
        final ExplorationPlayer player = session.exploration();

        player.interactions().get(ExchangeDialog.class).item(
            player.inventory().get(packet.id()),
            packet.quantity()
        );
    }

    @Override
    public Class<ItemsMovement> packet() {
        return ItemsMovement.class;
    }
}