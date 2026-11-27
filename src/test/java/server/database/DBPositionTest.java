package server.database;

import server.note.*;
import org.junit.jupiter.api.Test;

import static server.NoteTestHelper.*;
import static org.junit.jupiter.api.Assertions.*;

public class DBPositionTest extends DBTestBase {
    @Test
    public void addFirstNote() {
        testUser.addNote(getSimpleNote(), 0);
        commitUserToDB(testUser);
        assertEquals(1, retrieveTestUserFromDB().getNotes().size(), "Number of notes for user " + testUser.getName());
    }

    @Test
    public void addSecondNoteAbove() {
        testUser.addNote(getSimpleNote(), 0);
        testUser.addNote(getSimpleNote(), 0);
        commitUserToDB(testUser);
        // ToDo: Confirm correct note positions (top note should be 0.0)?
        System.out.println("Position of 2nd note added: " + testUser.getNotes().getFirst().getPosition());
        assertEquals(2, retrieveTestUserFromDB().getNotes().size(), "Number of notes for user " + testUser.getName());
    }

    @Test
    public void addSecondNoteBelow() {
        testUser.addNote(getSimpleNote(), 0);
        testUser.addNote(getSimpleNote(), 1);
        commitUserToDB(testUser);
        // ToDo: Confirm correct note positions (last note should be 20000.0)?
        System.out.println("Position of 2nd note added: " + testUser.getNotes().getLast().getPosition());
        assertEquals(2, retrieveTestUserFromDB().getNotes().size(), "Number of notes for user " + testUser.getName());
    }

    @Test
    public void addNoteAtTopOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase newNote = getSimpleNote();
        testBedUser.addNote(newNote, 0);
        Double newNotePosition = newNote.getPosition();
        testBedUser = commitAndRetrieveTestUser(testBedUser);
        assertEquals(6, testBedUser.getNotes().size(), "Number of notes for User");
        assertEquals(newNotePosition, testBedUser.getNotes().getFirst().getPosition(), "Position of first Note");
    }

    @Test
    public void addNoteAtBottomOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase newNote = getSimpleNote();
        testBedUser.addNote(newNote, 5);
        Double newNotePosition = newNote.getPosition();
        testBedUser = commitAndRetrieveTestUser(testBedUser);
        assertEquals(6, testBedUser.getNotes().size(), "Number of notes for User");
        assertEquals(newNotePosition, testBedUser.getNotes().getLast().getPosition(), "Position of last Note");
    }

    @Test
    public void addNoteInMiddleOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase newNote = getSimpleNote();
        testBedUser.addNote(newNote, 2);
        Double newNotePosition = newNote.getPosition();
        testBedUser = commitAndRetrieveTestUser(testBedUser);
        assertEquals(6, testBedUser.getNotes().size(), "Number of notes for User");
        assertEquals(newNotePosition, testBedUser.getNotes().get(2).getPosition(), "Position of 3rd Note");
    }

    @Test
    public void moveNoteToTopOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase noteToMove = testBedUser.getNotes().get(2);
        testBedUser.addNote(noteToMove, 0);
        testBedUser = commitAndRetrieveTestUser(testBedUser);
        assertEquals(noteToMove, testBedUser.getNotes().getFirst(), "First Note in list");
    }

    @Test
    public void moveNoteToBottomOfList() {
        User testBedUser = createFiveNoteTestBed();
        NoteBase noteToMove = testBedUser.getNotes().get(2);
        testBedUser.addNote(noteToMove, 5);
        testBedUser = commitAndRetrieveTestUser(testBedUser);
        assertEquals(noteToMove, testBedUser.getNotes().getLast(), "Last Note in list");
    }

    @Test
    public void moveNoteToMiddleOfList() {
        User testBedUser = createFiveNoteTestBed();
        // Move 1st note in between 3rd and 4th (index 2 and 3)
        NoteBase noteAbove = testBedUser.getNotes().get(2);
        NoteBase noteBelow = testBedUser.getNotes().get(3);
        NoteBase noteToMove = testBedUser.getNotes().getFirst();
        testBedUser.addNote(noteToMove, 2);
        testBedUser = commitAndRetrieveTestUser(testBedUser);
        assertEquals(noteAbove, testBedUser.getNotes().get(1), "2nd Note in list");
        assertEquals(noteToMove, testBedUser.getNotes().get(2), "3rd Note in list");
        assertEquals(noteBelow, testBedUser.getNotes().get(3), "4th Note in list");
    }

    @Test
    public void notesAreRetrievedFromDBInPositionOrder() {
        // Add notes out of position order
        for (double position : new double[] {3.0, 5.0, 1.0, 4.0, 2.0}) {
            Note newNote = getTestNote();
            // Skipping the automated User position handling here
            newNote.setPosition(position);
            testUser.getNotes().add(newNote);
        }
        testUser = commitUserToDB(testUser);
        testUser = retrieveUserFromDB(testUser);
        double lastPosition = 0.0;
        // Confirm that notes are retrieved from the DB in position order
        for (int i = 0; i < testUser.getNotes().size(); i++) {
            double currentPosition = testUser.getNotes().get(i).getPosition();
            assertTrue(lastPosition < currentPosition, "Position of note at " + i
                    + " (" + currentPosition + ") is greater than position of previous note (" + lastPosition + ")");
            lastPosition = currentPosition;
        }
    }

    // ToDo: Tests:
    //  - Test ordering for child notes
    //  - Test ordering for links
    //  - Test deletion as well?
}
