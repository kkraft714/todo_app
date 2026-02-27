package server.note;

import server.categories.CategoryTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This is the base class for all Note elements.
 *
 * The setter methods return the "this" object to allow for chaining.
 */
// ToDo: Needs to be mapped to DB with Hibernate annotations
public class Note extends NoteBase {
    private Boolean completed;
    protected ArrayList<NoteBase> childNotes;
    protected ArrayList<Link> links;

    public Note(String newName, String newDescription) {
        super(newName, newDescription);
        this.childNotes = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public Note(String newName) { this(newName, null); }

    // ToDo: Add addLink() and addElement() methods (or addSubNote() or addChildNote()?)
    public ArrayList<NoteBase> getChildNotes() { return childNotes; }
    public Note addChildNote(NoteBase childNote) { childNotes.add(childNote); return this; }
    public Note addChildNote(NoteBase childNote, int index) { childNotes.add(index, childNote); return this; }
    public Note removeChildNote(NoteBase childNote) { childNotes.remove(childNote); return this; }
    public Note addLink(Link link) { links.add(link); return this; }
    public boolean getCompleted() { return completed != null && completed; }
    public Note setCompleted(boolean isCompleted) { completed = isCompleted; return this; }

    private <T extends NoteBase> void findMatchingNotes(
            NoteBase note, T noteClass, Set<CategoryTag> tags, boolean allTags, List<NoteBase> results) {
        // Ignore class and search just by tag if noteClass is null
        if ((noteClass == null || noteClass.getClass().isInstance(note))
                && ((allTags && note.containsAllTags(tags) || (!allTags && note.containsAnyTags(tags))))) {
            results.add(note);
        }
        if (note instanceof Note) {
            for (NoteBase childNote : ((Note) note).childNotes) {
                findMatchingNotes(childNote, noteClass, tags, allTags, results);
            }
        }
    }

    public <T extends NoteBase> ArrayList<NoteBase> findNotes(T noteClass, Set<CategoryTag> tags, boolean allTags) {
        ArrayList<NoteBase> matchingNotes = new ArrayList<>();
        findMatchingNotes(this, noteClass, tags, allTags, matchingNotes);
        return matchingNotes;
    }

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
