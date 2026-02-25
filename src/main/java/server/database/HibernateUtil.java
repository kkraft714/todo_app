package server.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final Logger LOG = LogManager.getLogger(HibernateUtil.class);
    private static final SessionFactory factory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration config = new Configuration();     // Looks for hibernate.properties
            System.out.println("Hibernate: Connecting to database using connection string "
                    + config.getProperty("hibernate.connection.url"));
            // Currently we assume a PostgreSQL DB
            config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            trySettingConfigFromEnvironment(config);
            return config.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    // ToDo: Set up connections for test and prod DBs (or just choose the relevant one depending on the context?)
    private static void trySettingConfigFromEnvironment(Configuration config) {
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            config.setCredentials(username, password);
            LOG.info("Set DB_USERNAME and DB_PASSWORD from environment variables");
        }
        String dbPort = System.getenv("DB_PORT");
        // ToDo: Support setting test or prod connection depending on context
        String testDB = System.getenv("DB_TEST_NAME");
        String prodDB = System.getenv("DB_PROD_NAME");
        if (dbPort != null && !dbPort.isEmpty() && testDB != null && !testDB.isEmpty()) {
            config.setJdbcUrl("jdbc:postgresql://localhost:" + dbPort + "/" + testDB);
            LOG.info("Set the JDBC URL based on the DB_PORT and DB_TEST_NAME environment variables");
        }
    }

    public static SessionFactory getSessionFactory() {
        return factory;
    }

    public static Session getSession() {
        return factory.openSession();
}
}
