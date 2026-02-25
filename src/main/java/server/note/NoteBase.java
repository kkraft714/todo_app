package server.note;

import java.time.LocalDateTime;

/**
 * This is the base class for all Note objects.
 *
 * @param <T>
 */
// This can be used for leaf-type notes with no child elements
public class NoteBase<T extends NoteBase<T>> {
    protected String name;
    protected String description;
    private final LocalDateTime created;

    public NoteBase(String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
        created = LocalDateTime.now();
    }

    // ToDo: Update the spec doc to note that I am overloading the setter methods as builders
    public String getName() { return name; }
    public NoteBase<T> setName(String newName) { this.name = newName; return this; }
    public String getDescription() { return description; }
    public NoteBase<T> setDescription(String newDescription) { description = newDescription; return this; }
    public LocalDateTime getDateCreated() { return created; }
}
