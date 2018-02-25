package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.common.CharacteristicsChanged;
import fr.quatrevieux.araknemu.game.event.common.LifeChanged;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.world.item.inventory.exception.InventoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.ref.Reference;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class LifeTest extends GameBaseCase {
    private Life life;

    private GamePlayer player;
    private Player entity;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        life = new Life(
            player = gamePlayer(true),
            entity = container.get(PlayerRepository.class).get(new Player(player.id()))
        );
    }

    @Test
    void maxSimple() {
        assertEquals(295, life.max());
    }

    @Test
    void maxLifeWithStuffAndBaseVitality() throws SQLException, ContainerException, InventoryException {
        dataSet.pushItemTemplates();

        player.characteristics().base().set(Characteristic.VITALITY, 50);
        player.inventory().add(
            container.get(ItemService.class).create(2419, true),
            1, 2
        );
        player.characteristics().rebuildStuffStats();

        life.rebuild();
        assertEquals(373, life.max());
    }

    @Test
    void current() {
        entity.setLife(123);

        assertEquals(123, life.current());
    }

    @Test
    void percent() {
        assertEquals(100, life.percent());

        entity.setLife(150);
        assertEquals(50, life.percent());
    }

    @Test
    void rebuildWithFullLife() {
        assertEquals(295, life.max());
        assertEquals(295, life.current());
        assertEquals(100, life.percent());

        player.characteristics().base().set(Characteristic.VITALITY, 100);
        life.rebuild();

        assertEquals(395, life.max());
        assertEquals(395, life.current());
        assertEquals(100, life.percent());
    }

    @Test
    void rebuildWillKeepPercentLife() {
        entity.setLife(59);

        assertEquals(295, life.max());
        assertEquals(20, life.percent());

        player.characteristics().base().set(Characteristic.VITALITY, 100);
        life.rebuild();

        assertEquals(395, life.max());
        assertEquals(79, life.current());
        assertEquals(20, life.percent());
    }

    @Test
    void initWithLifePoints() throws SQLException, ContainerException {
        entity.setLife(65);

        life = new Life(player, entity);

        assertEquals(65, life.current());
    }

    @Test
    void initWithMaxLifePoints() throws SQLException, ContainerException {
        entity.setLife(-1);

        life = new Life(player, entity);

        assertEquals(295, life.current());
    }

    @Test
    void initWithLifePointsUpperThanMax() throws SQLException, ContainerException {
        entity.setLife(10000);

        life = new Life(player, entity);

        assertEquals(295, life.current());
    }

    @Test
    void add() {
        entity.setLife(65);

        life.add(10);

        assertEquals(75, life.current());
    }

    @Test
    void addMoreThanRemaining() {
        entity.setLife(200);

        life.add(100);

        assertEquals(295, life.current());
    }

    @Test
    void setWillDispatchEvent() throws SQLException, ContainerException {
        AtomicReference<LifeChanged> ref = new AtomicReference<>();
        gamePlayer().dispatcher().add(LifeChanged.class, ref::set);

        life.set(123);

        assertNotNull(ref.get());
        assertEquals(123, life.current());
        assertEquals(123, ref.get().current());
    }
}
