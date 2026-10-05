package server.note;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the base class for all Note objects.
 */
// This can be used for leaf-type notes with no child elements
public class NoteBase {
    protected String name;
    protected String description;
    // ToDo: Should a child Note inherit its parent's categories (maybe conceptually but not literally)?
    // ToDo: Do I really need to track categories here?
    protected Set<String> categories;
    private final LocalDateTime created;

    public NoteBase(String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
        this.categories = new HashSet<>();
        this.created = LocalDateTime.now();
    }

    public String getName() { return name; }
    public NoteBase setName(String newName) { this.name = newName; return this; }
    public String getDescription() { return description; }
    public NoteBase setDescription(String newDescription) { description = newDescription; return this; }
    // ToDo: Add Javadoc
    public Set<String> getCategories() { return categories; }
    public NoteBase addCategory(String category) { categories.add(category); return this; }
    public NoteBase removeCategory(String cat) { categories.remove(cat); return this; }
    public NoteBase addCategories(Set<String> cats) { categories.addAll(cats); return this; }
    public LocalDateTime getDateCreated() { return created; }

    public boolean hasCategory(String category) { return categories.contains(category); }
    // ToDo: Need Javadoc (e.g. no categories automatically returns true)
    public boolean hasAllCategories(Set<String> cats) { return cats == null || categories.containsAll(cats); }
    public boolean hasAnyCategory(Set<String> cats) {
        return cats == null || cats.isEmpty() || cats.stream().anyMatch(cat -> categories.contains(cat));
    }
    // ToDo: Support toString() method
}
