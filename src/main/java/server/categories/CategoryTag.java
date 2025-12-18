package server.categories;

import java.util.*;

// Interface for enum category tags
public interface CategoryTag {
    static final Random rand = new Random();

    // Used for testing
    default CategoryTag getRandomTag() {
        return getTagValues().get(rand.nextInt(getTagValues().size()));
    }

    abstract public List<CategoryTag> getTagValues();
}
