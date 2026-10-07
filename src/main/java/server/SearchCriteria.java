package server;

import server.note.NoteBase;

import java.util.HashSet;
import java.util.Set;

// ToDo: Add option to search by date created or date modified (or date range)?
public class SearchCriteria {
    // ToDo: Create getters for these and make them final?
    // ToDo: Add searchChildNotes property?
    Set<String> categories;
    Class<? extends NoteBase> noteClass;
    String nameMatch;
    // Currently this only applies to categories
    boolean joinWithAnd = true;     // Whether to search using AND or OR criteria

    public SearchCriteria(Set<String> cats, Class<? extends NoteBase> cls, boolean joinWithAnd, String name) {
        this.categories = cats != null ? new HashSet<>(cats) : new HashSet<>();
        this.noteClass = cls;
        this.joinWithAnd = joinWithAnd;
        this.nameMatch = name;
    }

    // ToDo: Pre-filter class type using noteTypes and schedule properties from main?
    //  Can remove the noteClass property if I'm always passing null
    public boolean match(NoteBase note) {
        boolean nameMatches = nameMatch == null || note.getName().toLowerCase().contains(nameMatch.toLowerCase());
        boolean classMatch = noteClass == null || noteClass.isAssignableFrom(note.getClass());
        boolean categoryMatch = joinWithAnd ? note.hasAllCategories(categories) : note.hasAnyCategory(categories);
        return nameMatches && classMatch && categoryMatch;
    }

    boolean hasCategories() { return categories != null && !categories.isEmpty(); }

    // ToDo: Probably don't need these because we will just initialize SearchCriteria once
    public SearchCriteria addCategories(Set<String> newCategories) { categories.addAll(newCategories); return this; }
    public SearchCriteria setNoteClass(Class<? extends server.note.NoteBase> noteClass) {
        this.noteClass = noteClass;
        return this;
    }
}
