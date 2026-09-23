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
    protected ArrayList<NoteBase> childNotes;   // ToDo: Rename "children"?
    protected ArrayList<Link> links;

    public Note(String newName, String newDescription) {
        super(newName, newDescription);
        this.childNotes = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public Note(String newName) { this(newName, null); }

    // ToDo: Add addLink() and addElement() methods (or addSubNote() or addChildNote()?)
    public ArrayList<NoteBase> getChildNotes() { return childNotes; }
    public NoteBase getChildNote(int index) { return childNotes.get(index); }
    public Note addChildNote(NoteBase childNote) { childNotes.add(childNote); return this; }
    public Note addChildNote(NoteBase childNote, int index) { childNotes.add(index, childNote); return this; }
    public Note removeChildNote(NoteBase childNote) { childNotes.remove(childNote); return this; }
    public Note addLink(Link link) { links.add(link); return this; }
    public boolean getCompleted() { return completed != null && completed; }
    public Note setCompleted(boolean isCompleted) { completed = isCompleted; return this; }

    // ToDo: Make SearchCriteria an inner class and add a version of findMatchingNotes() that takes a SearchCriteria object
    private <T extends NoteBase> void findMatchingNotes(
            NoteBase note, String nameMatch, T noteClass, Set<CategoryTag> tags, Set<String> categories,
            boolean joinWithAnd, List<NoteBase> results) {
        // Ignore class and search just by tag if noteClass is null
        // ToDo: Almost need different methods for AND-search and OR-search
        if ((noteClass == null || note.getClass().isInstance(noteClass)
                || (nameMatch == null || note.getName().toLowerCase().contains(nameMatch.toLowerCase())))
                && ((joinWithAnd && note.containsAllTags(tags) || (!joinWithAnd && note.containsAnyTags(tags))))
                && (categories == null || note.getCategories().containsAll(categories))) {
            results.add(note);
        }
        if (note instanceof Note) {
            for (NoteBase childNote : ((Note) note).childNotes) {
                findMatchingNotes(childNote, nameMatch, noteClass, tags, categories, joinWithAnd, results);
            }
        }
    }
    // ToDo: Test cases:
    //  * Search by name only
    //  * Search by class only
    //  * Search by one or multiple tags only (with both allTags true and false)
    //  * Search by name and class
    //  * Search by name and tags
    public <T extends NoteBase> ArrayList<NoteBase> findNotes(T noteClass, String nameMatch, Set<CategoryTag> tags,
            Set<String> categories, boolean joinWithAnd) {
        ArrayList<NoteBase> matchingNotes = new ArrayList<>();
        findMatchingNotes(this, nameMatch, noteClass, tags, categories, joinWithAnd, matchingNotes);
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
