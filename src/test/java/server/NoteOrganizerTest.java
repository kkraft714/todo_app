package server;

import server.note.*;
import server.categories.MediaType;
import org.junit.jupiter.api.*;

import java.util.*;
import static server.NoteTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

// ToDo: Create a TestBase class with common setup and teardown (and helper) methods?
// ToDo: Add tests:
//  Confirm that new notes and categories (and notes in categories) are added at the end
//  Add multiple notes to a category at a specified position (why do we need this test?)
//  Test standard Note categories (set up in initialize())
//  Test adding duplicate Categories (should have no effect?)
// ToDo: Figure out JUnit assertThat() with matchers (and replace assertTrue())
// ToDo: Switch to TestNG Test annotations and framework (instead of JUnit)?
//   If so use the description attribute to document the tests
// Todo: Change the name to make this a unit test?
public class NoteOrganizerTest {
    private static NoteOrganizer main;
    private static final String defaultCategoryName = "NewCategory";

    @BeforeEach
    public void initialize() { main = new NoteOrganizer(); }

    @AfterEach
    public void testCleanup() { NoteTestHelper.resetNoteNumber(); }

    @Test
    public void addNewNote() {
        addTestNote(main);
        assertEquals(1, main.getNotes().size(), "Number of notes");
    }

    @Test
    public void addNewNoteAtASpecifiedLocation() {
        // ToDo: Need new NoteOrganizer API for this
    }

    @Test
    // Remove the only note in the list
    public void removeOnlyNote() {
        NoteBase newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        NoteBase deletedNote = main.deleteNote(newNote);
        // ToDo: MP suggests using notes.size() directly and losing main.numberOfNotes()
        assertEquals(0, main.getNotes().size(), "Number of notes");
        assertSame(newNote, deletedNote, "Note removed");
    }

    @Test
    public void addMultipleNotes() {
        int expectedCount = 3;
        addTestNotes(expectedCount, main);
        assertEquals(expectedCount, main.getNotes().size(), "Number of notes");
    }

    @Test
    // ToDo: Verify that the SAME notes are being deleted that were added
    //   What this is REALLY testing is removing the note at index 0
    public void removeMultipleNotes() {
        int expectedCount = 3;
        addTestNotes(expectedCount, main);
        for (int i = 0; i < expectedCount; i++) {
            NoteBase noteToRemove = main.getNote(0);
            // ToDo: Test the index version too: deleteNote(int index)?
            NoteBase removedNote = main.deleteNote(noteToRemove);
            assertEquals(expectedCount - i - 1, main.getNotes().size(), "Number of notes");
            assertSame(noteToRemove, removedNote, "Note removed");
        }
    }

    @Test
    public void removeAllNotes() {
        int initialNoteCount = 5;
        addTestNotes(initialNoteCount, main);
        main.getNotes().clear();
        assertEquals(0, main.getNotes().size(), "Number of remaining notes after deletion");
    }

    @Test
    public void removeOneOfMultipleNotes() {
        int initialNoteCount = 5;
        addTestNotes(initialNoteCount,  main);
        // ToDo: Verify that I deleted the RIGHT note
        main.deleteNote(0);
        assertEquals(initialNoteCount - 1, main.getNotes().size(), "Number of remaining notes after deletion");
    }

