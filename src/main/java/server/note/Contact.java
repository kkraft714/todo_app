package server.note;

import server.element.*;

import java.util.ArrayList;
import java.util.List;

// Includes Person, Company etc.

// ToDo: Add list of email addresses!
public class Contact extends Entity {
    private String firstName;
    private String lastName;
    private ArrayList<Address> addresses;
    private ArrayList<Phone> phoneNumbers;

    public Contact(String displayName, EntityType type) {
        super(displayName, null, type);
        this.addresses = new ArrayList<>();
        this.phoneNumbers = new ArrayList<>();
    }
    // ToDo: Replace this with builder pattern?
    public Contact(String firstName, String lastName, String fullName, String description, EntityType type) {
        // ToDo: The fullName kludge is kind of ugly - can I clean it up?
        super(setFullName(fullName, firstName, lastName), description, type);
        this.firstName = firstName;
        this.lastName = lastName;
        this.addresses = new ArrayList<>();
        this.phoneNumbers = new ArrayList<>();
    }

    // ToDo: Change to setDisplayName()?
    private static String setFullName(String full, String first, String last) {
        if ((first == null || first.isEmpty()) && (last == null || last.isEmpty()) && (full == null || full.isEmpty())) {
            throw new IllegalArgumentException("At least one name is required for a contact");
        }
        return full == null || full.isEmpty() ? first + " " + last : full;
    }

    // ToDo: Should type be final or do we need a setter?
    public String getFirstName() { return firstName; }
    public Contact setFirstName(String firstName) { this.firstName = firstName; return this; }
    public String getLastName() { return lastName; }
    public Contact setLastName(String lastName) { this.lastName = lastName; return this; }
    public Contact setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }
    public List<Address> getAddresses() { return addresses; }
    public Contact addAddress(Address newAddress) { addresses.add(newAddress); return this; }
    public List<Phone> getPhoneNumbers() { return phoneNumbers; }
    public Contact addPhoneNumber(Phone newPhone) { phoneNumbers.add(newPhone); return this; }

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
