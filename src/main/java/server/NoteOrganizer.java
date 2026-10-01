package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.categories.*;
import server.note.NoteBase;

import java.util.*;

// ToDo: Track categories and schedule items in this class (will need to be stored in DB)?
//  Need to add some kind of calendar property?
//  Also track contacts (people and businesses)?
// ToDo: Can just load all Notes from the DB each time (because presumably it's a small app)?
//  Have a mechanism to complete/deactivate notes and don't load them by default
// ToDo: Add Javadoc (this is the main program / entry point)!
//   E.g. user-defined versus internal categories
// ToDo: Add DEBUG logging (for exception context)
// ToDo: Can (or should) this class be a static singleton (how would that work with Hibernate)?
public class NoteOrganizer {
    // Note: using List because Set isn't ordered
    // ToDo: Support one map (or other data structure) for each view type (category, schedule item, contact, etc.)?
    protected List<NoteBase> notes;   // This structure defines the note tree
    protected Map<String, Set<NoteBase>> categories;
    // ToDo: Create one universal logger for the whole app?
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
    // ToDo: I think I can remove this element locator code (and add search methods using the Note search code)
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
    public Set<String> getCategories() { return categories.keySet(); }
    public Set<NoteBase> getNotesForCategory(String name) {
        return categories.get(name) != null ? categories.get(name) : new HashSet<>();
    }

    public void addNote(NoteBase newNote) {
        notes.add(newNote);
        addNoteToCategories(newNote);
    }

    public void addNotes(List<NoteBase> newNotes) {
        newNotes.forEach(this::addNote);
    }

    // ToDo: If I implement this I also need to update categories
    public void addNote(NoteBase newNote, int index) { }

    public void addCategory(String name) { categories.put(name, new HashSet<>()); }

    public void addNoteToCategory(String name, NoteBase newNote) {
        newNote.addCategory(name);
        if (!categories.containsKey(name)) {
            addCategory(name);
        }
        categories.get(name).add(newNote);
    }

    public void addNoteToCategories(NoteBase newNote) {
        newNote.getCategories().forEach(cat -> addNoteToCategory(cat, newNote));
    }

    public void addNoteToCategories(Set<String> categories, NoteBase newNote) {
        categories.forEach(cat -> addNoteToCategory(cat, newNote));
    }

    public void addNotesToCategory(String name, List<NoteBase> notes) {
        notes.forEach(nb -> addNoteToCategory(name, nb));
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
    private void removeNoteFromAllCategories(NoteBase n) {
        n.getCategories().forEach(cat -> removeNoteFromCategory(cat, n));
    }

    public void deleteCategory(String name) {
        checkForValidCategory(name);
        // ToDo: Only allow this if category is empty (doesn't contain any notes)?
        if (categories.containsKey(name)) {
            LOG.warn("Deleting category '{}' will remove it from all notes", name);
        }
        categories.get(name).forEach(n -> n.getCategories().remove(name));
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
    public Set<NoteBase> getNotesWithAllCategories(Set<String> cats) { return getNotesForCategories(cats, true); }
    // ToDo: Test this on a list of sub-notes in the tree
    public Set<NoteBase> getNotesWithAllCategories(Set<String> cats, List<NoteBase> list) {
        return getNotesForCategories(cats, list, true);
    }
    public Set<NoteBase> getNotesWithAnyCategories(Set<String> cats) { return getNotesForCategories(cats, false); }
    // ToDo: Test this on a list of sub-notes in the tree
    public Set<NoteBase> getNotesWithAnyCategories(Set<String> cats, List<NoteBase> list) {
        return getNotesForCategories(cats, list, false);
    }

    // ToDo: Document meaning of allCategories (i.e. note must have ALL categories vs. note can have ANY category)
    private Set<NoteBase> getNotesForCategories(Set<String> cats, boolean allCategories) {
        return getNotesForCategories(cats, notes, allCategories);
    }

    private Set<NoteBase> getNotesForCategories(Set<String> cats, List<NoteBase> list, boolean allCategories) {
        Set<NoteBase> notesForCategories = new HashSet<>();
        for (NoteBase n : list) {
            if ((allCategories && n.hasAllCategories(cats)) || (!allCategories && n.hasAnyCategory(cats))) {
                notesForCategories.add(n);
            }
        }
        return notesForCategories;
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

    // ToDo: Can I remove this (if the category doesn't exist we just create it)?
    //  But may still be useful when deleting categories
    private void checkForValidCategory(String name) {
        if (!categories.containsKey(name)) {
            throw new RuntimeException("Unable to locate category '" + name + "'");
        }
    }
}
