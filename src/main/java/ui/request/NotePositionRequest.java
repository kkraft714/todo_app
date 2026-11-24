package ui.request;

import server.note.NoteBase;

public class NotePositionRequest {
    public Double noteAbovePosition;
    public Double noteBelowPosition;

    public NotePositionRequest(NoteBase note1, NoteBase note2) {
        noteAbovePosition = note1 != null ? note1.getPosition() : null;
        noteBelowPosition = note2 != null ? note2.getPosition() : null;
    }
}
