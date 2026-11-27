package server.note;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static server.NoteUtils.insertNote;

// ToDo: Should this move out of the note package under the server package?
@jakarta.persistence.Entity    // Import includes full path due to conflict with the Entity class
@Table(name = "note_user")     // "user" is a reserved word in PostgreSQL
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
    private final List<NoteBase> notes = new ArrayList<>();

    // ToDo: This is just a placeholder (need to support more fields/functionality)
    public User(String name) { this.name = name; }
    protected User() {}    // Required zero-arg constructor for Hibernate

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<NoteBase> getNotes() { return notes; }
    public void addNote(NoteBase note, int index) { insertNote(note, index, notes); }
    // ToDo: Should I add notes at the start or the end of the list by default?
    public void addNoteAtEndOfList(NoteBase note) { addNote(note, notes.size()); }

    // ToDo: Need removeNote()
}
