package server;

import java.util.ArrayList;
import java.util.List;

/*
** Extends the NoteOrganizer class with test APIs
*/
public class TestOrganizer extends NoteOrganizer {
    protected Note addTestNote() {
        Note testNote = NoteTestHelper.createGenericTestNote();
        addNote(testNote);
        return testNote;
    }
    protected List<Note> addTestNotes(int noteCount) {
        List<Note> noteList = new ArrayList<>();
        for (int i = 0; i < noteCount; i++) {
            noteList.add(addTestNote());
        }
        return noteList;
    }

    protected void clearNotes() { notes.clear(); }
    protected void clearCategories() { categories.clear(); }
    protected int numberOfNotes() { return notes.size(); }
    protected int numberOfCategories() { return categories.size(); }
}
