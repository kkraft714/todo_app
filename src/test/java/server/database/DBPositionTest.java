package server.database;

import server.note.User;
import ui.request.NotePositionRequest;
import org.junit.jupiter.api.Test;

import static server.NoteTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class DBPositionTest extends DBTestBase {
    @Test
    public void addFirstNote() {
        testUser.addNote(getSimpleNote(), new NotePositionRequest(null, null));
        commitUserToDB(testUser);
        assertEquals(1, retrieveTestUserFromDB().getNotes().size(), "Number of notes for user " + testUser.getName());
    }
    // ToDo: Tests:
    //  - Add two notes
    //  - Add note at top
    //  - Add note at bottom
    //  - Move note to top
    //  - Move note to bottom
    //  - Move note in between 2 notes
    //  - Verify that notes are retrieved from DB in position order
    //  - Test ordering for child notes
    //  - Test ordering for links
}
