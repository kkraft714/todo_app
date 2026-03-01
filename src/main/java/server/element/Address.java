package server.element;

/**
** Contains contact information.
*/
// ToDo: Support multiple phone numbers and addresses?
// ToDo: Define separate phone number class with type (mobile, home, work)?
public class Address {
  private String phoneNumber;
  private String email;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private Integer postalCode;

  // No-arg constructor required by Hibernate (ToDo: Is this still true?)
  public Address() {}

  public Address(String newPhone, String addr1, String addr2, String cityName, String stateName, Integer newCode) {
    this.phoneNumber = newPhone;
    this.address1 = addr1;
    this.address2 = addr2;
    this.city = cityName;
    this.state = stateName;
    this.postalCode = newCode;
  }

  public String getPhoneNumber() { return phoneNumber; }
  public Address setPhoneNumber(String newPhone) { this.phoneNumber = newPhone; return this; }

  public String getEmail() { return email; }
  public Address setEmail(String newEmail) { this.email = newEmail; return this; }

  public String getAddress1() { return address1; }
  public Address setAddress1(String addr1) { this.address1 = addr1; return this; }

  public String getAddress2() { return address2; }
  public Address setAddress2(String addr2) { this.address2 = addr2; return this; }

  public String getCity() { return city; }
  public Address setCity(String newCity) { this.city = newCity; return this; }

  public String getState() { return state; }
  public Address setState(String newState) { this.state = newState; return this; }

  public Integer getPostalCode() { return postalCode; }
  public Address setPostalCode(Integer code) { this.postalCode = code; return this; }

  @Override
  public String toString() {
    // ToDo: Could turn this into a toString() utility (takes a list of Strings)
    String result = "Contact:\n" + super.toString();
    String pc = postalCode == null ? null : postalCode.toString();
    for (String item : new String[] {phoneNumber, email, address1, address2, city, state, pc}) {
      if (item != null && !item.isEmpty()) {
        result = result.concat("\n" + item);
      }
    }
    return result;
  }
}
