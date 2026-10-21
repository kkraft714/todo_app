package server.database;

import server.categories.Category;
import server.note.*;
import server.element.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

import static server.NoteUtils.*;

public class HibernateUtil {

    private static SessionFactory buildSessionFactory(boolean isTestDB) {
        Configuration config = new Configuration();     // Looks for hibernate.properties
        try {
            // Currently we assume a PostgreSQL DB
            config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            trySettingConfigFromEnvironment(config, isTestDB);
            LOG.info("Hibernate: Connecting to database using connection string {}",
                    config.getProperty("hibernate.connection.url"));
            // Note that the order for addPackage() is significant here (e.g. @Entity must come before @Embeddable)
            for (String serverPackage : List.of("note", "categories", "element")) {
                config.addPackage("server." + serverPackage);
            }
            return config.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static void trySettingConfigFromEnvironment(Configuration config, boolean isTestDB) {
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        if (isSet(username) && isSet(password)) {
            config.setCredentials(username, password);
            LOG.info("Set DB_USERNAME and DB_PASSWORD from environment variables");
        }
        String dbPort = System.getenv("DB_PORT");
        String database = System.getenv(isTestDB ? "DB_TEST_NAME" : "DB_PROD_NAME");
        if (isSet(dbPort) && isSet(database)) {
            config.setJdbcUrl("jdbc:postgresql://localhost:" + dbPort + "/" + database);
            LOG.info("Set the JDBC URL based on the DB_PORT and DB_TEST_NAME environment variables");
        }
    }

    public static SessionFactory getTestSessionFactory() { return buildSessionFactory(true); }
    public static SessionFactory getProdSessionFactory() { return buildSessionFactory(false); }
}
