package parallax.backend.model;

/**
 * Response payload returned after a login attempt.
 * Conveys whether authentication was successful along with user metadata.
 */
public class LoginResponse {
    private boolean success;
    private String message;
    private String username;
    private String displayName;
    private boolean admin;

    /**
     * Creates a response reflecting the result of an authentication attempt.
     *
     * @param success     whether authentication succeeded
     * @param message     human-readable status message
     * @param username    username/email of the authenticated user
     * @param displayName display name associated with the account
     */
    public LoginResponse(boolean success, String message, String username, String displayName) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.displayName = displayName;
        this.admin = false;
    }

    /**
     * Indicates whether authentication was successful.
     *
     * @return {@code true} when login succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the status message associated with the login attempt.
     *
     * @return status message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the username/email of the authenticated user, if any.
     *
     * @return username or {@code null} on failure
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the display name of the authenticated user.
     *
     * @return display name or {@code null} on failure
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Indicates whether the authenticated user has admin privileges.
     *
     * @return {@code true} when the account is recognized as admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Sets whether the account is treated as an administrator.
     *
     * @param admin admin flag
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
