package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.note.NoteBase;

import java.util.List;

public class NoteUtils {
    public static final Logger LOG = LogManager.getLogger("Universal Logger");
    public static boolean isSet(String value){ return value != null && !value.isEmpty(); }

    // ToDo: Move these to a UI-specific utility class?
    public static <T extends NoteBase> void insertNote(T note, int index, List<T> notes) {
        // ToDo: Check for valid index?
        if (notes.remove(note) && index > notes.size()) { index = notes.size(); }   // Handle MOVE case
        note.setPosition(calculateNewPosition(notes, index));
        LOG.debug("Set position for Note '{}' to {}", note.getName(), note.getPosition());
        note.setParent(null);   // This is kind of a cheesy hack which must be overridden in addChildNote()
        notes.add(index, note);
    }

    private static final double positionInterval = 10000.00;
    private static final double minimumInterval = 1e-9;    // 0.000000001
    // Handles adding or moving a note
    private static <T extends NoteBase> double calculateNewPosition(List<T> notes, int index) {
        if (notes.isEmpty()) {    // This is the first note in the list
            return positionInterval;
        }
        else if (index == 0) {  // This note is at to the top of the list
            return notes.getFirst().getPosition() - positionInterval;
        }
        else if (index == notes.size()) {   // This note is at the bottom of the list
            return notes.get(index - 1).getPosition() + positionInterval;
        }
        else {    // Calculate a position between the two notes
            double positionAbove = notes.get(index - 1).getPosition();
            // If I use "index + 1" I get an IndexOutOfBoundsException due to the remove() call
            double increment = (notes.get(index).getPosition() - positionAbove)/2.0;
            if (increment < minimumInterval) {     // ToDo: Test triggering this?
                LOG.warn("Interval between note positions is less than the minimum threshold of {}"
                        + " (need to defragment)", minimumInterval);
            }
            return positionAbove + increment;
        }
    }
}
