package server;

import java.util.ArrayList;
import server.note.NoteBase;
import java.util.List;

// ToDo: Add option to search by date created or date modified?
// ToDo: Add method for searching and returning matching notes
public class SearchCriteria {
    List<String> categories;
    Class<? extends NoteBase> noteClass;
    String nameMatch;
    // Currently this only applies to categories
    boolean joinWithAnd = true;     // Whether to search using AND or OR criteria
    // ToDo: Add partial match on title

    public SearchCriteria(List<String> cats, Class<? extends NoteBase> cls, boolean joinWithAnd, String name) {
        this.categories = cats != null ? new ArrayList<>(cats) : new ArrayList<>();
        this.noteClass = cls;
        this.joinWithAnd = joinWithAnd;
        this.nameMatch = name;
    }

    // ToDo: Pre-filter class type using noteTypes and schedule properties from main?
    //  Can remove the noteClass property if I'm always passing null
    public boolean match(NoteBase note) {
        boolean nameMatches = nameMatch == null || note.getName().toLowerCase().contains(nameMatch.toLowerCase());
        boolean classMatch = noteClass == null || note.getClass().isInstance(noteClass);
        boolean categoryMatch = joinWithAnd ? note.hasAllCategories(new java.util.HashSet<>(categories))
                                              : note.hasAnyCategory(new java.util.HashSet<>(categories));
        return nameMatches && classMatch && categoryMatch;
    }

    // ToDo: Probably don't need these because we will just initialize SearchCriteria once
    public SearchCriteria addCategories(List<String> newCategories) { categories.addAll(newCategories); return this; }
    public SearchCriteria setNoteClass(Class<? extends server.note.NoteBase> noteClass) {
        this.noteClass = noteClass;
        return this;
    }
}
