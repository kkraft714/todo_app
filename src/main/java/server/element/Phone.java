package server.element;

import jakarta.persistence.Embeddable;
import java.util.Objects;

// ToDo Resurrect ContactInfo and add email?
@Embeddable
public class Phone {
    // ToDo: Support String customPhoneType for user-defined phone types?
    public enum PhoneType { MOBILE, HOME, WORK, OTHER };
    private String number;
    private PhoneType type;

    public Phone() {}   // No-arg constructor required by Hibernate

    public String getNumber() { return number; }
    public Phone setNumber(String number) { this.number = number; return this; }
    public PhoneType getType() { return type; }
    public Phone setType(PhoneType type) { this.type = type; return this; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Phone p)) return false;
        return Objects.equals(number, p.getNumber()) && Objects.equals(type, p.getType());
    }

    @Override
    public int hashCode() { return Objects.hash(number, type); }
}
