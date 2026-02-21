package server;

import server.categories.CategoryTag;
import server.element.NoteElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// ToDo: Need this AND a NoteElementBuilder?
// ToDo: Will probably replace this with having the setters return the "this" object and delete
public class NoteBuilder<T extends NoteElement<T>> {
    private final String name;
    private String description;
    private ArrayList<NoteElement<?>> elements;
    private Set<String> categories;    // User-defined categories
    private Set<CategoryTag> tags;     // Internally defined categories

    public NoteBuilder(String name) {
        this.name = name;
    }

    public NoteBuilder<T> withDescription(String description) {
        this.description = description;
        return this;
    }

    public NoteBuilder<T> withElements(ArrayList<NoteElement<?>> elements) {
        this.elements = elements;
        return this;
    }

    public NoteBuilder<T> withTags(Set<CategoryTag> tags) {
        this.tags = tags;
        return this;
    }

    public NoteBuilder<T> withTag(CategoryTag tag) {
        if (this.tags == null) {
            this.tags = new HashSet<>();
        }
        this.tags.add(tag);
        return this;
    }

    public NoteBuilder<T> withCategories(Set<String> categories) {
        this.categories = categories;
        return this;
    }

    public NoteBuilder<T> withCategory(String category) {
        if (this.categories == null) {
            this.categories = new HashSet<>();
        }
        this.categories.add(category);
        return this;
    }

    public Note build() {
        Note newNote = new Note(name, description);
        if (elements != null) {
            elements.forEach(newNote::addElement);
        }
        if (categories != null) {
            categories.forEach(newNote::addCategory);
        }
        if (tags != null) {
            tags.forEach(newNote::addTag);
        }
        return newNote;
    }
}
