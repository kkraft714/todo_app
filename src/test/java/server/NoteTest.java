package server;

import server.note.*;
// Apparently need to include this until I delete the old Note class under server
import server.note.Note;
import server.categories.MediaType;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;

import static server.NoteTestHelper.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

// Todo: Change the name to make this a unit test?
public class NoteTest {
    // ToDo: MP: Why are these test objects static?
    private static final int defaultElementCount = 5;
    private static Note noteWithRandomElements;
    private static Note emptyTestNote;
    private static Note subNote1;
    private static Note subNote2;
    private static final Entity artist = Entity.newArtist("Grand Funk", null);

    @BeforeAll
    public static void setUpTests() throws MalformedURLException {
        emptyTestNote = createGenericTestNote();
        noteWithRandomElements = createGenericTestNoteWithRandomElements(defaultElementCount);
        subNote1 = createGenericTestNote("Test sub-note");
        subNote1.addChildNote(new Product("I'm Your Captain", null, artist).setType(MediaType.SONG));
        subNote2 = createGenericTestNote("Test sub-note");
        subNote2.addChildNote(new Link("Google", "http://facebook.com"));
    }

    @AfterEach
    public void testCleanup() {
        clearChildNotes(emptyTestNote);
        noteWithRandomElements = createGenericTestNoteWithRandomElements(defaultElementCount);
    }

    // ToDo: Add test for each Note type

    @Test
    // ToDo: MP points out that this implicitly tests the items ORDER as well
    //  Need separate tests for ordering
    public void createNoteWithEachElementType() throws MalformedURLException {
        emptyTestNote.addChildNote(Entity.newContact("Contact1", null).setAddress(
                new Address().setPhoneNumber("234-5678").setAddress1("123 4th St.").setCity("Menlo Park")));
        emptyTestNote.addChildNote(new Product("Blind Alley", null, Entity.newArtist("Fanny", null))
                .setType(MediaType.SONG));
        // emptyTestNote.addElement(new Price(5.00));
        emptyTestNote.addChildNote(new Link("Google", "http://google.com"));
        emptyTestNote.addChildNote(new ScheduleItem("Deadline", null, "2020-06-12 00:00:00"));
        emptyTestNote.addChildNote(new Note("Note1"));

        System.out.println(emptyTestNote);
        // ToDo: Use fluent assertions (no need for Hamcrest matchers)?
        // assertThat("Expected Contact type at position 0", testNote.getElement(0));
        // ToDo: Rethink the the search tests (the ordering of the child-notes is less important now)
/*
        assertSame(emptyTestNote.getChildNotes().getFirst(), emptyTestNote.getElement(Contact.class, 0),
                "Expected Contact type at position 0");
        assertSame(emptyTestNote.getChildNotes().get(1), emptyTestNote.getElement(MediaItem.class, 0),
                "Expected MediaItem type at position 1");
        assertSame(emptyTestNote.getElement(2), emptyTestNote.getElement(Price.class, 0),
                "Expected Price type at position 2");
        assertSame(emptyTestNote.getElement(3), emptyTestNote.getElement(Link.class, 0),
                "Expected Link type at position 3");
        assertSame(emptyTestNote.getElement(4), emptyTestNote.getElement(EventInfo.class, 0),
                "Expected EventInfo type at position 4");
*/
    }

    @Test
    public void createNoteWithTwoSubNotes() {
        emptyTestNote.addChildNote(subNote1);
        emptyTestNote.addChildNote(subNote2);

        // System.out.println(emptyTestNote);
        assertEquals(2, emptyTestNote.getChildNotes().size(), "Number of child notes");
        // ToDo: Rethink the search tests
/*
        assertSame(emptyTestNote.getElement(0), emptyTestNote.getElement(Note.class, 0),
                "Expected Contact type at position 0");
        assertSame(emptyTestNote.getElement(1), emptyTestNote.getElement(Note.class, 1),
                "Expected Contact type at position 0");
*/
        // ToDo: Assert size of sub-note list = 2
    }

    @Test
    // ToDo: MP suggests callingClearRemovesAllElements() (i.e. we're testing the clear/delete function)
    public void removeAllElements() {
        // System.out.println(noteWithRandomElements);
        assertEquals(defaultElementCount, noteWithRandomElements.getChildNotes().size(), "Number of Note elements");
        clearChildNotes(noteWithRandomElements);
        assertEquals(0, noteWithRandomElements.getChildNotes().size(), "Number of Note elements");
    }

    @Test
    public void removeOneOfMultipleElements() {
        NoteBase note = noteWithRandomElements.getChildNotes().getFirst();
        noteWithRandomElements.removeChildNote(note);
        // This test has randomly failed here:
        assertFalse(noteWithRandomElements.getChildNotes().contains(note),
                "Note contains element '" + note.getName() + "'");
        assertEquals(defaultElementCount - 1, noteWithRandomElements.getChildNotes().size(),
                "Number of note elements after deletion");
    }

    // ToDo: Validate elementLocator after add and remove (make it protected?)

    @Test
    public void addElementAtASpecifiedLocation() {
        NoteBase testNoteElement = NoteTestHelper.getRandomNoteElement();
        int elementIndex = 3;
        noteWithRandomElements.addChildNote(testNoteElement, elementIndex);
        assertEquals(defaultElementCount + 1, noteWithRandomElements.getChildNotes().size(), "Number of Note elements");
        assertSame(testNoteElement, noteWithRandomElements.getChildNotes().get(elementIndex),
                "Expected note element '" + testNoteElement.getName() + "' at index " + elementIndex);
    }

    @Test
    // ToDo: Test this on a note with multiple sub-notes of different types (e.g. noteWithRandomElements)
    // ToDo: MP suggests gettingNonExistentNoteTypeThrowsError()
    public void tryGettingInvalidClassTypeFromElementList() {
        assertEquals(0, emptyTestNote.getChildNotes().size(), "Number of child notes in emptyTestNote");
        Exception ex = assertThrows(NoSuchElementException.class, () -> emptyTestNote.getChildNotes().getFirst());
        assertNull(ex.getMessage(), "For some reason the exception message is null");
    }

    @Test
    public void tryGettingInvalidElementIndexFromElementList() throws MalformedURLException {
        emptyTestNote.addChildNote(new Link("Google", "http://facebook.com"));
        // ToDo: Rethink the search tests
        // Exception ex = assertThrows(RuntimeException.class, () -> emptyTestNote.getElement(Link.class, 1));
        // System.out.println("Exception message: " + ex.getMessage());
        String expectedMessage = "Invalid index (1) for list of Link elements (size 1)";
        // assertEquals(expectedMessage, ex.getMessage(), "Exception message equals " + expectedMessage);
    }

    // ToDo: Test adding and removing more than one of the same kind of element
    // ToDo: Test adding and removing tags
    // ToDo: Test adding and removing categories (coupled with main program)?
    // ToDo: Test adding tags and categories?
    // ToDo: More negative tests?
    // ToDo: Test adding duplicate notes?
    // ToDo: Test adding a Link element with an invalid URL
}
