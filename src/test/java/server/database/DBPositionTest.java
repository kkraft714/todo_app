package server.database;

import server.note.*;
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

    @Test
    public void addTwoNotes() {
        // ToDo: Factor out a helper method for this?
        testUser.addNote(getSimpleNote(), new NotePositionRequest(null, null));
        testUser.addNote(getSimpleNote(), new NotePositionRequest(null, null));
        commitUserToDB(testUser);
        assertEquals(2, retrieveTestUserFromDB().getNotes().size(), "Number of notes for user " + testUser.getName());
    }

    @Test
    public void addNoteAtTopOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase currentTopNote = testBedUser.getNotes().getFirst();
        NoteBase newNote = getSimpleNote();
        testBedUser.addNote(newNote, new NotePositionRequest(null, currentTopNote));
        Double newNotePosition = newNote.getPosition();
        commitUserToDB(testBedUser);
        testBedUser = retrieveUserFromDB(testBedUser);
        assertEquals(6, testBedUser.getNotes().size(), "Number of notes for User");
        assertEquals(newNotePosition, testBedUser.getNotes().getFirst().getPosition(), "Position of first Note");
    }

    @Test
    public void addNoteAtBottomOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase currentBottomNote = testBedUser.getNotes().getLast();
        NoteBase newNote = getSimpleNote();
        testBedUser.addNote(newNote, new NotePositionRequest(currentBottomNote, null));
        Double newNotePosition = newNote.getPosition();
        commitUserToDB(testBedUser);
        testBedUser = retrieveUserFromDB(testBedUser);
        assertEquals(6, testBedUser.getNotes().size(), "Number of notes for User");
        assertEquals(newNotePosition, testBedUser.getNotes().getLast().getPosition(), "Position of last Note");
    }

    @Test
    public void moveNoteToTopOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase firstNote = testBedUser.getNotes().getFirst();
        NoteBase noteToMove = testBedUser.getNotes().get(2);
        testBedUser.setNotePosition(noteToMove, new NotePositionRequest(null, firstNote));
        commitUserToDB(testBedUser);
        testBedUser = retrieveUserFromDB(testBedUser);
        assertEquals(noteToMove, testBedUser.getNotes().getFirst(), "First Note in list");
    }

    @Test
    public void moveNoteToBottomOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase lastNote = testBedUser.getNotes().getLast();
        NoteBase noteToMove = testBedUser.getNotes().get(2);
        testBedUser.setNotePosition(noteToMove, new NotePositionRequest(lastNote, null));
        commitUserToDB(testBedUser);
        testBedUser = retrieveUserFromDB(testBedUser);
        assertEquals(noteToMove, testBedUser.getNotes().getLast(), "Last Note in list");
    }

    @Test
    public void moveNoteInBetweenTwoNotes() {
        User testBedUser = createFiveNoteTestBed();
        // Move first note in between 3rd and 4th (index 2 and 3)
        NoteBase noteAbove = testBedUser.getNotes().get(2);
        NoteBase noteBelow = testBedUser.getNotes().get(3);
        NoteBase noteToMove = testBedUser.getNotes().getFirst();
        testBedUser.setNotePosition(noteToMove, new NotePositionRequest(noteAbove, noteBelow));
        commitUserToDB(testBedUser);
        testBedUser = retrieveUserFromDB(testBedUser);
        assertEquals(noteAbove, testBedUser.getNotes().get(1), "2nd Note in list");
        assertEquals(noteToMove, testBedUser.getNotes().get(2), "3rd Note in list");
        assertEquals(noteBelow, testBedUser.getNotes().get(3), "4th Note in list");
    }

    @Test
    public void noteAreRetrievedFromDBInPositionOrder() {
        // Add notes out of position order
        for (double position : new double[] {3.0, 5.0, 1.0, 4.0, 2.0}) {
            Note newNote = getTestNote();
            newNote.setPosition(position);
            testUser.getNotes().add(newNote);
        }
        testUser = commitUserToDB(testUser);
        testUser = retrieveUserFromDB(testUser);
        double lastPosition = 0.0;
        // Confirm that notes are retrieved from the DB in position order
        for (int i = 0; i < testUser.getNotes().size(); i++) {
            double currentPosition = testUser.getNotes().get(i).getPosition();
            assertTrue(lastPosition < currentPosition, "Position of note at " + i + " (" + currentPosition
                    + " is greater than position of previous note (" + lastPosition + ")");
            lastPosition = currentPosition;
        }
    }

    // ToDo: Tests:
    //  - Test ordering for child notes
    //  - Test ordering for links
}
