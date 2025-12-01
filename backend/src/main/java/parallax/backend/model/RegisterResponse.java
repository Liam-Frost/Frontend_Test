package parallax.backend.model;

/**
 * Response payload for registration attempts.
 * Indicates success or specific error states and may include a sanitized {@link User} profile.
 */
public class RegisterResponse {
    private boolean success;
    private String message;
    private User user;

    public RegisterResponse() {
    }

    /**
     * Creates a response without a user payload.
     *
     * @param success whether registration succeeded
     * @param message status or error code
     */
    public RegisterResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a response including the sanitized user profile.
     *
     * @param success whether registration succeeded
     * @param message status or error code
     * @param user    sanitized user details
     */
    public RegisterResponse(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    /**
     * Indicates whether registration succeeded.
     *
     * @return {@code true} when the account was created
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the registration success flag.
     *
     * @param success whether the registration was successful
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Returns the status or error message.
     *
     * @return status or error code
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the status or error message.
     *
     * @param message status or error code
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the sanitized user profile when registration succeeded.
     *
     * @return registered user or {@code null}
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the sanitized user profile.
     *
     * @param user registered user
     */
    public void setUser(User user) {
        this.user = user;
    }
}
