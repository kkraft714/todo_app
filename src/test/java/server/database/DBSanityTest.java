package server.database;

import org.hibernate.Session;
import org.junit.jupiter.api.*;
import server.SearchCriteria;
import server.categories.Category;
import server.element.*;
import server.note.*;

import java.time.LocalDateTime;
import java.util.List;

import static server.NoteTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Note: The DB must be running for these tests to work.
 * If it's not try: pg_ctl restart -D "C:/Program Files/PostgreSQL/18/data"
 * Note that you may have to try running this command more than once
 */
// ToDo: Figure out JUnit assertThat() with matchers (and replace assertTrue())
// ToDo: Switch to TestNG Test annotations and framework (instead of JUnit)?
//   If so use the description attribute to document the tests

// ToDo: Add test for a 3-level note (i.e. a note with a child note that has a child)
// ToDo: Add Contact test with multiple Addresses, Phones, and, Emails (and assertEquals() on full lists)
// ToDo: Add DB configuration tests (combinations of config files and environment variables)
//  - Test non-existent config file
// ToDo: Try adding a duplicate category (looks like it's currently allowed)
// ToDo: Add tests for Note ordering
// ToDo: Add more remove tests?
// ToDo: Come up with some negative tests (null Note, etc.)
@SuppressWarnings("ReassignedVariable")
public class DBSanityTest extends DBTestBase {

    @Test
    public void testDBConnection() {
        // This is a basic session sanity test
        try (Session testSession = testSessionFactory.openSession()) {
            assertTrue(testSession.isOpen(), "Test session is open");
            assertFalse(testSession.getProperties().isEmpty(), "Expected non-empty properties for open DB session");
        } catch (Exception e) {
            fail("Failed to open DB session: " + e.getMessage());
        }
    }

    @Test
    public void createSimpleNote() {
        NoteBase simpleNote = getSimpleNote();
        NoteBase fetchedNote = commitNoteToDB(simpleNote);
        assertEquals(simpleNote.getName(), fetchedNote.getName(), "Simple Note name");
    }

    @Test
    public void createNote() {
        String noteName = "New Note 1";
        Note newNote = new Note(noteName, "Testing Note creation");;
        Note fetchedNote = commitNoteToDB(newNote);
        assertEquals(newNote.getName(), fetchedNote.getName(), "New Note name");
    }

    // ToDo: Add static noteID and increment it to distinguish Note names?
    @Test
    public void createNoteWithOneChildNote() {
        Note newNote = new Note("NewNote2", "Testing adding a child note");
        Note childNote = new Note("ChildNote1", "Testing adding a child note");
        newNote.addChildNote(childNote);
        Note fetchedNote = commitNoteToDB(newNote);
        assertEquals(newNote.getName(), fetchedNote.getName());
        assertEquals(1, fetchedNote.getChildNotes().size(), "Number of child notes");
        // ToDo: Verify child note name as well?
        // System.out.println("Child Note name: " + fetchedNote.getChildNote(0).getName());
    }

    @Test
    public void createNoteWithOneChildOfEachNoteType() {
        Note newNote = getTestNote();
        newNote.addChildNote(getSimpleNote());
        newNote.addChildNote(getTestNote());
        newNote.addChildNote(getTestPerson());
        newNote.addChildNote(getTestBusiness());
        newNote.addChildNote(getTestProduct());   // Note that Product here includes an owner Entity type
        newNote.addChildNote(getTestArtist());
        newNote.addChildNote(getGoogleLink());
        newNote.addChildNote(getTestScheduleItem());
        List<? extends NoteBase> children = commitNoteToDB(newNote).getChildNotes();
        // ToDo: Do a direct List compare here?
        assertEquals(newNote.getChildNotes().size(), children.size(), "Number of child notes");
        List<Class<? extends NoteBase>> childTypes = List.of(NoteBase.class, Note.class, Contact.class,
                Contact.class, Product.class, Entity.class, Link.class, ScheduleItem.class);
        for (int i = 0; i < childTypes.size(); i++) {
            assertEquals(childTypes.get(i), children.get(i).getClass(), "Type for child #" + (i + 1));
        }
        // ToDo: Write separate test(s) for retrieveAllNotesFromDB()?
        // Note that Product includes an owner Entity type, thus adding an extra Note to the total
        assertEquals(10, retrieveAllNotesFromDB().size(), "Total number of Notes in DB");
    }

