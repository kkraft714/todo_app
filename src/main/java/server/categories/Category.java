package server.categories;

import jakarta.persistence.Embeddable;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.util.Objects;

@Embeddable
public class Category {
    @NotBlank
    private String name;

    protected Category() {}  // Required zero-arg constructor for Hibernate

    // Factory method for user-defined string categories
    public static Category of(String customName) {
        Category cat = new Category();
        cat.name = customName;
        return cat;
    }

    // Factory method for your internal typed enums
    public static Category of(InternalCategory type) {
        Category cat = new Category();
        cat.name = type.getName(); // Converts internal type to clean String
        return cat;
    }

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
