package server.categories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.NoteOrganizer;

import java.util.HashSet;
import java.util.Set;

/*
** Contains user-defined categories
*/
public class UserCategories {
    private final Set<String> categories;
    private static final Logger LOG = LogManager.getLogger(UserCategories.class);

    public UserCategories() { this.categories = new HashSet<>(); }

    public void addCategory(String category) { categories.add(category); }
    public void removeCategory(String category) {
        if (!categories.remove(category)) {
            LOG.warn("Could not remove category " + category);
        }
    }
    public void addCategories(Set<String> categories) { this.categories.addAll(categories); }

    public Set<String> getCategories() { return categories; }
}
