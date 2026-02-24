package server.note;

// ToDo: Needs to be mapped to DB with Hibernate annotations
public class Category extends Note<Category> {
    // ToDo: Define Category types (or will just the name be sufficient)?
    //  Is it worth trying to define a taxonomy of categories? Or just let users define their own categories as needed?
    //  Define internal categories as enums that covert to String (along with user-defined categories)?
    public Category(String name, String description) {
        super(name, description);
    }
}
