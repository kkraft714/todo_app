package server;

import server.note.NoteBase;

import java.util.ArrayList;
import java.util.List;

/*
** Extends the NoteOrganizer class with test APIs
*/
public class TestOrganizer extends NoteOrganizer {
    protected NoteBase addTestNote() {
        NoteBase testNote = NoteTestHelper.createGenericTestNote();
        addNote(testNote);
        return testNote;
    }
    protected List<NoteBase> addTestNotes(int noteCount) {
        List<NoteBase> noteList = new ArrayList<>();
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
