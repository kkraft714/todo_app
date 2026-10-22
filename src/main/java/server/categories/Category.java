package server.categories;

import jakarta.persistence.Embeddable;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.Objects;

@Embeddable
public class Category {
    // Note that name is required to be unique, since it is used to define equals() and hashCode().
    // That requirement will have to be enforced when adding categories in NoteOrganizer.
    @NotBlank
    private String name;

    protected Category() {}  // Required zero-arg constructor for Hibernate

    // Factory method for user-defined string categories
    // ToDo: Add tests for both kinds of category
    public static Category of(String customName) {
        Category cat = new Category();
        cat.name = customName;
        return cat;
    }

    // Factory method for your internal typed enums
    public static Category of(InternalCategory type) { return of(type.getName()); }

    public String getName() {
        return name;
    }

    // A clean helper to check if this category matches an internal enum type
    public boolean validCategory(Enum<?> internalType) {
        return internalType != null && internalType.name().equalsIgnoreCase(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        return Objects.equals(name, ((Category)o).name);
    }

    @Override
    public int hashCode() { return Objects.hash(name); }
}
