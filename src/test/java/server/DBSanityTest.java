package server;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;
import org.hibernate.Session;
import server.categories.Category;
import server.categories.MediaType;
import server.note.*;

import java.time.LocalDateTime;

import static server.database.HibernateUtil.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Note: The DB must be running for these tests to work.
 * If it's not try: pg_ctl restart -D "C:/Program Files/PostgreSQL/18/data"
 * Note that you may have to try running this command more than once
 */
// ToDo: Move into server.database package?
public class DBSanityTest {
    private static StandardServiceRegistry testServiceRegistry;
    private static SessionFactory testSessionFactory;
    private Session testSession;

    // ToDo: Move all this setup and teardown code into TestBase?
    @BeforeAll
    public static void setUpTestSessionFactory() {
        testServiceRegistry = buildServiceRegistry(true);
        testSessionFactory = getSessionFactory(testServiceRegistry);
    }

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

    @AfterAll
    public static void closeSessionFactory() {
        if (testSessionFactory != null) {
            testSessionFactory.close();
        }
        if (testServiceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(testServiceRegistry);
        }
    }

    @Test
    public void testDBConnection() {
        // This is a basic session sanity test
        assertTrue(testSession.isOpen(), "Test session in open");
        assertFalse(testSession.getProperties().isEmpty(), "Expected non-empty properties for open DB session");
        testSession.close();
        assertFalse(testSession.isOpen(), "Test session in open");
    }

    private static final String simpleNoteName = "Simple Note 1";
    private static final NoteBase simpleNote = new NoteBase(simpleNoteName, "Simple NoteBase object");
    @Test
    public void createSimpleNote() {
        runCreationTest(testSession, simpleNote);
        testSession.clear();
        NoteBase fetchedNote = testSession.find(NoteBase.class, simpleNote.getId());
        assertNotNull(fetchedNote);
        assertEquals(simpleNoteName, fetchedNote.getName(), "Simple Note name");
    }

    @Test
    public void createNote() {
        String noteName = "New Note 1";
        Note newNote = new Note(noteName, "Testing Note creation");
        runCreationTest(testSession, newNote);
        testSession.clear();
        Note fetchedNote = testSession.find(Note.class, newNote.getId());
        assertNotNull(fetchedNote);
        assertEquals(newNote.getName(), fetchedNote.getName(), "New Note name");
    }

    // ToDo: Add static noteID to distinguish Note names?
    @Test
    public void createNoteWithOneChildNote() {
        String noteName = "NewNote2";
        Note newNote = new Note(noteName, "Testing adding a child note");
        Note childNote = new Note("ChildNote1", "Testing adding a child note");
        newNote.addChildNote(childNote);
        testSession.persist(childNote);
        runCreationTest(testSession, newNote);
        testSession.clear();
        Note fetchedNote = testSession.find(Note.class, newNote.getId());
        assertNotNull(fetchedNote);
        assertEquals(noteName, fetchedNote.getName());
        assertEquals(1, fetchedNote.getChildNotes().size(), "Number of child notes");
        // ToDo: Verify child note name as well?
        // System.out.println("Child Note name: " + fetchedNote.getChildNote(0).getName());
    }

    @Test
    public void createEntity() {
        // ToDo: Factory out static final Entity type for testing?
        Entity artist = new Entity("Grand Funk", null, Entity.EntityType.ARTIST);
        runCreationTest(testSession, artist);
        testSession.clear();
        Note fetchedNote = testSession.find(Entity.class, artist.getId());
        assertNotNull(fetchedNote);
        assertEquals(Entity.EntityType.ARTIST, artist.getType(), "Entity type");
    }

    @Test
    public void createContact() {
        String firstName = "Person1";
        String lastName = "LastName";
        Contact person = Contact.newPerson(firstName, lastName);
        runCreationTest(testSession, person);
        testSession.clear();
        Note fetchedNote = testSession.find(Contact.class, person.getId());
        assertNotNull(fetchedNote);
        assertEquals(firstName + " " + lastName, fetchedNote.getName(), "Person name");
    }

    @Test
    public void createProduct() {
        Entity owner = new Entity("Fanny", null, Entity.EntityType.ARTIST);
        testSession.persist(owner);
        Product newProduct = new Product("Blind Alley", null, null).setType(Category.of(MediaType.SONG));
        newProduct.setOwner(owner);
        runCreationTest(testSession, newProduct);
        testSession.clear();
        Note fetchedNote = testSession.find(Product.class, newProduct.getId());
        assertNotNull(fetchedNote);
        assertEquals(Category.of(MediaType.SONG), newProduct.getType(), "Product category");
    }

    @Test
    public void createLink() {
        String linkURL = "http://google.com";
        Link webLink = new Link("Google", null, linkURL);
        runCreationTest(testSession, webLink);
        Link fetchedNote = testSession.find(Link.class, webLink.getId());
        assertNotNull(fetchedNote);
        assertEquals(linkURL, webLink.getUrl().toString(), "Link URL");
    }

    @Test
    public void createScheduleItem() {
        LocalDateTime eventDate = ScheduleItem.convertToDate("2020-06-12 00:00:00 AM");
        ScheduleItem event = new ScheduleItem("Deadline", null, eventDate);
        runCreationTest(testSession, event);
        ScheduleItem fetchedNote = testSession.find(ScheduleItem.class, event.getId());
        assertNotNull(fetchedNote);
        assertEquals(eventDate, event.getDate(), "Event date");
    }
}

// ToDo: Add deletion tests
// ToDo: Test deleting one child note from a list of multiple
// ToDo: Test cascading deletes (deleting parent entity also deletes children)
// ToDo: Add tests for CRUD operations on each entity type (Note, Contact, MediaItem, etc.)
// ToDo: Add tests for complex queries
// ToDo: Add tests for verifying managed entities (dbSession.getManagedEntities())
//       Verify there is at least one managed entity?
