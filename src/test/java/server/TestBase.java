package server;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import server.database.HibernateUtil;
import server.note.NoteBase;

import static server.NoteTestHelper.*;
import static server.database.HibernateUtil.*;

// ToDo: Rename this to DBTestBase?
public class TestBase {
    private static StandardServiceRegistry testServiceRegistry;
    private static SessionFactory testSessionFactory;
    protected static NoteOrganizer main;
    protected static Session testSession;
    protected static final String defaultCategoryName = "NewCategory";

    @BeforeAll
    public static void setUpTestSessionFactory() {
        testServiceRegistry = getServiceRegistry(true);
        testSessionFactory = getSessionFactory(testServiceRegistry);
    }

    @BeforeEach
    public void initialize() throws Exception {
        main = new NoteOrganizer();
        if (testSessionFactory == null) {
            throw new Exception("Failed to create Hibernate SessionFactory");
        }
        testSession = testSessionFactory.openSession();
        testSession.beginTransaction();
    }

    @AfterEach
    public void testCleanup() {
        if (testSession != null && testSession.isOpen()) {
            if (testSession.getTransaction().isActive()) {
                testSession.getTransaction().rollback();   // Rollback transaction so test DB stays clean
            }
            testSession.close();
        }
        resetNoteNumber();
    }

    @AfterAll
    public static void closeSessionFactory() {
        if (testSessionFactory != null) {
            testSessionFactory.close();
        }
        if (testServiceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(testServiceRegistry);
        }
    }

    public static <T extends NoteBase> T retrieveNoteFromDB(T note) {
        if (testSession != null && testSession.isOpen()) {
            return HibernateUtil.retrieveNoteFromDB(testSession, note);
        }
        throw new IllegalStateException("Failed to retrieve note " + note.getName() + " (session is not open)");
    }
}
