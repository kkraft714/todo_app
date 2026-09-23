package server;

import server.categories.CategoryTag;
import server.categories.MediaType;
import server.element.Address;
import server.note.*;
import java.util.*;

public class NoteTestHelper {
    private static final Random rand = new Random();
    private static final int defaultElementCount = 5;
    private static int noteNumber = 0;
    private static final Map<CategoryTag, Integer> tagTracker = new HashMap<>();


    // ======================== Note Test Helper Methods ======================
    // ToDo: Will static variables work if the tests are run in parallel?
    public static void resetNoteNumber() {
        noteNumber = 0;
    }

    protected static Note createGenericTestNote() {
        return createGenericTestNote(null);
    }

    protected static Note createGenericTestNote(String baseName) {
        noteNumber++;
        // ToDo: MP: Use a GUID instead of adding noteNumber?
        String name = (baseName == null ? "Test Note" : baseName) + " #" + noteNumber;
        return new Note(name, "Description for " + name);
    }

    // ToDo: Create test API that can add a specified list of Element types?
    private static final int numberOfElementTypes = 5;
    private static int contactCount = 0, songCount = 0, linkCount = 0, eventCount = 0, noteCount = 0;
    // ToDo: Make sure all Note-type objects are covered
    // ToDo: Rename to getRandomNoteType()?
    public static NoteBase getRandomNoteElement() {
        int elementTypeIndex = rand.nextInt(numberOfElementTypes);
        return switch (elementTypeIndex) {
            case 0 -> {
                contactCount++;
                yield Contact.newContact("First", "Contact" + contactCount, null).addAddress(
                        new Address().setPhoneNumber("345-5679").setAddress1("222 2nd St.").setCity("Menlo Park"));
            }
            case 1 -> {
                songCount++;
                Entity owner = new Entity("The Who", null, Entity.EntityType.ARTIST);
                yield new Product("Who Song #" + songCount).setOwner(owner).setType(MediaType.SONG).setPrice(5.99);
            }
            case 2 -> {
                linkCount++;
                yield new Link("Web Site #" + linkCount, "http://website" + linkCount + ".com");
            }
            case 3 -> {
                eventCount++;
                yield new ScheduleItem("Event #" + eventCount, null, "2020-07-03 00:00:00");
            }
            case 4 -> {
                noteCount++;
                yield new Note("Note #" + noteCount);
            }
            default -> throw new RuntimeException("Illegal note element index: " + elementTypeIndex
                    + " (maximum is " + (numberOfElementTypes - 1) + ")");
        };
    }

    // ToDo: Replace these methods with a NoteBuilder class?
    //  * Will this work with polymorphism? Need a factory instead?
    //  * Define a constructor taking required parameters (title)
    //  * Define methods for adding other parameters
    //  * Define a build() method that calls the constructor and returns the note object
    //    Or maybe a factory class?
    protected static Note createGenericTestNoteWithRandomElements() {
        return createGenericTestNoteWithRandomElements(null, defaultElementCount);
    }

    protected static Note createGenericTestNoteWithRandomElements(String baseName) {
        return createGenericTestNoteWithRandomElements(baseName, defaultElementCount);
    }

    protected static Note createGenericTestNoteWithRandomElements(int numberOfElements) {
        return createGenericTestNoteWithRandomElements(null, numberOfElements);
    }

    protected static Note createGenericTestNoteWithRandomElements(String baseName, int numberOfElements) {
        Note testNote = createGenericTestNote(baseName);
        for (int i = 0; i < numberOfElements; i++) {
            testNote.addChildNote(getRandomNoteElement());
        }
        return testNote;
    }

    protected static void clearChildNotes(Note note) {
        note.getChildNotes().clear();
    }

    protected static NoteBase addTestNote(List<NoteBase> noteList) {
        NoteBase testNote = createGenericTestNote();
        noteList.add(testNote);
        return testNote;
    }

    protected static List<NoteBase> addTestNotes(int noteCount, List<NoteBase> noteList) {
        for (int i = 0; i < noteCount; i++) {
            addTestNote(noteList);
        }
        return noteList;
    }

    public static Set<CategoryTag> getTags() { return tagTracker.keySet(); }
    public static int numberOfTags() { return tagTracker.size(); }
    public static int numberOfTags(CategoryTag tag) { return tagTracker.get(tag); }
    public static void clearTags() { tagTracker.clear(); }

    // ToDo: Add Category parameter to pass in and test other lists
    public static void createNotesWithTags(int numberOfNotes, CategoryTag tag, List<NoteBase> noteList) {
        for (int i = 0; i < numberOfNotes; i++) {
            createNoteWithTag(tag, noteList);
        }
    }

    public static void createNoteWithTag(CategoryTag tag, List<NoteBase> noteList) {
        CategoryTag newTag = tag == null ? MediaType.get().getRandomTag() : tag;
        NoteBase newNote = NoteTestHelper.createGenericTestNote().addTag(newTag);
        updateTagTracker(newTag);
        System.out.println(newNote);
        noteList.add(newNote);
    }

    // ToDo: Create addNote() API that automatically updates tagTracker
    // Creates notes for testing AND and OR tag queries
    static final int partitionCount = 4;
    public static void createNotesWithMultipleTags(List<NoteBase> noteList) {
        addTestNotes(partitionCount*3, noteList);
        for (int i = 0; i < partitionCount*3; i++) {
            if (i < partitionCount*2) {
                noteList.get(i).addTag(MediaType.BOOK);
                updateTagTracker(MediaType.BOOK);
            }
            if (i >= partitionCount) {
                noteList.get(i).addTag(MediaType.FILM);
                updateTagTracker(MediaType.FILM);
            }
        }
    }

    private static void updateTagTracker(CategoryTag newTag) {
        if (!tagTracker.containsKey(newTag)) {
            tagTracker.put(newTag, 0);
        }
        tagTracker.put(newTag, tagTracker.get(newTag) + 1);
    }
}
