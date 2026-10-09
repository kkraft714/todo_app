package server;

import server.note.NoteBase;

import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.Set;

// Add nameStartsWith property (and/or do actual match on nameMatch?)
// ToDo: Add option to search by date created or date modified (or date range)?
public class SearchCriteria {
    // ToDo: Add searchChildNotes property if I support it?
    private Set<String> categories;
    private Class<? extends NoteBase> noteClass;
    private String nameMatch;
    private Pattern matchPattern;
    // Currently this only applies to categories
    // Support a more lax search criteria (false/OR) by default
    boolean joinWithAnd = false;    // Whether to search using AND or OR criteria

    public SearchCriteria() { this.categories = new HashSet<>(); }

    public SearchCriteria setCategory(String category) { categories.add(category); return this; }
    public SearchCriteria setCategories(Set<String> newCategories) { categories.addAll(newCategories); return this; }
    public SearchCriteria setNoteClass(Class<? extends NoteBase> noteClass) { this.noteClass = noteClass; return this; }
    // Name match is case-insensitive
    public SearchCriteria setNameMatch(String nameMatch) {
        this.nameMatch = nameMatch;
        this.matchPattern = nameMatch == null || nameMatch.isEmpty() ?
                null : Pattern.compile(nameMatch, Pattern.CASE_INSENSITIVE);
        return this; }
    public SearchCriteria setJoinWithAnd(boolean joinWithAnd) { this.joinWithAnd = joinWithAnd; return this; }
    public SearchCriteria joinWithAnd() { this.joinWithAnd = true; return this; }

    Class<? extends NoteBase> getNoteClass() { return noteClass; }
    Set<String> getCategories() { return categories; }
    boolean hasCategories() { return categories != null && !categories.isEmpty(); }
    boolean nameMatch(String name) { return nameMatch == null || name == null || name.isEmpty() ||
            matchPattern.matcher(name).matches() || name.toLowerCase().contains(nameMatch.toLowerCase()); }

    public boolean match(NoteBase note) {
        boolean classMatch = noteClass == null || noteClass.isAssignableFrom(note.getClass());
        boolean categoryMatch = joinWithAnd ? note.hasAllCategories(categories) : note.hasAnyCategory(categories);
        return nameMatch(note.getName()) && classMatch && categoryMatch;
    }
}
