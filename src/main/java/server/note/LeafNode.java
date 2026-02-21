package server.note;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This is the base class for all Note elements.
 *
 * @param <T>
 */
// ToDo: Can probably delete this class
// ToDo: Needs to be mapped to DB with Hibernate annotations
public class LeafNode<T extends LeafNode<T>> {
    // Because Hibernate uses a no-arg constructor with setters I can't make properties final
    // ToDo: See if I can make name (and other required fields) final
    private String name;
    private String description;
    // ToDo: Can completed be moved up to Note?
    private Boolean completed;
    private final LocalDateTime created;

    public LeafNode(String newName, String newDescription) {
        this.name = newName;
        this.description = newDescription;
        created = LocalDateTime.now();
    }

    public LeafNode(String newName) { this(newName, null); }

    // ToDo: Create a separate NoteBuilder class?
    public String getName() { return name; }
    public T setName(String newName) { this.name = newName; return (T)this; }
    public String getDescription() { return description; }
    public T setDescription(String newDescription) { description = newDescription; return (T)this; }
    public boolean getCompleted() { return completed != null && completed; }
    public T setCompleted(boolean isCompleted) { this.completed = isCompleted; return (T)this; }

    public LocalDateTime getDateCreated() { return created; }

    // ToDo: Add more info to this (e.g. categories, tags, elements)?
    // ToDo: Add loop over elements
    @Override
    public String toString() {
        String result = name;
        if (description != null && !description.isEmpty()) {
            result += "\n" + description;
        }
        return result;
    }
}