    @Test
    public void addNoteWithCategories() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        String[] categories = new String[] {"category1", "category2", "category3"};
        main.addNoteToCategories(new HashSet<>(Arrays.asList(categories)), newNote);
        main.addCategory("category4");
        main.deleteNote(newNote);
        // Confirm that the note has been deleted from all categories
        for (String name : main.getCategories()) {
            assertFalse(main.getNotesForCategory(name).contains(newNote),
                    "Category '" + name + "' contains note '" + newNote.getName() + "'");
        }
    }

    @Test
    // ToDo: Is this dependent on previous test results?
    public void tryRemovingInvalidNoteByIndex() {
        Exception ex = assertThrows(RuntimeException.class, () -> main.deleteNote(1));
        assertEquals("Invalid index (1) for note list (size 0)", ex.getMessage(), "Exception message");
    }

    @Test
    public void tryRemovingInvalidNoteByObject() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class, () -> main.deleteNote(newNote));
        assertEquals("Failed to locate note: '" + newNote.getName() + "'", ex.getMessage(), "Exception message");
    }

    @Test
    public void addNewCategory() {
        main.addCategory(defaultCategoryName);
        assertEquals(1, numberOfCategories(main), "Number of categories");
    }

    @Test
    public void addCategoryWithSpaceInName() {
        String categoryName = "Category name with spaces";
        main.addCategory(categoryName);
        Set<NoteBase> category = main.getNotesForCategory(categoryName);
        assertEquals(0, category.size(), "Number of notes in category");
    }

    @Test
    // This is also removing the *only* category?
    public void removeLastCategory() {
        main.addCategory(defaultCategoryName);
        main.deleteCategory(defaultCategoryName);
        // assertEquals(0, numberOfCategories(main), "Number of categories");
        assertEquals(0, main.getCategories().size(), "Number of categories");
    }

    // ToDo: addMultipleCategories()
    // ToDo: removeOneOfMultipleCategories()

    static final String invalidCategory = "invalidCategory";
    @Test
    public void tryGettingInvalidCategory() {
        // ToDo: Replace this with categories from main
        assertEquals(0, numberOfCategories(main), "Number of categories");
     }

    @Test
    public void tryRemovingInvalidCategory() {
        Exception ex = assertThrows(RuntimeException.class, () -> main.deleteCategory(invalidCategory));
        String expectedMessage = "Unable to locate category '" + invalidCategory + "'";
        assertTrue(ex.getMessage().contains(expectedMessage),
                "Exception message contains '" + expectedMessage + "'");
    }

    @Test
    public void addNoteToCategory() {
        main.addNoteToCategory(defaultCategoryName, NoteTestHelper.createGenericTestNote());
        Set<NoteBase> category = main.getNotesForCategory(defaultCategoryName);
        assertEquals(1, category.size(),
                "Number of notes in category '" + defaultCategoryName + "'");
    }

    @Test
    public void removeNoteFromCategory() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNoteToCategory(defaultCategoryName, newNote);
        main.removeNoteFromCategory(defaultCategoryName, newNote);
        Set<NoteBase> category = main.getNotesForCategory(defaultCategoryName);
        assertEquals(0, category.size(),
                "Number of notes in category '" + defaultCategoryName + "'");
    }

    @Test
    public void addMultipleNotesToCategory() {
        String categoryName = "testCategory";
        int expectedCount = 3;
        List<NoteBase> noteList = addTestNotes(expectedCount,  main);
        main.addNotesToCategory(categoryName, noteList);
        assertEquals(expectedCount, main.getNotesForCategory(categoryName).size(),
                "Number of notes in '" + categoryName + "'");
        for (NoteBase n : noteList) {
            assertTrue(main.getNotesForCategory(categoryName).contains(n),
                    "Category " + categoryName + " contains note '" + n.getName() + "'");
        }
    }

    @Test
    public void removeMultipleNotesFromCategory() {
        int expectedCount = 3;
        List<NoteBase> noteList = addTestNotes(expectedCount, main);
        main.addNotesToCategory(defaultCategoryName, noteList);
        for (int i = 0; i < expectedCount; i++) {
            main.removeNoteFromCategory(defaultCategoryName, noteList.get(i));
            assertEquals(expectedCount - i - 1, main.getNotesForCategory(defaultCategoryName).size(),
                    "Number of notes in '" + defaultCategoryName + "'");
        }
    }

    // removeOneOfMultipleNotesFromCategory()

    @Test
    public void addNoteToMultipleCategories() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        HashSet<String> categories = new HashSet<>(Arrays.asList("category1", "category2", "category3"));
        main.addNoteToCategories(categories, newNote);
        for (String category : categories) {
            assertTrue(main.getNotesForCategory(category).contains(newNote), "Category " + category + " contains note '" + newNote.getName() + "'");
        }
    }

    @Test
    public void removeNoteFromMultipleCategories() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        HashSet<String> categories = new HashSet<>(Arrays.asList("category1", "category2", "category3"));
        main.addNoteToCategories(categories, newNote);
        for (String category : categories) {
            main.removeNoteFromCategory(category, newNote);
            assertFalse(main.getNotesForCategory(category).contains(newNote),
                    "Category " + category + " contains note '" + newNote.getName() + "'");
        }
    }

    @Test
    public void tryRemovingNoteFromInvalidCategory() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class,
                () -> main.removeNoteFromCategory(invalidCategory, newNote));
        assertEquals("Unable to locate category '" + invalidCategory + "'", ex.getMessage(),
                "Exception message");
    }

    @Test
    public void tryRemovingInvalidNoteFromCategory() {
        main.addCategory(defaultCategoryName);
        NoteBase newNote = NoteTestHelper.createGenericTestNote();
        Exception ex = assertThrows(RuntimeException.class,
                () -> main.removeNoteFromCategory(defaultCategoryName, newNote));
        assertEquals("Unable to locate note '" + newNote.getName() + "' in category '" + defaultCategoryName + "'",
                ex.getMessage(), "Exception message");
    }

    @Test
    public void getSingleNoteWithCategory() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        String mediaType = MediaType.SONG.toString();
        newNote.addCategory(mediaType);
        main.addNote(newNote);
        Set<NoteBase> notes = main.getNotesForCategory(mediaType);
        assertEquals(1, notes.size(), "Number of notes with media type " + mediaType);
        assertTrue(notes.remove(newNote), "New note with media type " + mediaType + " is in list");
    }

    @Test
    public void getMultipleNotesWithSameCategory() {
        String mediaType = MediaType.SONG.name();
        int numberOfNotes = 3;
        addNotesWithCategory(numberOfNotes, mediaType, main);
        Set<NoteBase> notes = main.getNotesForCategory(mediaType);
        assertEquals(numberOfNotes, notes.size(), "Number of notes with media type " + mediaType);
    }

    @Test
    public void getMultipleNotesWithVariousCategories() {
        int numberOfNotes = 10;
        int total = 0;
        addNotesWithCategory(numberOfNotes, null, main);
        for (String cat : getCategories(main)) {
            Set<NoteBase> notes = main.getNotesForCategory(cat);
            assertEquals(numberOfNotesForCategory(cat, main), notes.size(),
                    "Number of notes with media type " + cat);
            total += notes.size();
        }
        assertEquals(numberOfNotes, total, "Total number of tagged notes");
    }

    @Test
    public void getCategorizedNotesFromListContainingCategorizedAndUncategorizedNotes() {
        int numberOfCategorizedNotes = 10;
        int numberOfUncategorizedNotes = 5;
        int total = 0;
        addNotesWithCategory(numberOfCategorizedNotes, null, main);
        // This is identical to the above test except for this line adding untagged notes
        addTestNotes(numberOfUncategorizedNotes, main);
        assertEquals(numberOfCategorizedNotes + numberOfUncategorizedNotes, main.getNotes().size(),
                "Total number of notes");
        for (String cat : getCategories(main)) {
            Set<NoteBase> notes = main.getNotesForCategory(cat);
            assertEquals(numberOfNotesForCategory(cat, main), notes.size(),
                    "Number of notes with media type " + cat);
            total += notes.size();
        }
        assertEquals(numberOfCategorizedNotes, total, "Total number of tagged notes");
    }

    @Test
    public void tryGettingCategorizedNotesFromListContainingOnlyUncategorizedNotes() {
        addTestNotes(10, main);     // These are uncategorized notes
        assertEquals(0, NoteTestHelper.numberOfCategories(main), "Number of test categories tracked");
        for (String cat : MediaType.get().convertToStrings()) {
            Set<NoteBase> notes = main.getNotesForCategory(cat);
            // ToDo: Add assertNotNull()?
            assertEquals(0, notes.size(), "Number of notes with media type " + cat);
        }
    }

    @Test
    public void tryGettingCategorizedNotesFromEmptyList() {
        // The list of test notes is empty by default
        // ToDo: Replace this with categories
        assertEquals(0, numberOfCategories(main), "Number of test categories tracked");
        for (String cat : MediaType.get().convertToStrings()) {
            Set<NoteBase> notes = main.getNotesForCategory(cat);
            assertEquals(0, notes.size(), "Number of notes with media type " + cat);
        }
    }

    // ToDo: Add test for trying to get tagged note from list that doesn't contain the tag

    @Test
    public void getCategorizedNotesWithOrRelationship() {
        Set<String> categories = createNotesWithMultipleCategories(main);
        Set<NoteBase> orResult = main.getNotesWithAnyCategories(categories);
        assertEquals(partitionCount*3, orResult.size(), "Number of notes with any queried categories");
    }

    @Test
    public void getCategorizedNotesWithAndRelationship() {
        Set<String> categories = createNotesWithMultipleCategories(main);
        Set<NoteBase> andResult = main.getNotesWithAllCategories(categories);
        assertEquals(partitionCount, andResult.size(), "Number of notes with all queried categories");
    }

    // Re-run (at least some) tagging tests with category lists instead of main

    // @Test
    public void testTemplate() {
        throw new RuntimeException("Test not yet implemented");
    }
}
