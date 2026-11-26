package ui.request;

import server.note.NoteBase;

// ToDo: Update this to include only the Note and index
public class NotePositionRequest {
    public Double noteAbovePosition;
    public Double noteBelowPosition;

    protected NotePositionRequest() {}   // No-arg constructor for JSON deserialization

    public NotePositionRequest(NoteBase note1, NoteBase note2) {
        noteAbovePosition = note1 != null ? note1.getPosition() : null;
        noteBelowPosition = note2 != null ? note2.getPosition() : null;
    }
}
