package parallax.backend.model;

/**
 * Request payload for user registration capturing profile and contact details.
 * Fields correspond to the registration form and are validated by the handler.
 */
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String country;
    private String birthMonth;
    private String birthDay;
    private String birthYear;
    private String phoneCountry;
    private String phone;
    private String contactMethod;

    public RegisterRequest() {
    }

    /**
     * Returns the email address that will also serve as the username.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address that will also serve as the username.
     *
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the plaintext password provided during registration.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the plaintext password provided during registration.
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the user's first name.
     *
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user's first name.
     *
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the user's last name.
     *
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the user's last name.
     *
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the selected country.
     *
     * @return country code or name
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the selected country.
     *
     * @param country country code or name
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Returns the birth month component.
     *
     * @return birth month
     */
    public String getBirthMonth() {
        return birthMonth;
    }

    /**
     * Sets the birth month component.
     *
     * @param birthMonth birth month
     */
    public void setBirthMonth(String birthMonth) {
        this.birthMonth = birthMonth;
    }

    /**
     * Returns the birth day component.
     *
     * @return birth day
     */
    public String getBirthDay() {
        return birthDay;
    }

    /**
     * Sets the birth day component.
     *
     * @param birthDay birth day
     */
    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    /**
     * Returns the birth year component.
     *
     * @return birth year
     */
    public String getBirthYear() {
        return birthYear;
    }

    /**
     * Sets the birth year component.
     *
     * @param birthYear birth year
     */
    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    /**
     * Returns the dialing code associated with the phone number.
     *
     * @return phone country code
     */
    public String getPhoneCountry() {
        return phoneCountry;
    }

    /**
     * Sets the dialing code associated with the phone number.
     *
     * @param phoneCountry phone country code
     */
    public void setPhoneCountry(String phoneCountry) {
        this.phoneCountry = phoneCountry;
    }

    /**
     * Returns the phone number digits.
     *
     * @return phone digits
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number digits.
     *
     * @param phone phone digits
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the preferred contact method selected during registration.
     *
     * @return contact method (e.g., email or phone)
     */
    public String getContactMethod() {
        return contactMethod;
    }

    /**
     * Sets the preferred contact method selected during registration.
     *
     * @param contactMethod contact method (e.g., email or phone)
     */
    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }
}
