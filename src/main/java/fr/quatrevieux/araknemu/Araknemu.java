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

package fr.quatrevieux.araknemu;

import fr.quatrevieux.araknemu.core.BootException;
import fr.quatrevieux.araknemu.core.Service;
import fr.quatrevieux.araknemu.core.config.Configuration;
import fr.quatrevieux.araknemu.core.config.DefaultConfiguration;
import fr.quatrevieux.araknemu.core.config.IniDriver;
import fr.quatrevieux.araknemu.core.dbal.DatabaseConfiguration;
import fr.quatrevieux.araknemu.core.dbal.DatabaseHandler;
import fr.quatrevieux.araknemu.core.dbal.DefaultDatabaseHandler;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.repository.implementation.sql.SqlLivingRepositoriesModule;
import fr.quatrevieux.araknemu.data.world.repository.implementation.sql.SqlWorldRepositoriesModule;
import fr.quatrevieux.araknemu.game.GameModule;
import fr.quatrevieux.araknemu.game.GameService;
import fr.quatrevieux.araknemu.game.connector.LocalModule;
import fr.quatrevieux.araknemu.realm.RealmModule;
import fr.quatrevieux.araknemu.realm.RealmService;
import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Startup class
 */
public class Araknemu {
    /**
     * Get the current version of the server (retrieved from pom.xml)
     */
    final static public String VERSION = Araknemu.class.getPackage().getImplementationVersion();
    final static public String NAME = "Araknemu";
    final static public String YEAR = "2017-2019";
    final static public String AUTHOR = "Vincent Quatrevieux";

    final private Logger logger = LoggerFactory.getLogger(getClass());

    final private Configuration configuration;
    final private DatabaseHandler database;
    final private List<Service> services = new ArrayList<>();

    public Araknemu(Configuration configuration, DatabaseHandler database) {
        this.configuration = configuration;
        this.database = database;
    }

    /**
     * Boot all services
     */
    public void boot() throws BootException {
        System.out.println(NAME + " Copyright (c) " + YEAR + " " + AUTHOR);
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY.");
        System.out.println("This is free software, and you are welcome to redistribute it under certain conditions.");

        logger.info("Starting {} v{}", NAME, VERSION);
        logger.info("Booting services");

        for (Service service : services) {
            service.boot();
        }

        logger.info("Running garbage collector");
        System.gc();

        logger.info("Araknemu started");
    }

    /**
     * Stop all services
     */
    public void shutdown() {
        logger.info("Shutdown requested...");

        for (Service service : services) {
            service.shutdown();
        }

        database.stop();

        logger.info("Araknemu successfully stopped");
    }

    /**
     * Add a new service
     */
    public void add(Service service) {
        services.add(service);
    }

    /**
     * Get the application configuration
     */
    public Configuration configuration() {
        return configuration;
    }

    /**
     * Get the database handler
     */
    public DatabaseHandler database() {
        return database;
    }

    /**
     * Application entry point
     */
    public static void main(String[] args) throws IOException, SQLException, ContainerException, BootException {
        Configuration configuration = new DefaultConfiguration(
            new IniDriver(
                new Ini(new File("config.ini"))
            )
        );

        Araknemu app = new Araknemu(
            configuration,
            new DefaultDatabaseHandler(
                configuration.module(DatabaseConfiguration.class),
                LoggerFactory.getLogger(DatabaseHandler.class)
            )
        );

        Container realmContainer = makeRealmContainer(app);
        Container gameContainer  = makeGameContainer(app, realmContainer);

        app.add(realmContainer.get(RealmService.class));
        app.add(gameContainer.get(GameService.class));

        app.boot();

        Runtime.getRuntime().addShutdownHook(new Thread(app::shutdown));
    }

    static private Container makeRealmContainer(Araknemu app) throws SQLException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(
            app.database().get("realm")
        ));
        container.register(new RealmModule(app));

        return container;
    }

    static private Container makeGameContainer(Araknemu app, Container realmContainer) throws SQLException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(
            app.database().get("game")
        ));
        container.register(new SqlWorldRepositoriesModule(
            app.database().get("game")
        ));
        container.register(new GameModule(app));
        container.register(new LocalModule(realmContainer));

        return container;
    }
}
