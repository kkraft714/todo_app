package server;

import server.categories.MediaType;
import server.element.Address;
import server.note.*;
// Apparently need to include this until I delete the old Note class under server
import server.note.Note;
import java.util.*;

public class NoteTestHelper {
    private static final Random rand = new Random();
    private static final int defaultElementCount = 5;
    private static int noteNumber = 0;

    // ======================== Note Test Helper Methods ======================
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
    public static NoteBase getRandomNoteElement() {
        int elementTypeIndex = rand.nextInt(numberOfElementTypes);
        return switch (elementTypeIndex) {
            case 0 -> {
                contactCount++;
                yield Entity.newContact("Contact" + contactCount, null).setAddress(
                        new Address().setPhoneNumber("345-5679").setAddress1("222 2nd St.").setCity("Menlo Park"));
            }
            case 1 -> {
                songCount++;
                Entity owner = Entity.newArtist("The Who", null);
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
                // ToDo: Not sure why it's picking up the Note class from server.element instead of server.note
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
}
