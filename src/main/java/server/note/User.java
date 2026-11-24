package server.note;

import jakarta.persistence.*;
import ui.request.NotePositionRequest;

import java.util.ArrayList;
import java.util.List;

@jakarta.persistence.Entity    // Import includes full path due to conflict with the Entity class
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")   // Allows the User->Note relationship to be one-way
    @OrderBy("position ASC")
    // @JsonManagedReference
    private List<NoteBase> notes = new ArrayList<>();

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<NoteBase> getNotes() { return notes; }
    public void addNote(NoteBase note, NotePositionRequest request) {
        notes.add(note);
        setNotePosition(note, request);
    }
    // ToDo: Need removeNote()

    private static final double positionInterval = 10000.00;
    // Handles adding or moving a note
    private void setNotePosition(NoteBase note, NotePositionRequest request) {
        Double above = request.noteAbovePosition;
        Double below = request.noteBelowPosition;
        if (above == null && below == null) {   // This is the first note in the list
            note.setPosition(positionInterval);
        }
        else if (above == null) {    // This note has been moved to the top of the list
            note.setPosition(below - positionInterval);
        }
        else if (below == null) {    // This note has been moved to the bottom of the list
            note.setPosition(above + positionInterval);
        }
        else {  // Calculate a position between the two notes
            // ToDo: If the interval between notes gets too small may need to "defrag" note positions in the DB
            note.setPosition(above + (below - above)/2.0);
        }
    }
}
