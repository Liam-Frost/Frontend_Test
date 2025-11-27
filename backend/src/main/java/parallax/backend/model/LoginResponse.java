package parallax.backend.model;

public class LoginResponse {
    private boolean success;
    private String message;
    private String username;
    private String displayName;
    private boolean admin;

    public LoginResponse(boolean success, String message, String username, String displayName, boolean admin) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.displayName = displayName;
        this.admin = admin;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAdmin() {
        return admin;
    }
}
