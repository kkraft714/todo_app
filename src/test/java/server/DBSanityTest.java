package server;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.hibernate.Session;
import server.note.*;

import static server.database.HibernateUtil.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Note: The DB must be running for these tests to work.
 * If it's not try: pg_ctl restart -D "C:/Program Files/PostgreSQL/18/data"
 * Note that you may have to try running this command more than once
 */
// ToDo: Move into server.database package?
public class DBSanityTest {
    // ToDo: Use the same session factory for all tests, or create a new one for each test?
    private static SessionFactory testSessionFactory;
    private Session testSession;

    // ToDo: Move all this setup and teardown code into TestBase?
    @BeforeAll
    public static void setUpTestSessionFactory() { testSessionFactory = getTestSessionFactory(); }

    @BeforeEach
    public void openNewSession() throws Exception {
        if (testSessionFactory == null) {
            throw new Exception("Failed to create Hibernate SessionFactory");
        }
        testSession = testSessionFactory.openSession();
        testSession.beginTransaction();
    }

    @AfterEach
    public void closeSession() {
        if (testSession != null && testSession.isOpen()) {
            if (testSession.getTransaction().isActive()) {
                testSession.getTransaction().rollback();   // Rollback transaction so test DB stays clean
            }
            testSession.close();
        }
    }

    @Test
    public void createDBConnection() {
        // This is a basic session sanity test
        assertTrue(testSession.isOpen(), "Test session in open");
        assertFalse(testSession.getProperties().isEmpty(), "Expected non-empty properties for open DB session");
        testSession.close();
        assertFalse(testSession.isOpen(), "Test session in open");
    }

    private static final NoteBase simpleNote = new NoteBase("Simple Note 1", "Simple NoteBase object");
    @Test
    public void createSimpleNote() {
        runCreationTest(testSession, simpleNote);
    }

    @Test
    public void createNote() {
        Note newNote = new Note("NewNote1", "Testing Note creation");
        runCreationTest(testSession, newNote);
    }

    // ToDo: Test adding child Note(s)

    @Test
    public void createContact() {
        Contact newPerson = Contact.newPerson("Person1", "LastName");
        runCreationTest(testSession, newPerson);
    }

    @Test
    public void createSimpleNoteAndRetrieveFromDatabase() {

    }
}

// ToDo: Add tests for creating and verifying each entity type (Note, Contact, MediaItem, etc.)
// ToDo: Add tests for CRUD operations on each entity type (Note, Contact, MediaItem, etc.)
// ToDo: Add tests for simple queries
// ToDo: Add tests for verifying managed entities (dbSession.getManagedEntities())
//       Verify there is at least one managed entity?
// ToDo: Add tests for transactions (dbSession.beginTransaction())
