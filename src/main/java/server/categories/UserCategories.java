package server.categories;

import java.util.ArrayList;
import java.util.List;

/*
** Contains user-defined categories
*/
public class UserCategories implements CategoryTag {
    private final List<CategoryTag> categories;

    public UserCategories() { this.categories = new ArrayList<>(); }

    public void addCategory(CategoryTag category) { categories.add(category); }
    public void removeCategory(CategoryTag category) { categories.remove(category); }
    public void addCategories(List<CategoryTag> categories) { this.categories.addAll(categories); }

    @Override
    public List<CategoryTag> getCategories() { return categories; }
}