    // @Test
    public void createNoteWithCategory() {

    }

    // @Test
    public void addCategoryWithSpaceInName() {
/*
        String categoryName = "Category name with spaces";
        main.addCategory(categoryName);
        Set<NoteBase> category = main.getNotesForCategory(categoryName);
        assertEquals(0, category.size(), "Number of notes in category");
*/
    }


    @Test
    public void createNoteWithLink() {
        Note newNote = new Note("NewNote3", "Testing adding a link to a note");
        Link newLink = getGoogleLink();
        newNote.addLink(newLink);
        Note fetchedNote = commitNoteToDB(newNote);
        assertEquals(1, fetchedNote.getLinks().size(), "Number of Note links");
        assertEquals(newLink.getUrl(), fetchedNote.getLink(0).getUrl(), "Link added to Note");
    }

    @Test
    public void createEntity() {
        Entity newEntity = getTestArtist();
        Entity fetchedNote = commitNoteToDB(newEntity);
        assertEquals(newEntity.getType(), fetchedNote.getType(), "Entity type");
        assertEquals(newEntity.getName(), fetchedNote.getName(), "Artist name");
    }

    @Test
    public void createContact() {
        Contact person = Contact.newPerson("Person1", "LastName1");
        Contact fetchedNote = commitNoteToDB(person);
        assertEquals(person.getName(), fetchedNote.getName(), "Person name");
        assertEquals(1, findMatchingNotesInDB(new SearchCriteria().setNoteClass(Contact.class)).size(),
                "Number of Contact notes in DB");
    }

    @Test
    public void createContactWithAddressPhoneAndEmail() {
        Contact person = Contact.newPerson("Person2", "LastName2");
        person.addAddress(new Address().setAddress1("123 4th St.").setCity("Menlo Park"));
        person.addPhoneNumber("775-3433", Phone.PhoneType.HOME);
        person.addEmail("user1@gmail.com", Email.EmailType.PERSONAL);
        Contact fetchedNote = commitNoteToDB(person);
        assertContactHasExpectedNumberOfAddressesPhoneNumbersAndEmails(1, fetchedNote);
    }

    @Test
    public void createProduct() {
        Product newProduct = getTestProduct();
        Product fetchedNote = commitNoteToDB(newProduct);
        assertEquals(newProduct.getType(), fetchedNote.getType(), "Product category");
        assertEquals(newProduct.getOwner().getName(), fetchedNote.getOwner().getName(), "Owner name");
        assertEquals(newProduct.getPrice(), fetchedNote.getPrice(), "Product price");
        assertEquals(1, findMatchingNotesInDB(new SearchCriteria().setNoteClass(Product.class)).size(),
                "Number of Product notes in DB");
    }

    @Test
    public void createLink() {
        Link newLink = getGoogleLink();
        Link fetchedNote = commitNoteToDB(newLink);
        assertEquals(newLink.getUrl(), fetchedNote.getUrl(), "Link URL");
    }

    @Test
    public void createScheduleItem() {
        ScheduleItem event = new ScheduleItem("Deadline", null, ScheduleItem.convertToDate("2020-06-12 00:00:00 AM"));
        ScheduleItem fetchedNote = commitNoteToDB(event);
        assertEquals(1, findMatchingNotesInDB(new SearchCriteria().setNoteClass(ScheduleItem.class)).size(),
                "Number of ScheduleItem notes in DB");
        assertEquals(event.getDate(), fetchedNote.getDate(), "Event date");
    }

    // ToDo: Test ScheduleItems with the same date
    @Test
    public void scheduleItemsAreRetrievedInTimestampOrder() {
        createScheduleItemTestBed();    // This creates the ScheduleItems with dates in non-timestamp order
        List<ScheduleItem> schedule = retrieveScheduleFromDB();
        for (int i = 1; i < schedule.size(); i++) {
            LocalDateTime date1 = schedule.get(i - 1).getDate();
            LocalDateTime date2 = schedule.get(i).getDate();
            assertTrue(date2.isAfter(date1) || date2.isEqual(date1), date2 + " >= " + date1);
        }
    }

    // @Test
    // ToDo: Need some provision for returning data for a schedule view from the DB
    public void callGetScheduleWithNoScheduleItemsAdded() {
        // assertEquals(0, main.getSchedule().size(), "Number of schedule items");
    }

