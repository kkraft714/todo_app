package server;

import server.note.*;
import server.categories.CategoryTag;
import server.categories.MediaType;
import org.junit.jupiter.api.*;

import java.util.*;
import static server.NoteTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

// ToDo: Add tests:
//  Confirm that new notes and categories (and notes in categories) are added at the end
//  Add multiple notes to a category at a specified position
//  Test standard Note categories (set up in initialize())
//  Test adding duplicate Category or Tags
// ToDo: Figure out JUnit assertThat() with matchers (and replace assertTrue())
// ToDo: Switch to TestNG Test annotations (instead of JUnit)?
//   If so use the description attribute to document the tests
// ToDo: Is it an issue that tags and categories are handled separately but are conceptually the same?
// ToDo: Per MP should categories be statically defined and Tags be user defined (i.e. switch them)?
// Todo: Change the name to make this a unit test?
public class NoteOrganizerTest {
    private static NoteOrganizer main; // = new NoteOrganizer();
    private static final String defaultCategoryName = "NewCategory";

    @BeforeEach
    public void initialize() { main = new NoteOrganizer(); }

    @AfterEach
    public void testCleanup() {
        clearTags();
        NoteTestHelper.resetNoteNumber();
    }

    protected static int numberOfCategories() { return main.getCategories().size(); }

    @Test
    public void addNewNote() {
        addTestNote(main.getNotes());
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
        addTestNotes(expectedCount, main.getNotes());
        assertEquals(expectedCount, main.getNotes().size(), "Number of notes");
    }

    @Test
    // ToDo: Verify that the SAME notes are being deleted that were added
    //   What this is REALLY testing is removing the note at index 0
    public void removeMultipleNotes() {
        int expectedCount = 3;
        addTestNotes(expectedCount, main.getNotes());
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
        addTestNotes(initialNoteCount, main.getNotes());
        main.getNotes().clear();
        assertEquals(0, main.getNotes().size(), "Number of remaining notes after deletion");
    }

    @Test
    public void removeOneOfMultipleNotes() {
        int initialNoteCount = 5;
        addTestNotes(initialNoteCount,  main.getNotes());
        // ToDo: Verify that I deleted the RIGHT note
        main.deleteNote(0);
        assertEquals(initialNoteCount - 1, main.getNotes().size(), "Number of remaining notes after deletion");
    }

    @Test
    public void removeNoteWithCategories() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNote(newNote);
        String[] categories = new String[] {"category1", "category2", "category3"};
        main.addNoteToCategories(new HashSet<>(Arrays.asList(categories)), newNote);
        main.addCategory("category4");
        main.deleteNote(newNote);
        // Confirm that the note has been deleted from all categories
        for (String name : main.getCategories()) {
            assertFalse(main.getCategory(name).contains(newNote),
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
        assertEquals(1, numberOfCategories(), "Number of categories");
    }

    @Test
    public void addCategoryWithSpaceInName() {
        String categoryName = "Category name with spaces";
        main.addCategory(categoryName);
        List<NoteBase> category = main.getCategory(categoryName);
        assertEquals(0, category.size(), "Number of notes in category");
    }

    @Test
    // This is also removing the *only* category?
    public void removeLastCategory() {
        main.addCategory(defaultCategoryName);
        main.deleteCategory(defaultCategoryName);
        assertEquals(0, numberOfCategories(), "Number of categories");
    }

    // ToDo: addMultipleCategories()
    // ToDo: removeOneOfMultipleCategories()

    static final String invalidCategory = "invalidCategory";
    @Test
    public void tryGettingInvalidCategory() {
        Exception ex = assertThrows(RuntimeException.class, () -> main.getCategory(invalidCategory));
        assertEquals("Unable to locate category '" + invalidCategory + "'", ex.getMessage(),
                "Exception message");
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
        List<NoteBase> category = main.getCategory(defaultCategoryName);
        assertEquals(1, category.size(),
                "Number of notes in category '" + defaultCategoryName + "'");
    }

    @Test
    public void removeNoteFromCategory() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        main.addNoteToCategory(defaultCategoryName, newNote);
        main.removeNoteFromCategory(defaultCategoryName, newNote);
        List<NoteBase> category = main.getCategory(defaultCategoryName);
        assertEquals(0, category.size(),
                "Number of notes in category '" + defaultCategoryName + "'");
    }

    @Test
    public void addMultipleNotesToCategory() {
        String categoryName = "testCategory";
        int expectedCount = 3;
        List<NoteBase> noteList = addTestNotes(expectedCount,  main.getNotes());
        main.addNotesToCategory(categoryName, noteList);
        assertEquals(expectedCount, main.getCategory(categoryName).size(),
                "Number of notes in '" + categoryName + "'");
        for (NoteBase n : noteList) {
            assertTrue(main.getCategory(categoryName).contains(n),
                    "Category " + categoryName + " contains note '" + n.getName() + "'");
        }
    }

