package server.note;

import server.element.ContactInfo;

// Includes Contact, Person, Company, Artist, etc.
public class Entity extends Note<Entity> {
    public enum EntityType { PERSON, BUSINESS, ARTIST };
    private final EntityType type;
    // ToDo: Define getter/setter for contactInfo
    private ContactInfo contactInfo;

    public Entity(String name, String description, EntityType type) {
        super(name, description);
        this.type = type;
    }

    public EntityType getType() { return type; }
}
