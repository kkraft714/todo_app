package server.note;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base class for all Note elements.
 *
 * The setter methods return the "this" object to allow for chaining.
 */
@jakarta.persistence.Entity
@DiscriminatorValue("NOTE")
public class Note extends NoteBase {
    // Maps the relationship using the 'parent' field declared in NoteBase
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<NoteBase> childNotes = new ArrayList<>();;   // ToDo: Rename "children"?
    @ElementCollection(targetClass = Link.class)
    @CollectionTable(name = "note_links", joinColumns = @JoinColumn(name = "note_id"))
    protected List<Link> links = new ArrayList<>();;

    public Note(String newName, String newDescription) {
        super(newName, newDescription);
    }

    public Note(String newName) { this(newName, null); }

    // ToDo: Add addLink() and addElement() methods (or addSubNote() or addChildNote()?)
    public List<NoteBase> getChildNotes() { return childNotes; }
    public NoteBase getChildNote(int index) { return childNotes.get(index); }
    // We call these from wrapper methods in NoteOrganizer in order to update note tracking
    public Note addChildNote(NoteBase childNote) { childNotes.add(childNote); return this; }
    public Note addChildNote(NoteBase childNote, int index) { childNotes.add(index, childNote); return this; }
    public Note removeChildNote(NoteBase childNote) { childNotes.remove(childNote); return this; }
    public Note addLink(Link link) { links.add(link); return this; }
    public boolean hasChildNotes() { return !childNotes.isEmpty(); }

    // ToDo: Add more info to this (e.g. categories, tags, elements)?
    // ToDo: Add loop over elements
    @Override
    public String toString() {
        String result = name;
        if (description != null && !description.isEmpty()) {
            result += "\n" + description;
        }
        return result;
    }
}
