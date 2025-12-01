package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import parallax.backend.config.AppConfig;
import parallax.backend.db.UserRepository;
import parallax.backend.db.VehicleRepository;
import parallax.backend.model.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * HTTP handler for account management operations.
 * <p>
 * Supports updating contact details, changing passwords, and deleting accounts via the
 * {@code /api/account} endpoint. Each operation validates credentials using the supplied
 * {@link UserRepository} and maintains vehicle ownership through {@link VehicleRepository} when a
 * username changes or an account is removed. CORS preflight requests are respected.
 * </p>
 */
public class AccountHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$");

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final AppConfig appConfig;

    /**
     * Creates an account handler.
     *
     * @param userRepository    repository used for credential validation and user updates
     * @param vehicleRepository repository used to keep vehicle ownership in sync with account changes
     * @param appConfig         configuration providing admin safeguards and CORS settings
     */
    public AccountHandler(UserRepository userRepository, VehicleRepository vehicleRepository, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.appConfig = appConfig;
    }

    /**
     * Routes account-related requests based on path suffix and HTTP method, delegating to specific
     * handlers for contact updates, password changes, or account deletion. Unsupported methods
     * return {@code 405 Method Not Allowed}.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);
        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        if (path.endsWith("/contact") && "POST".equalsIgnoreCase(method)) {
            handleContact(exchange);
            return;
        }
        if (path.endsWith("/password") && "POST".equalsIgnoreCase(method)) {
            handlePassword(exchange);
            return;
        }
        if (path.endsWith("/account") && "DELETE".equalsIgnoreCase(method)) {
            handleDelete(exchange);
            return;
        }

        exchange.sendResponseHeaders(405, -1);
    }

    /**
     * Updates a user's contact information after validating current credentials and ensuring email
     * and phone uniqueness. When the username/email changes, associated vehicles are reassigned to
     * the new owner key.
     */
    private void handleContact(HttpExchange exchange) throws IOException {
        ContactUpdateRequest request;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            request = gson.fromJson(reader, ContactUpdateRequest.class);
        }

        if (request == null || isBlank(request.username) || isBlank(request.currentPassword)) {
            sendJson(exchange, 400, Map.of("success", false, "message", "USERNAME_AND_PASSWORD_REQUIRED"));
            return;
        }

        Optional<User> existingUser = userRepository.findByEmail(request.username);
        if (existingUser.isEmpty()) {
            sendJson(exchange, 400, Map.of("success", false, "message", "USER_NOT_FOUND"));
            return;
        }

        User user = existingUser.get();
        if (!request.currentPassword.equals(user.getPassword())) {
            sendJson(exchange, 400, Map.of("success", false, "message", "INVALID_PASSWORD"));
            return;
        }

        String email = request.email == null ? null : request.email.trim().toLowerCase();
        String phoneCountry = request.phoneCountry == null ? "" : request.phoneCountry.trim();
        String phoneDigits = request.phone == null ? "" : request.phone.replaceAll("\\D", "");

        if (isBlank(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            sendJson(exchange, 400, Map.of("success", false, "message", "INVALID_EMAIL"));
            return;
        }

        if (isBlank(phoneCountry) || phoneDigits.length() < 5) {
            sendJson(exchange, 400, Map.of("success", false, "message", "INVALID_PHONE"));
            return;
        }

        if (AppConfig.ADMIN_ENABLED && appConfig.ADMIN_EMAIL.equalsIgnoreCase(email)) {
            sendJson(exchange, 400, Map.of("success", false, "message", "ADMIN_EMAIL_RESERVED"));
            return;
        }

        for (User other : userRepository.findAllUsers().values()) {
            if (other.getUsername().equalsIgnoreCase(user.getUsername())) {
                continue;
            }
            if (email.equalsIgnoreCase(other.getEmail())) {
                sendJson(exchange, 400, Map.of("success", false, "message", "EMAIL_EXISTS"));
                return;
            }
            String otherSignature = (other.getPhoneCountry() == null ? "" : other.getPhoneCountry()) +
                    (other.getPhone() == null ? "" : other.getPhone());
            if (!otherSignature.isBlank()) {
                String normalized = otherSignature.replaceAll("\\D", "");
                if (!normalized.isBlank() && normalized.equals((phoneCountry + phoneDigits).replaceAll("\\D", ""))) {
                    sendJson(exchange, 400, Map.of("success", false, "message", "PHONE_EXISTS"));
                    return;
                }
            }
        }

        String previousUsername = user.getUsername();
        userRepository.updateContact(user.getUsername(), email, phoneCountry, phoneDigits);
        if (!previousUsername.equalsIgnoreCase(email)) {
            vehicleRepository.reassignVehicles(previousUsername, email);
        }

        User sanitized = sanitizeUser(userRepository.findByEmail(email).orElse(user));
        sendJson(exchange, 200, Map.of("success", true, "user", sanitized));
    }

    /**
     * Changes a user's password after validating the current password and enforcing basic
     * complexity and confirmation rules.
     */
    private void handlePassword(HttpExchange exchange) throws IOException {
        PasswordUpdateRequest request;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            request = gson.fromJson(reader, PasswordUpdateRequest.class);
        }

        if (request == null || isBlank(request.username) || isBlank(request.oldPassword)) {
            sendJson(exchange, 400, Map.of("success", false, "message", "USERNAME_AND_PASSWORD_REQUIRED"));
            return;
        }

        Optional<User> existingUser = userRepository.findByEmail(request.username);
        if (existingUser.isEmpty()) {
            sendJson(exchange, 400, Map.of("success", false, "message", "USER_NOT_FOUND"));
            return;
        }

        User user = existingUser.get();
        if (!request.oldPassword.equals(user.getPassword())) {
            sendJson(exchange, 400, Map.of("success", false, "message", "INVALID_PASSWORD"));
            return;
        }

        if (isBlank(request.newPassword) || isBlank(request.confirmPassword)) {
            sendJson(exchange, 400, Map.of("success", false, "message", "PASSWORD_REQUIRED"));
            return;
        }

        if (!request.newPassword.equals(request.confirmPassword)) {
            sendJson(exchange, 400, Map.of("success", false, "message", "PASSWORD_MISMATCH"));
            return;
        }

        if (!PASSWORD_PATTERN.matcher(request.newPassword).matches()) {
            sendJson(exchange, 400, Map.of("success", false, "message", "INVALID_PASSWORD_FORMAT"));
            return;
        }

        userRepository.updatePassword(user.getUsername(), request.newPassword);
        sendJson(exchange, 200, Map.of("success", true));
    }

    /**
     * Deletes a user account and removes all associated vehicles after verifying credentials and
     * ensuring the admin account is protected.
     */
    private void handleDelete(HttpExchange exchange) throws IOException {
        DeleteRequest request;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            request = gson.fromJson(reader, DeleteRequest.class);
        }

        if (request == null || isBlank(request.username) || isBlank(request.currentPassword)) {
            sendJson(exchange, 400, Map.of("success", false, "message", "USERNAME_AND_PASSWORD_REQUIRED"));
            return;
        }

        if (AppConfig.ADMIN_ENABLED && appConfig.ADMIN_EMAIL.equalsIgnoreCase(request.username)) {
            sendJson(exchange, 400, Map.of("success", false, "message", "ADMIN_ACCOUNT_CANNOT_BE_DELETED"));
            return;
        }

        Optional<User> existingUser = userRepository.findByEmail(request.username);
        if (existingUser.isEmpty()) {
            sendJson(exchange, 400, Map.of("success", false, "message", "USER_NOT_FOUND"));
            return;
        }

        User user = existingUser.get();
        if (!request.currentPassword.equals(user.getPassword())) {
            sendJson(exchange, 400, Map.of("success", false, "message", "INVALID_PASSWORD"));
            return;
        }

        vehicleRepository.removeVehiclesForUser(user.getUsername());
        userRepository.deleteUser(user.getUsername());
        sendJson(exchange, 200, Map.of("success", true));
    }

    private User sanitizeUser(User user) {
        if (user == null) {
            return null;
        }
        User sanitized = new User();
        sanitized.setUsername(user.getUsername());
        sanitized.setEmail(user.getEmail());
        sanitized.setDisplayName(user.getDisplayName());
        sanitized.setFirstName(user.getFirstName());
        sanitized.setLastName(user.getLastName());
        sanitized.setCountry(user.getCountry());
        sanitized.setBirthMonth(user.getBirthMonth());
        sanitized.setBirthDay(user.getBirthDay());
        sanitized.setBirthYear(user.getBirthYear());
        sanitized.setPhoneCountry(user.getPhoneCountry());
        sanitized.setPhone(user.getPhone());
        sanitized.setContactMethod(user.getContactMethod());
        sanitized.setCreatedAt(user.getCreatedAt());
        return sanitized;
    }

    private void addCorsHeaders(HttpExchange exchange) {
        Headers h = exchange.getResponseHeaders();
        h.add("Access-Control-Allow-Origin", "*");
        h.add("Access-Control-Allow-Headers", "Content-Type");
        h.add("Access-Control-Allow-Methods", "POST, DELETE, OPTIONS");
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] bytes = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static class ContactUpdateRequest {
        String username;
        String currentPassword;
        String email;
        String phoneCountry;
        String phone;
    }

    private static class PasswordUpdateRequest {
        String username;
        String oldPassword;
        String newPassword;
        String confirmPassword;
    }

    private static class DeleteRequest {
        String username;
        String currentPassword;
    }
}
