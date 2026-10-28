package server.database;

import org.hibernate.*;
import org.hibernate.search.engine.search.predicate.SearchPredicate;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jspecify.annotations.NonNull;
import server.SearchCriteria;
import server.note.*;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static server.NoteUtils.*;

public class HibernateUtil {

    public static @NonNull StandardServiceRegistry getServiceRegistry(boolean isTestDB) {
        try {
            return new StandardServiceRegistryBuilder()
                    .applySettings(getConfiguration(isTestDB).getProperties()).build();
        }
        catch (Exception ex) {
            LOG.error("Failed to create Hibernate service registry", ex);
            throw ex;
        }
    }

    public static SessionFactory getSessionFactory(StandardServiceRegistry registry) {
        MetadataSources sources = new MetadataSources(registry);
        // All @Entity classes must be included here (looks like I don't need to include @Embeddable classes)
        for (Class<?> entity : new Class<?>[]{ NoteBase.class, Note.class, Entity.class, Contact.class, Link.class,
                Product.class, ScheduleItem.class}) {
            sources.addAnnotatedClass(entity);
        }
        return sources.buildMetadata().buildSessionFactory();
    }

    private static @NonNull Configuration getConfiguration(boolean isTestDB) {
        Configuration config = new Configuration();    // Looks for hibernate.properties by default
        // Currently we assume a PostgreSQL DB
        config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        // Highly recommended for debugging your first test (ToDo: put under a DEBUG flag?)
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.format_sql", "true");
        trySettingConfigFromEnvironment(config, isTestDB);
        if (isTestDB) {
            // ToDo: Confirm we are NOT pointing at the production DB here!
            // Create and drop the DB for each test run
            config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            // Force sync for DB test results
            // config.setProperty("hibernate.search.automatic_indexing.synchronization.strategy", "sync");
            // Cache the Lucene search results in memory instead of on disk
            config.setProperty("hibernate.search.backend.directory.type", "local-heap");
        }
        else {
            String hbm2ddlAuto = config.getProperty("hibernate.hbm2ddl.auto");
            if (isSet(hbm2ddlAuto) && hbm2ddlAuto.equalsIgnoreCase("create-drop")) {
                throw new IllegalStateException("Cannot allow hibernate.hbm2ddl.auto=create-drop on production DB!");
            }
        }
        LOG.info("Hibernate: Connecting to database using connection string {}",
                config.getProperty("hibernate.connection.url"));
        return config;
    }

    private static void trySettingConfigFromEnvironment(Configuration config, boolean isTestDB) {
        // ToDo: Should environment variables override config settings?
        if (setConfigFromLocalResourceFile(config)) {
            return;
        }
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        if (isSet(username) && isSet(password)) {
            config.setCredentials(username, password);
            LOG.info("Set DB_USERNAME and DB_PASSWORD from environment variables");
        }
        String dbPort = System.getenv("DB_PORT");
        String database = System.getenv(isTestDB ? "DB_TEST_NAME" : "DB_PROD_NAME");
        if (isSet(dbPort) && isSet(database)) {
            String jdbcUrl = "jdbc:postgresql://localhost:" + dbPort + "/" + database;
            config.setJdbcUrl(jdbcUrl);
            // ToDo: Am I logging this twice?
            LOG.info("Set the JDBC URL to {}", jdbcUrl);
        }
    }

    private static boolean setConfigFromLocalResourceFile(Configuration config) {
        String hibernateConfigFile = System.getenv("HIBERNATE_CONFIG_FILE");
        if (!isSet(hibernateConfigFile)) {
            return false;
        }
        try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream(hibernateConfigFile)) {
            if (input == null) {
                return false;
            }
            LOG.info("Loading Hibernate properties from config file {}", hibernateConfigFile);
            Properties configProperties = new Properties();
            configProperties.load(input);
            config.addProperties(configProperties);
            return true;
        }
        catch (Exception ex) {
            LOG.error("Failed to load Hibernate config file '{}'", hibernateConfigFile, ex);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T extends NoteBase> T retrieveNoteFromDB(Session session, T note) {
        Class<T> noteClass = (Class<T>) Hibernate.getClass(note);
        return session.find(noteClass, note.getId());
    }

    public static boolean hasOpenTransaction(Session session) {
        return session != null && session.getTransaction() != null && session.getTransaction().isActive();
    }

    public static List<? extends NoteBase> findMatchingNotesInDB(Session session, SearchCriteria criteria) {
        // Search.session(session).indexingPlan().execute();
        return Search.session(session).search(criteria.getNoteClass()).where(
                factory -> factory.bool().with(predicate -> {
            if (isSet(criteria.getNameMatch())) {
                // I tried factoring out the duplicate code but it looks difficult/impossible
                if (criteria.isPatternMatch()) {
                    predicate.must(factory.regexp().field("name").matching(criteria.getNamePattern()));
                }
                else {
                    predicate.must(factory.wildcard().field("name")
                            .matching("*" + criteria.getNameMatch().toLowerCase() + "*"));
                }
            }
            if (!criteria.getCategories().isEmpty()) {
                if (criteria.isJoinWithAnd()) {
                    for (String category : criteria.getCategories()) {
                        predicate.must(factory.match().field("categories").matching(category));
                    }
                }
                else {
                    predicate.must(factory.bool().with(innerBool -> {
                        for (String category : criteria.getCategories()) {
                            innerBool.should(factory.match().field("categories").matching(category));
                        }
                    }));
                }
            }
        })).fetchAllHits();
    }
}
