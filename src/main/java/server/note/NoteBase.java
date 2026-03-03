package server.note;

import server.categories.CategoryTag;

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
    // ToDo: Should a child Note inherit its parent's categories and tags?
    protected Set<String> categories;    // User-defined categories
    protected Set<CategoryTag> tags;     // Internally defined categories
    private final LocalDateTime created;

    // ToDo: Make name required but editable (i.e. not final)
    public NoteBase(String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
        this.categories = new HashSet<>();
        this.tags = new HashSet<>();
        this.created = LocalDateTime.now();
    }

    public String getName() { return name; }
    public NoteBase setName(String newName) { this.name = newName; return this; }
    public String getDescription() { return description; }
    public NoteBase setDescription(String newDescription) { description = newDescription; return this; }
    // ToDo: Add Javadoc
    public Set<String> getCategories() { return categories; }
    public NoteBase addCategory(String category) { categories.add(category); return this; }
    // ToDo: Add removeCategory()?
    public NoteBase addCategories(Set<String> cats) { categories.addAll(cats); return this; }
    public Set<CategoryTag> getTags() { return tags; }
    public NoteBase addTag(CategoryTag newTag) { this.tags.add(newTag); return this; }
    // ToDo: Support adding multiple tags and removing tags?
    public LocalDateTime getDateCreated() { return created; }

    // ToDo: Need Javadoc!
    public boolean containsAllTags(Set<CategoryTag> tagList) { return tags.containsAll(tagList); }
    public boolean containsAnyTags(Set<CategoryTag> tagList) {
        return tagList.stream().anyMatch(tag -> tags.contains(tag));
    }
    // ToDo: Support toString() method
}
