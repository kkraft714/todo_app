package server.categories;

import java.util.*;
import java.util.stream.Collectors;

// ToDo: Implement extensible enums like at Shutterfly (do I really need this)?
//  * Define a category class with data fields (name, other?) and getters/setters
//  * Define an interface with a get() method for returning a data class object
//  * Define separate classes for internal and external (user-defined) categories?
//  * Currently user categories are just a Set<String> field in NoteBase
// Interface for enum category tags
public interface CategoryTag {
    Random rand = new Random();

    // Used for testing
    default CategoryTag getRandomTag() {
        int itemNumber = rand.nextInt(getCategories().size());
        return getCategories().stream().skip(itemNumber).findFirst().orElse(null);
    }
    default Set<String> convertToStrings() {
        return getCategories().stream().map(CategoryTag::toString).collect(Collectors.toSet());
    };
    Set<CategoryTag> getCategories();
}
