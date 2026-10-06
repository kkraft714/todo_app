package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class TestBase {
    protected static NoteOrganizer main;
    protected static final String defaultCategoryName = "NewCategory";

    @BeforeEach
    public void initialize() { main = new NoteOrganizer(); }

    @AfterEach
    public void testCleanup() { NoteTestHelper.resetNoteNumber(); }
}
