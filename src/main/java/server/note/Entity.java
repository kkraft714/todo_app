package server.note;

// Includes Contact, Person, Company, Artist, etc.
public class Entity extends Note {
    public enum EntityType { PERSON, BUSINESS, ARTIST };
    private final EntityType type;
    // ToDo: Define getter/setter for contactInfo
    private Address address;

    public Entity(String name, String description, EntityType type) {
        super(name, description);
        this.type = type;
    }

    public static Entity newContact(String name, String description) {
        return new Entity(name, description, EntityType.PERSON);
    }

    public static Entity newBusiness(String name, String description) {
        return new Entity(name, description, EntityType.BUSINESS);
    }

    public static Entity newArtist(String name, String description) {
        return new Entity(name, description, EntityType.ARTIST);
    }

    public EntityType getType() { return type; }
    public Address getAddress() { return address; }
    public Entity setAddress(Address newAddress) { this.address = newAddress; return this; }
    public Address newAddress() { this.address = new Address(); return this.address; }

    @Override
    public String toString() {
        String result = type.toString() + ": " + name;
        if (description != null && !description.isEmpty()) {
            result += "\n" + description;
        }
        if (address != null) {
            result += "\n" + address.toString();
        }
        return result;
    }
}