    @Test
    public void removeMultipleNotesFromCategory() {
        int expectedCount = 3;
        List<NoteBase> noteList = addTestNotes(expectedCount, main.getNotes());
        main.addNotesToCategory(defaultCategoryName, noteList);
        for (int i = 0; i < expectedCount; i++) {
            main.removeNoteFromCategory(defaultCategoryName, noteList.get(i));
            assertEquals(expectedCount - i - 1, main.getCategory(defaultCategoryName).size(),
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
            assertTrue(main.getCategory(category).contains(newNote), "Category " + category + " contains note '" + newNote.getName() + "'");
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
            assertFalse(main.getCategory(category).contains(newNote),
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
    public void getSingleNoteWithTag() {
        Note newNote = NoteTestHelper.createGenericTestNote();
        CategoryTag mediaType = MediaType.SONG;
        newNote.addTag(mediaType);
        main.addNote(newNote);
        List<NoteBase> taggedNotes = main.getNotesWithTag(mediaType);
        assertEquals(1, taggedNotes.size(), "Number of notes with media type " + mediaType);
        assertSame(newNote, taggedNotes.getFirst(), "Note with media type " + mediaType);
    }

    @Test
    public void getMultipleNotesWithSameTag() {
        CategoryTag mediaType = MediaType.SONG;
        int numberOfNotes = 3;
        createNotesWithTags(numberOfNotes, mediaType, main.getNotes());
        List<NoteBase> taggedNotes = main.getNotesWithTag(mediaType);
        assertEquals(numberOfNotes, taggedNotes.size(), "Number of notes with media type " + mediaType);
    }

    @Test
    public void getMultipleNotesWithVariousTags() {
        int numberOfNotes = 10;
        int total = 0;
        createNotesWithTags(numberOfNotes, null, main.getNotes());
        for (CategoryTag tag : getTags()) {
            List<NoteBase> taggedNotes = main.getNotesWithTag(tag);
            assertEquals(numberOfTags(tag), taggedNotes.size(),
                    "Number of notes with media type " + tag);
            total += taggedNotes.size();
        }
        assertEquals(numberOfNotes, total, "Total number of tagged notes");
    }

    @Test
    public void getTaggedNotesFromListContainingTaggedAndUntaggedNotes() {
        int numberOfTaggedNotes = 10;
        int numberOfUntaggedNotes = 5;
        int total = 0;
        createNotesWithTags(numberOfTaggedNotes, null, main.getNotes());
        // This is identical to the above test except for this line adding untagged notes
        addTestNotes(numberOfUntaggedNotes, main.getNotes());
        assertEquals(numberOfTaggedNotes + numberOfUntaggedNotes, main.getNotes().size(), "Total number of notes");
        for (CategoryTag tag : getTags()) {
            List<NoteBase> taggedNotes = main.getNotesWithTag(tag);
            assertEquals(numberOfTags(tag), taggedNotes.size(),
                    "Number of notes with media type " + tag);
            total += taggedNotes.size();
        }
        assertEquals(numberOfTaggedNotes, total, "Total number of tagged notes");
    }

    @Test
    public void tryGettingTaggedNotesFromListContainingOnlyUntaggedNotes() {
        addTestNotes(10, main.getNotes());    // These are untagged notes
        assertEquals(0, numberOfTags(), "Number of test tags tracked");
        for (CategoryTag tag : MediaType.get().getCategories()) {
            List<NoteBase> taggedNotes = main.getNotesWithTag(tag);
            assertEquals(0, taggedNotes.size(), "Number of notes with media type " + tag);
        }
    }

    @Test
    public void tryGettingTaggedNotesFromEmptyList() {
        // The list of test notes is empty by default
        assertEquals(0, numberOfTags(), "Number of test tags tracked");
        for (CategoryTag tag : MediaType.get().getCategories()) {
            List<NoteBase> taggedNotes = main.getNotesWithTag(tag);
            assertEquals(0, taggedNotes.size(), "Number of notes with media type " + tag);
        }
    }

    // ToDo: Add test for trying to get tagged note from list that doesn't contain the tag

    @Test
    public void getTaggedNotesWithOrRelationship() {
        createNotesWithMultipleTags(main.getNotes());
        List<NoteBase> orResult = main.getNotesWithAnyTags(getTags());
        assertEquals(partitionCount*3, orResult.size(), "Number of notes with any queried tags");
    }

    @Test
    public void getTaggedNotesWithAndRelationship() {
        createNotesWithMultipleTags(main.getNotes());
        List<NoteBase> andResult = main.getNotesWithAllTags(getTags());
        assertEquals(partitionCount, andResult.size(), "Number of notes with all queried tags");
    }

    // Re-run (at least some) tagging tests with category lists instead of main

    // @Test
    public void testTemplate() {
        throw new RuntimeException("Test not yet implemented");
    }
}
