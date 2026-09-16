package server.note;

import server.element.*;

import java.util.ArrayList;
import java.util.List;

// Includes Person, Company etc.
public class Contact extends Entity {
    private String firstName;
    private String lastName;
    private ArrayList<Address> addresses;
    private ArrayList<Phone> phoneNumbers;

    public Contact(String firstName, String lastName, String fullName, String description, EntityType type) {
        super(fullName, description, type);
        this.firstName = firstName;
        this.lastName = lastName;
        this.addresses = new ArrayList<>();
        this.phoneNumbers = new ArrayList<>();
    }

    // ToDo: Should type be final or do we need a setter?
    public String getFirstName() { return firstName; }
    public Contact setFirstName(String firstName) { this.firstName = firstName; return this; }
    public String getLastName() { return lastName; }
    public Contact setLastName(String lastName) { this.lastName = lastName; return this; }
    public List<Address> getAddresses() { return addresses; }
    public Contact addAddress(Address newAddress) { addresses.add(newAddress); return this; }
    public List<Phone> getPhoneNumbers() { return phoneNumbers; }
    public Contact addPhoneNumber(Phone newPhone) { phoneNumbers.add(newPhone); return this; }

    public static Contact newContact(String firstName, String lastName, String description) {
        return new Contact(firstName, lastName, null, description, EntityType.PERSON);
    }

    public static Contact newBusiness(String name, String description) {
        return new Contact(null, null, name, description, EntityType.BUSINESS);
    }

    // ToDo: Update to add address and phone info
    @Override
    public String toString() {
        String result = super.toString();
        if (description != null && !description.isEmpty()) {
            result += "\n" + description;
        }
/*
        // ToDo: Update to add multiple numbers
        if (address != null) {
            result += "\n" + address.toString();
        }
*/
        return result;
    }
}
