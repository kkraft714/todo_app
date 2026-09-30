package server;

import server.categories.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    @Test
    public void testConvertToStringsFromMediaType() {
        Set<String> mediaTypes = MediaType.get().convertToStrings();
        for (InternalCategory mediaType : MediaType.values()) {
            assertTrue(mediaTypes.contains(mediaType.toString()),
                    "Set of media types " + mediaType + " should contain '" + mediaType + "'");
        }
    }

    static final int numberOfTries = 10;
    @Test
    public void testGetRandomTagFromMediaType() {
        for (int i = 0; i < numberOfTries; i++) {
            InternalCategory randomTag = MediaType.get().getRandomTag();
            assertNotNull(randomTag, "Random tag should not be null");
            assertTrue(MediaType.get().getCategories().contains(randomTag), "Attempt #" + (i + 1) + ": Media types "
                    + MediaType.get().getCategories() + " should contain tag " + randomTag);
        }
    }
}
