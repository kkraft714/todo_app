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
    public void addNote(NoteBase note, int index) {
        // ToDo: Check for valid index?
        if (notes.remove(note) && index > notes.size()) { index = notes.size(); }   // Handle MOVE case
        note.setPosition(calculateNewPosition(index));
        LOG.debug("Set position for Note '{}' to {}", note.getName(), note.getPosition());
        notes.add(index, note);
    }
    // ToDo: Should I add notes at the start or the end of the list by default?
    public void addNoteAtEndOfList(NoteBase note) { addNote(note, notes.size()); }

    // ToDo: Need removeNote()

    private static final double positionInterval = 10000.00;
    private static final double minimumInterval = 1e-9;    // 0.000000001
    // Handles adding or moving a note
    private double calculateNewPosition(int index) {
        if (notes.isEmpty()) {    // This is the first note in the list
            return positionInterval;
        }
        else if (index == 0) {  // This note is at to the top of the list
            return notes.getFirst().getPosition() - positionInterval;
        }
        else if (index == notes.size()) {   // This note is at the bottom of the list
            return notes.get(index - 1).getPosition() + positionInterval;
        }
        else {    // Calculate a position between the two notes
            double positionAbove = notes.get(index - 1).getPosition();
            // If I use "index + 1" I get an IndexOutOfBoundsException due to the remove() call
            double increment = (notes.get(index).getPosition() - positionAbove)/2.0;
            if (increment < minimumInterval) {     // ToDo: Test triggering this?
                LOG.warn("Interval between note positions is less than the minimum threshold of {}"
                        + " (need to defragment)", minimumInterval);
            }
            return positionAbove + increment;
        }
    }
}
