package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.categories.*;
import server.note.NoteBase;

import java.util.*;

// ToDo: Add Javadoc (this is the main program / entry point)!
//   I think categories might be for the user to define custom categories
//   while tags are pre-defined by the program?
// ToDo: Add DEBUG logging (for exception context)
// ToDo: Can (or should) this class be a static singleton (how would that work with Hibernate)?
public class NoteOrganizer {
    // Note: using List because Set isn't ordered
    protected List<NoteBase> notes;
    // ToDo: Do I need categories?
    protected Map<String, List<NoteBase>> categories;
    private static final Logger LOG = LogManager.getLogger(NoteOrganizer.class);

    public NoteOrganizer() {
        notes = new ArrayList<>();
        categories = new HashMap<>();
    }

    public NoteBase getNote(int index) {
        checkForValidNoteIndex(index);
        return notes.get(index);
    }

    // ToDo: Move all search/locator and Note tracking code here?
/*
    private void updateElementLocatorAfterAdd(NoteElement<?> element, int index) {
        if (!elementLocator.containsKey(element.getClass())) {
            elementLocator.put(element.getClass(), new ArrayList<>());
        }
        elementLocator.get(element.getClass()).add(index);
    }

    // ToDo: Update the element locator logic?
    private void updateElementLocatorAfterDelete(NoteElement<?> element) {
        if (elementLocator.containsKey(element.getClass())) {
            // ToDo: Why am I removing the whole look-up list?
            elementLocator.remove(element.getClass());
        }
        else {
            LOG.warn("Element locator for Note '" + element.getName() + "' contains no element of type "
                    + element.getClass().getSimpleName());
        }
    }
*/

    public List<NoteBase> getNotes() { return notes; }

    // ToDo: Rename to getNotesInCategory(), getNotesForCategory(), getNotesFromCategory()?
    public List<NoteBase> getCategory(String name) {
        checkForValidCategory(name);
        return categories.get(name);
    }

    public Set<String> getCategories() { return categories.keySet(); }

    public void addNote(NoteBase newNote) { notes.add(newNote); }
    public void addNote(NoteBase newNote, int index) {

    }

    public void addCategory(String name) { categories.put(name, new ArrayList<>()); }
    public void addNoteToCategory(String name, NoteBase newNote) {
        // ToDo: If I add the category check 5 tests fail
        // checkForValidCategory(name);
        addNotesToCategory(name, List.of(newNote));
    }
    public void addNoteToCategory(String name, NoteBase newNote, int position) {
        addNotesToCategory(name, List.of(newNote), position);
    }

    // ToDo: Change this to use CategoryTag?
    public void addNoteToCategories(Set<String> categories, NoteBase newNote) {
        categories.forEach(cat -> addNoteToCategory(cat, newNote));
        // newNote.addCategories(categories);
    }

    // Add Notes at the front by default
    public void addNotesToCategory(String name, List<NoteBase> notes) {
        addNotesToCategory(name, notes, 0);
    }
    
    public void addNotesToCategory(String name, List<NoteBase> notes, int position) {
        if (!categories.containsKey(name)) {
            addCategory(name);
        }
        if (position > categories.get(name).size()) {
            throw new RuntimeException("Invalid index (" + position + ") for " + name
                    + " category (size " + categories.get(name).size() + ")");
        }
        categories.get(name).addAll(position, notes);
        notes.forEach(n -> n.addCategory(name));
    }

    public NoteBase deleteNote(int index) {
        checkForValidNoteIndex(index);
        removeNoteFromAllCategories(notes.get(index));
        return notes.remove(index);
    }
    // ToDo: Will this work for notes that have been retrieved from the DB?
    public NoteBase deleteNote(NoteBase n) {
        checkForValidNoteObject(n);
        removeNoteFromAllCategories(n);
        notes.remove(n);
        return n;
    }

    // Used when deleting a note
    // ToDo: Change this to use CategoryTag?
    private void removeNoteFromAllCategories(NoteBase n) {
        n.getCategories().forEach(cat -> removeNoteFromCategory(cat, n));
    }

    public void deleteCategory(String name) {
        checkForValidCategory(name);
        // ToDo: Only allow this if category is empty (doesn't contain any notes)?
        //  Or alternately remove all notes from that category?
        categories.remove(name);
    }

    // ToDo: Add version that takes a note index? Also return the note?
    public void removeNoteFromCategory(String name, NoteBase n) {
        checkForValidCategory(name);
        if (!categories.get(name).contains(n)) {
            throw new RuntimeException("Unable to locate note '" + n.getName() + "' in category '" + name + "'");
        }
        System.out.println("Removing note '" + n.getName() + "' from category '" + name + "'");
        categories.get(name).remove(n);
    }

    // ToDo: Define a NoteSearch object (with root Note, lists of categories and tags, and search type (e.g. all vs. any))?
    public List<NoteBase> getNotesWithTag(CategoryTag tag) { return getNotesWithTag(tag, notes); }
    public List<NoteBase> getNotesWithTag(CategoryTag tag, List<NoteBase> list) {
        return getNotesWithTags(Set.of(tag), list, true);    // ToDo: Use List.of() here?
    }
    public List<NoteBase> getNotesWithAllTags(Set<CategoryTag> tags) { return getNotesWithAllTags(tags, notes); }
    public List<NoteBase> getNotesWithAllTags(Set<CategoryTag> tags, List<NoteBase> list) {
        return getNotesWithTags(tags, list, true);
    }
    public List<NoteBase> getNotesWithAnyTags(Set<CategoryTag> tags) { return getNotesWithAnyTags(tags, notes); }
    public List<NoteBase> getNotesWithAnyTags(Set<CategoryTag> tags, List<NoteBase> list) {
        return getNotesWithTags(tags, list, false);
    }
    // ToDo: Document meaning of allTags (i.e. note must have ALL tags vs. note can have ANY tag)
    // ToDo: Pass in rootNote?
    private List<NoteBase> getNotesWithTags(Set<CategoryTag> tags, List<NoteBase> list, boolean allTags) {
        List<NoteBase> notesWithTags = new ArrayList<>();
        for (NoteBase n : list) {
            if ((allTags && n.containsAllTags(tags)) || (!allTags && n.containsAnyTags(tags))) {
                notesWithTags.add(n);
            }
        }
        return notesWithTags;
    }

    public void initialize() {
        // ToDo: Set up standard Note categories (e.g. scheduled, purchase, etc.)
    }

    private void checkForValidNoteIndex(int index) {
        if (index >= notes.size()) {
            throw new RuntimeException("Invalid index (" + index + ") for note list (size " + notes.size() + ")");
        }
    }

    private void checkForValidNoteObject(NoteBase n) {
        if (!notes.contains(n)) {
            throw new RuntimeException("Failed to locate note: '" + n.getName() + "'");
        }
    }

    private void checkForValidCategory(String name) {
        if (!categories.containsKey(name)) {
            throw new RuntimeException("Unable to locate category '" + name + "'");
        }
    }
}
