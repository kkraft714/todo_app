package server.database;

import org.hibernate.Session;
import server.categories.Category;
import server.note.*;
import server.element.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.*;
import org.hibernate.cfg.Configuration;

import java.util.List;

import static server.NoteUtils.*;

public class HibernateUtil {

    private static SessionFactory buildSessionFactory(boolean isTestDB) {
        Configuration config = new Configuration();     // Looks for hibernate.properties
        try {
            // Currently we assume a PostgreSQL DB
            config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            // Highly recommended for debugging your first test:
            config.setProperty("hibernate.show_sql", "true");
            config.setProperty("hibernate.format_sql", "true");            trySettingConfigFromEnvironment(config, isTestDB);
            LOG.info("Hibernate: Connecting to database using connection string {}",
                    config.getProperty("hibernate.connection.url"));
            Metadata metadata;
            try (StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(config.getProperties()).build()) {
                MetadataSources sources = new MetadataSources(serviceRegistry);
                // My operating assumption is that I must add @Embeddable classes before @Entity classes
                for (Class<?> entity : new Class<?>[]{ Category.class, Address.class, Phone.class,Price.class,
                        NoteBase.class, Note.class, Entity.class, Contact.class, Link.class, Product.class,
                        ScheduleItem.class}) {
                    sources.addAnnotatedClass(entity);
                }
                return sources.getMetadataBuilder().build().buildSessionFactory();
            }
            catch (Exception ex) {
                LOG.error("Failed to create Hibernate session factory", ex);
            }
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
        return null;
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
            String jdbcUrl = "jdbc:postgresql://localhost:" + dbPort + "/" + database;
            config.setJdbcUrl(jdbcUrl);
            // ToDo: Am I logging this twice?
            LOG.info("Set the JDBC URL to {}", jdbcUrl);
        }
    }

    public static SessionFactory getTestSessionFactory() { return buildSessionFactory(true); }
    public static SessionFactory getProdSessionFactory() { return buildSessionFactory(false); }

    public static void runCreationTest(Session testSession, NoteBase noteType) {
        try {
            testSession.beginTransaction();
            testSession.persist(noteType);
            testSession.getTransaction().commit();
        }
        catch (Exception ex) {
            if (testSession.getTransaction() != null && testSession.getTransaction().isActive()) {
                testSession.getTransaction().rollback();
            }
            throw ex;  // Fail test
        }
    }
}
