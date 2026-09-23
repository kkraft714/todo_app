package server.categories;

import java.util.*;

// ToDo: Implement extensible enums like at Shutterfly
//  * Define a category class with data fields (name, other?) and getters/setters
//  * Define an interface with a get() method for returning a data class object
//  * Define separate classes for internal and external (user-defined) categories?
//  * Currently user categories are just a Set<String> field in NoteBase
// Interface for enum category tags
public interface CategoryTag {
    Random rand = new Random();

    // Used for testing
    default CategoryTag getRandomTag() {
        return getCategories().get(rand.nextInt(getCategories().size()));
    }
    List<CategoryTag> getCategories();
}
