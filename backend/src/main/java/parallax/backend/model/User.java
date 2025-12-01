package parallax.backend.model;

/**
 * Domain model representing an account within the Parallax system.
 * <p>
 * The {@code username} doubles as the canonical email identifier and is associated with profile
 * details, contact preferences, and a plaintext password (to be hashed in production). Vehicles are
 * linked to a user through this username.
 * </p>
 */
public class User {
    private String username;
    private String email;
    private String displayName;
    private String password; // TODO: hash passwords when real auth is implemented
    private String firstName;
    private String lastName;
    private String country;
    private String birthMonth;
    private String birthDay;
    private String birthYear;
    private String phoneCountry;
    private String phone;
    private String contactMethod;
    private String createdAt;

    public User() {
    }

    /**
     * Convenience constructor for creating a user with the minimum identifying fields.
     *
     * @param username    canonical username/email
     * @param email       email address
     * @param displayName display name shown to the user
     * @param password    plaintext password
     */
    public User(String username, String email, String displayName, String password) {
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.password = password;
    }

    /**
     * Returns the canonical username/email.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the canonical username/email.
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the email address on record.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address on record.
     *
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the display name visible to the user.
     *
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name visible to the user.
     *
     * @param displayName display name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the plaintext password (to be replaced with a hash in production).
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the plaintext password (to be replaced with a hash in production).
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
     * Returns the country associated with the user profile.
     *
     * @return country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country associated with the user profile.
     *
     * @param country country
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
     * Returns the dialing code associated with the user's phone number.
     *
     * @return phone country code
     */
    public String getPhoneCountry() {
        return phoneCountry;
    }

    /**
     * Sets the dialing code associated with the user's phone number.
     *
     * @param phoneCountry phone country code
     */
    public void setPhoneCountry(String phoneCountry) {
        this.phoneCountry = phoneCountry;
    }

    /**
     * Returns the phone number digits without formatting.
     *
     * @return phone digits
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number digits without formatting.
     *
     * @param phone phone digits
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the user's preferred contact method.
     *
     * @return contact method (e.g., email or phone)
     */
    public String getContactMethod() {
        return contactMethod;
    }

    /**
     * Sets the user's preferred contact method.
     *
     * @param contactMethod contact method (e.g., email or phone)
     */
    public void setContactMethod(String contactMethod) {
        this.contactMethod = contactMethod;
    }

    /**
     * Returns the ISO-8601 timestamp when the account was created.
     *
     * @return creation timestamp
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the ISO-8601 timestamp when the account was created.
     *
     * @param createdAt creation timestamp
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
