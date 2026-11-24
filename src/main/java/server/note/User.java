package server.note;

import jakarta.persistence.*;
import ui.request.NotePositionRequest;

import java.util.ArrayList;
import java.util.List;

import static server.NoteUtils.LOG;

// ToDo: Should this move out of the note package under the server package?
@jakarta.persistence.Entity    // Import includes full path due to conflict with the Entity class
@Table(name = "note_user")   // "user" is a reserved word in PostgreSQL
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    // ToDo: Need to add first name, last name, email etc. (add a Contact field?)

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")   // Allows the User->Note relationship to be one-way
    @OrderBy("position ASC")
    // @JsonManagedReference
    private List<NoteBase> notes = new ArrayList<>();

    // ToDo: This is just a placeholder (need to support more fields/functionality)
    public User(String name) { this.name = name; }
    protected User() {}    // Required zero-arg constructor for Hibernate

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<NoteBase> getNotes() { return notes; }
    public void addNote(NoteBase note, NotePositionRequest request) {
        notes.add(note);
        setNotePosition(note, request);
    }
    public void addNoteAtEndOfList(NoteBase note) {
        NoteBase noteAbove = !notes.isEmpty() ? notes.getLast() : null;
        addNote(note, new NotePositionRequest(noteAbove, null));
    }
    // ToDo: Support addNote() with index (and recalculate positions)
    // ToDo: Need removeNote()

    private static final double positionInterval = 10000.00;
    private static final double minimumInterval = 1e-9;    // 0.000000001
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
            double newPosition = above + (below - above)/2.0;
            if (newPosition < minimumInterval) {     // ToDo: Test triggering this?
                LOG.warn("Interval between note positions has exceeded the minimum threshold of {}"
                        + " (need to defragment)", minimumInterval);
            }
            note.setPosition(newPosition);
        }
    }
}
