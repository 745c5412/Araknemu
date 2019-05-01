package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.exploration.event.*;
import fr.quatrevieux.araknemu.game.exploration.interaction.InteractionHandler;
import fr.quatrevieux.araknemu.game.exploration.interaction.event.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.exploration.sprite.PlayerSprite;
import fr.quatrevieux.araknemu.game.player.CharacterProperties;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerSessionScope;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.creature.Explorer;
import fr.quatrevieux.araknemu.game.world.creature.Operation;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Player for exploration game session
 */
final public class ExplorationPlayer implements Creature, Explorer, PlayerSessionScope {
    final private GamePlayer player;

    final private ListenerAggregate dispatcher = new DefaultListenerAggregate();
    final private InteractionHandler interactions = new InteractionHandler();
    final private Restrictions restrictions = new Restrictions(this);
    final private Sprite sprite;

    private ExplorationMap map;
    private ExplorationMapCell cell;
    private Direction orientation = Direction.SOUTH_EAST;

    public ExplorationPlayer(GamePlayer player) {
        this.player = player;
        this.sprite = new PlayerSprite(this);

        restrictions.refresh();
    }

    @Override
    public int id() {
        return player.id();
    }

    public GameAccount account() {
        return player.account();
    }

    @Override
    public CharacterProperties properties() {
        return player.properties();
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    @Override
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    public void dispatch(Object event) {
        player.dispatch(event);
    }

    @Override
    public void register(GameSession session) {
        session.setExploration(this);
    }

    @Override
    public void unregister(GameSession session) {
        leave();
        interactions().stop();

        session.setExploration(null);
    }

    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Override
    public ExplorationMapCell cell() {
        return cell;
    }

    @Override
    public Position position() {
        return player.position();
    }

    @Override
    public Direction orientation() {
        return orientation;
    }

    /**
     * @todo Returns Optional<ExplorationMap>
     */
    @Override
    public ExplorationMap map() {
        return map;
    }

    @Override
    public void move(ExplorationMapCell cell, Direction orientation) {
        player.setPosition(player.position().newCell(cell.id()));
        this.cell = cell;
        this.orientation = orientation;

        map.dispatch(new PlayerMoveFinished(this, cell));
    }

    @Override
    public void apply(Operation operation) {
        operation.onExplorationPlayer(this);
    }

    /**
     * Join an exploration map
     */
    public void join(ExplorationMap map) {
        this.map = map;
        this.cell = map.get(position().cell());
        map.add(this);

        dispatch(new MapJoined(map));
    }

    /**
     * Change the current map and cell
     *
     * @param map The new map
     * @param cell The new cell
     */
    public void changeMap(ExplorationMap map, int cell) {
        player.setPosition(
            new Position(map.id(), cell)
        );

        join(map);
        dispatch(new MapChanged(map));
    }

    /**
     * Change the current cell of the player
     *
     * @param cell The new cell id
     *
     * @see ExplorationPlayer#changeMap(ExplorationMap, int) For changing the map and cell
     */
    public void changeCell(int cell) {
        player.setPosition(player.position().newCell(cell));
        this.cell = map.get(cell);

        map.dispatch(new CellChanged(this, cell));
    }

    /**
     * Leave the current map
     */
    public void leave() {
        if (map != null) {
            map.remove(this);
            dispatch(new MapLeaved(map));
        }
    }

    /**
     * Get the inventory
     */
    public PlayerInventory inventory() {
        return player.inventory();
    }

    /**
     * Handle player interactions
     */
    public InteractionHandler interactions() {
        return interactions;
    }

    /**
     * Get the player data
     */
    public GamePlayer player() {
         return player;
    }

    /**
     * Get the restrictions of the exploration player
     */
    public Restrictions restrictions() {
        return restrictions;
    }

    /**
     * Change the player's orientation
     */
    public void setOrientation(Direction orientation) {
        this.orientation = orientation;

        if (map != null) {
            map.dispatch(new OrientationChanged(this, orientation));
        }
    }
}
