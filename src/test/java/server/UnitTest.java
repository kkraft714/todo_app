package server;

import server.categories.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    @Test
    public void testConvertToStringsFromMediaType() {
        Set<String> mediaTypes = MediaType.get().convertToStrings();
        for (CategoryTag mediaType : MediaType.values()) {
            assertTrue(mediaTypes.contains(mediaType.toString()),
                    "Set of media types " + mediaType + " should contain '" + mediaType + "'");
        }
    }

    static final int numberOfTries = 10;
    @Test
    public void testGetRandomTagFromMediaType() {
        for (int i = 0; i < numberOfTries; i++) {
            CategoryTag randomTag = MediaType.get().getRandomTag();
            assertNotNull(randomTag, "Random tag should not be null");
            assertTrue(MediaType.get().getCategories().contains(randomTag), "Attempt #" + (i + 1) + ": Media types "
                    + MediaType.get().getCategories() + " should contain tag " + randomTag);
        }
    }

    static final Set<String> newCategories = Set.of("Category1", "Category2", "Category3");
    @Test
    public void testAddCategoryFromUserCategories() {
        UserCategories categories = new UserCategories();
        for (String category : newCategories) {
            categories.addCategory(category);
        }
        Set<String> userCategories = categories.getCategories();
        assertEquals(newCategories.size(), userCategories.size(),
                "User categories should contain " + newCategories.size() + " new categories");
        for (String category : newCategories) {
            assertTrue(userCategories.contains(category), "User categories should contain '" + category + "'");
        }
    }

    @Test
    public void testAddCategoriesFromUserCategories() {
        UserCategories categories = new UserCategories();
        categories.addCategories(newCategories);
        Set<String> userCategories = categories.getCategories();
        assertEquals(newCategories.size(), userCategories.size(),
                "User categories should contain " + newCategories.size() + " new categories");
        for (String category : newCategories) {
            assertTrue(userCategories.contains(category), "User categories should contain '" + category + "'");
        }
    }

    @Test
    public void testRemoveCategoriesFromUserCategories() {
        UserCategories categories = new UserCategories();
        categories.addCategories(newCategories);
        int expectedSize = newCategories.size();
        for (String category : newCategories) {
            assertEquals(expectedSize, categories.getCategories().size(), "Number of user categories before removal");
            categories.removeCategory(category);
            expectedSize--;
        }
        // Set<String> userCategories = categories.getCategories();
        assertEquals(expectedSize, categories.getCategories().size(),
                "User categories should be empty after removing all categories");
    }
}
