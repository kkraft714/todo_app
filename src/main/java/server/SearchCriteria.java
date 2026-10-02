package server;

import server.categories.InternalCategory;

import java.util.ArrayList;
import java.util.List;

// ToDo: Do we need this (seems like it's covered by the findNotes() method in Note)?
public class SearchCriteria {
    // ToDo: Combine tags and categories (why do I need two separate things)?
    List<InternalCategory> tags;
    List<String> categories;
    Class<? extends server.note.NoteBase> noteClass;
    boolean joinWithAnd = true;     // Whether to search using AND or OR criteria

    public SearchCriteria() {
        this.tags = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public SearchCriteria addTags(List<InternalCategory> newTags) { tags.addAll(newTags); return this; }
    public SearchCriteria addCategories(List<String> newCategories) { categories.addAll(newCategories); return this; }
    public SearchCriteria setNoteClass(Class<? extends server.note.NoteBase> noteClass) {
        this.noteClass = noteClass;
        return this;
    }

    // ToDo: Add option to search by partial name (or description)?
    // ToDo: Add option to search by date created or date modified?
    // ToDo: Add method for searching and returning matching notes
}
