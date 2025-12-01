package parallax.backend.model;

/**
 * Request body for authentication containing a user identifier and password.
 * The identifier may be an email address or phone number depending on login flow.
 */
public class LoginRequest {
    private String identifier;
    private String password;

    /**
     * Returns the email or phone identifier supplied by the client.
     *
     * @return login identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the email or phone identifier supplied by the client.
     *
     * @param identifier login identifier
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the plaintext password to validate.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the plaintext password to validate.
     *
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
