package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import java.sql.SQLException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class NpcServiceTest extends GameBaseCase {
    private NpcService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        service = new NpcService(
            container.get(DialogService.class),
            container.get(NpcTemplateRepository.class),
            container.get(NpcRepository.class)
        );
    }

    @Test
    void listenersOnMapLoadedShouldAddNpc() throws ContainerException, SQLException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        // Push NPC after load map to ensure that old NpcService will not load NPCs
        dataSet.pushNpcs();

        dispatcher.dispatch(new MapLoaded(map));

        Creature creature = map.creature(-47204);

        assertInstanceOf(GameNpc.class, creature);

        GameNpc npc = (GameNpc) creature;

        assertEquals(map.get(82), npc.cell());
        assertEquals(Direction.SOUTH_EAST, npc.orientation());
        assertEquals(-47204, npc.id());
        assertEquals(878, npc.template().id());
    }

    @Test
    void listenersOnMapLoadedPreloaded() throws ContainerException, SQLException {
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        ListenerAggregate dispatcher = new DefaultListenerAggregate();
        dispatcher.register(service);

        // Push NPC after load map to ensure that old NpcService will not load NPCs
        dataSet.pushNpcs();
        service.preload(NOPLogger.NOP_LOGGER);

        dispatcher.dispatch(new MapLoaded(map));

        Creature creature = map.creature(-47204);

        assertInstanceOf(GameNpc.class, creature);

        GameNpc npc = (GameNpc) creature;

        assertEquals(878, npc.template().id());
    }

    @Test
    void preload() throws SQLException, ContainerException {
        Logger logger = Mockito.mock(Logger.class);

        dataSet.pushNpcs();
        service.preload(logger);

        Mockito.verify(logger).info("Loading NPCs...");
        Mockito.verify(logger).info("{} NPC templates loaded", 3);
        Mockito.verify(logger).info("{} NPCs loaded", 3);
    }

    @Test
    void get() throws SQLException, ContainerException {
        dataSet.pushNpcs();

        GameNpc npc = service.get(472);

        assertEquals(-47204, npc.id());
        assertEquals(82, npc.position().cell());

        NpcQuestion question = npc.question(explorationPlayer()).get();

        assertEquals(3786, question.id());
        assertCount(2, question.responses(explorationPlayer()));
    }
}