    @Test
    public void createMultipleNotes() {
        int expectedCount = 3;
        List<NoteBase> fetchedNotes = commitAndRetrieveTestNotes(expectedCount);
        assertEquals(expectedCount, fetchedNotes.size(), "Number of notes created");
    }

    @Test
    public void updateSimpleNote() {
        String newNoteName = "New note name";
        String newDescription = "New note description";
        NoteBase fetchedNote = commitNoteToDB(getSimpleNote());
        assertNotNull(fetchedNote, "Note added to DB");
        fetchedNote.setName(newNoteName).setDescription(newDescription).setCompleted();
        fetchedNote = commitNoteToDB(fetchedNote);
        assertEquals(newNoteName, fetchedNote.getName(), "Updated Note name");
        assertEquals(newDescription, fetchedNote.getDescription(), "Updated Note description");
        assertTrue(fetchedNote.isCompleted(), "Updated Note completion status");
    }

    @Test
    public void updateProduct() {
        Product fetchedNote = commitNoteToDB(getTestProduct());
        assertNotNull(fetchedNote, "Note added to DB");
        Category newProductType = Category.of("New type");
        Entity newProductOwner = new Entity("New owner name", "", Entity.EntityType.PERSON);
        Price newProductPrice = new Price(19.99);
        fetchedNote.setType(newProductType).setOwner(newProductOwner).setPrice(newProductPrice);
        fetchedNote = commitNoteToDB(fetchedNote);
        assertEquals(newProductType, fetchedNote.getType(), "Product type");
        assertEquals(newProductOwner.getName(), fetchedNote.getOwner().getName(), "Product owner");
        assertEquals(newProductPrice, fetchedNote.getPrice(), "Product price");
    }

    @Test
    public void updateScheduleItem() {
        ScheduleItem fetchedNote = commitNoteToDB(getTestScheduleItem());
        assertNotNull(fetchedNote, "Note added to DB");
        LocalDateTime newDate = ScheduleItem.convertToDate("1961-07-06 00:00:00 AM");
        ScheduleItem.CompletionStatus newStatus = ScheduleItem.CompletionStatus.CANCELED;
        fetchedNote.setDate(newDate);
        fetchedNote.setStatus(newStatus);
        fetchedNote = commitNoteToDB(fetchedNote);
        assertEquals(newDate, fetchedNote.getDate(), "New date");
        assertEquals(newStatus, fetchedNote.getStatus(), "New status");
        // ToDo: Confirm that getSchedule() query returns the Note?
    }

    @Test
    public void updateChildNotes() {
        Note fetchedNote = commitNoteToDB(getTestNote());
        assertNotNull(fetchedNote, "Note added to DB");
        assertEquals(0, fetchedNote.getChildNotes().size(), "Number of child notes");
        fetchedNote.addChildNote(new Note("Child Note 1")).addChildNote(new Note("Child Note 2"));
        fetchedNote = commitNoteToDB(fetchedNote);
        assertEquals(2, fetchedNote.getChildNotes().size(), "Number of child notes");
        NoteBase childNote1 = fetchedNote.getChildNotes().getFirst();    // Need to get managed version
        fetchedNote.removeChildNote(childNote1);
        fetchedNote = commitNoteToDB(fetchedNote);
        assertEquals(1, fetchedNote.getChildNotes().size(), "Number of child notes");
    }

    @Test
    public void updateCategories() {
        NoteBase fetchedNote = commitNoteToDB(getSimpleNote());
        assertEquals(0, fetchedNote.getCategories().size(), "Number of categories in Note");
        assertEquals(0, retrieveCategoriesFromDB().size(), "Number of categories in DB");
        fetchedNote = commitNoteToDB(fetchedNote.addCategories(testCategories));
        assertEquals(testCategories.size(), fetchedNote.getCategories().size(), "Number of categories");
        assertEquals(testCategories.size(), retrieveCategoriesFromDB().size(), "Number of categories in DB");
        fetchedNote.getCategories().remove(fetchedNote.getCategories().stream().toList().getFirst());
        fetchedNote = commitNoteToDB(fetchedNote);
        assertEquals(1, fetchedNote.getCategories().size(), "Number of categories");
        assertEquals(1, retrieveCategoriesFromDB().size(), "Number of categories in DB");
    }

