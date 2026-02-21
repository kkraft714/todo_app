package server.note;

import java.time.LocalDateTime;

/**
 * This is the base class for all Note object.
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

    public String getName() { return name; }
    public T setName(String newName) { this.name = newName; return (T)this; }
    public String getDescription() { return description; }
    public T setDescription(String newDescription) { description = newDescription; return (T)this; }
    public LocalDateTime getDateCreated() { return created; }
}
