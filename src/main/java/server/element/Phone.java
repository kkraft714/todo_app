package server.element;

// ToDo Resurrect ContactInfo and add email?
public class Phone {
    public enum PhoneType { MOBILE, HOME, WORK, OTHER };
    private String number;
    private PhoneType type;
    private String customPhoneType;   // Support user-defined phone types

    public Phone(String number, PhoneType type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public PhoneType getType() { return type; }
    public void setType(PhoneType type) { this.type = type; }
}
