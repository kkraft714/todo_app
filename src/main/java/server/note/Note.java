package server.note;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This is the base class for all Note elements.
 *
 * @param <T>
 *
 * The setter methods return the "this" object to allow for chaining.
 */
// ToDo: Needs to be mapped to DB with Hibernate annotations
public class Note<T extends NoteBase<T>> extends NoteBase<T> {
    private Boolean completed;
    protected ArrayList<Note<?>> elements;
    protected ArrayList<Link> links;

    public Note(String newName, String newDescription) {
        super(newName, newDescription);
        this.elements = new ArrayList<>();
        this.links = new ArrayList<>();
    }

    public Note(String newName) { this(newName, null); }

    // ToDo: Create a separate NoteBuilder class?
    public boolean getCompleted() { return completed != null && completed; }
    public T setCompleted(boolean isCompleted) { this.completed = isCompleted; return (T)this; }

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