    @Test
    public void updateLinks() {
        Note fetchedNote = commitNoteToDB(new Note("Note with Links").addLink(getGoogleLink()));
        assertEquals(1, fetchedNote.getLinks().size(), "Number of links");
        fetchedNote.getLinks().removeFirst();
        fetchedNote = commitNoteToDB(fetchedNote);
        assertEquals(0, fetchedNote.getLinks().size(), "Number of links");
    }

    @Test
    public void updateBusinessAddressPhoneAndEmail() {
        Contact fetchedNote = commitNoteToDB(Contact.newBusiness("Company1", "New Company"));
        assertContactHasExpectedNumberOfAddressesPhoneNumbersAndEmails(0, fetchedNote);
        fetchedNote.addAddress(new Address().setAddress1("123 4th St.").setCity("Menlo Park"));
        fetchedNote.addPhoneNumber("775-3433", Phone.PhoneType.HOME);
        fetchedNote.addEmail("user1@gmail.com", Email.EmailType.PERSONAL);
        fetchedNote = commitNoteToDB(fetchedNote);
        assertContactHasExpectedNumberOfAddressesPhoneNumbersAndEmails(1, fetchedNote);
        fetchedNote.getAddresses().removeFirst();
        fetchedNote.getPhoneNumbers().removeFirst();
        fetchedNote.getEmails().removeFirst();
        fetchedNote = commitNoteToDB(fetchedNote);
        assertContactHasExpectedNumberOfAddressesPhoneNumbersAndEmails(0, fetchedNote);
    }

    // Todo: Update tests:
    //  - Test adding child notes at index?

    @Test
    public void removeOnlyNoteFromDB() {
        Note fetchedNote = commitNoteToDB(getTestNote());
        assertEquals(1, retrieveAllNotesFromDB().size(), "Number of Notes in DB");
        removeNoteFromDB(fetchedNote);
        assertEquals(0, retrieveAllNotesFromDB().size(), "Number of Notes in DB");
    }

    @Test
    public void removeAllNotesFromDB() {
        int numberOfNotes = 5;
        List<NoteBase> notes = createTestNotes(numberOfNotes).stream().map(HibernateUtil::commitNoteToDB).toList();
        for (int i = 0; i < numberOfNotes; i++) {
            removeNoteFromDB(notes.get(i));
            assertEquals(numberOfNotes - i - 1, retrieveAllNotesFromDB().size(), "Number of Notes in DB");
        }
    }

    @Test
    public void removingNoteRemovesChildNotes() {
        // ToDo: Create addChildren() test helper method?
        Note testNote = getTestNote();
        createTestNotes(3).forEach(testNote::addChildNote);
        Note fetchedNote = commitNoteToDB(testNote);
        assertEquals(4, retrieveAllNotesFromDB().size(), "Number of Notes in DB");
        removeNoteFromDB(fetchedNote);
        assertEquals(0, retrieveAllNotesFromDB().size(), "Number of Notes in DB");
    }

    @Test
    public void removeNoteWithCategoriesFromDB() {
        NoteBase fetchedNote = commitNoteToDB(getSimpleNote().addCategories(testCategories));
        assertEquals(testCategories.size(), retrieveCategoriesFromDB().size(), "Number of categories in DB");
        SearchCriteria criteria = new SearchCriteria().setCategories(testCategories);
        assertEquals(1, findMatchingNotesInDB(criteria).size(), "Number of Notes containing " + testCategories);
        removeNoteFromDB(fetchedNote);
        assertEquals(0, retrieveAllNotesFromDB().size(), "Number of Notes in DB");
        assertEquals(0, retrieveCategoriesFromDB().size(), "Number of categories in DB");
        assertEquals(0, findMatchingNotesInDB(criteria).size(), "Number of Notes containing " + testCategories);
    }

    @Test
    public void removeNoteThatIsNotInDB() {
        removeNoteFromDB(getTestNote());
    }

    @Test
    public void removeNullNoteFromDB() {
        removeNoteFromDB(null);
    }

    @Test
    public void removeNoteThatHasAlreadyBeenRemovedFromDB() {
        NoteBase fetchedNote = commitNoteToDB(getSimpleNote());
        removeNoteFromDB(fetchedNote);
        removeNoteFromDB(fetchedNote);
    }
}
