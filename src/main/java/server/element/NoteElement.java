package server.element;

import java.time.LocalDateTime;

public class NoteElement<T extends NoteElement<T>> {
    // Because Hibernate uses a no-arg constructor with setters I can't make properties final
    private String name;
    private String description;
    private final LocalDateTime created;

    public NoteElement(String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
        created = LocalDateTime.now();
    }

    public NoteElement(String newName) { this(newName, null); }

    // ToDo: Create a separate NoteBuilder class?
    public String getName() { return name; }
    public T setName(String newName) { this.name = newName; return (T)this; }
    public String getDescription() { return description; }
    public T setDescription(String newDescription) { description = newDescription; return (T)this; }

    public LocalDateTime getDateCreated() { return created; }

    @Override
    public String toString() {
        String result = name;
        if (description != null && !description.isEmpty()) {
            result += "\n" + description;
        }
        return result;
    }
}
