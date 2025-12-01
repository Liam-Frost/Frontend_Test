package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import parallax.backend.config.AppConfig;
import parallax.backend.db.UserRepository;
import parallax.backend.model.RegisterRequest;
import parallax.backend.model.RegisterResponse;
import parallax.backend.model.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

/**
 * HTTP handler for user registration.
 * <p>
 * Accepts {@code POST /api/auth/register} with a JSON {@link RegisterRequest} payload. Performs
 * basic validation, rejects conflicts with the configured admin account, persists a new user
 * through {@link UserRepository}, and returns a sanitized {@link RegisterResponse}. Handles CORS
 * preflight requests.
 * </p>
 */
public class AuthRegisterHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private final UserRepository userRepository;
    private final AppConfig appConfig;

    /**
     * Constructs a registration handler.
     *
     * @param userRepository repository where new users are created
     * @param appConfig      configuration used for admin safeguards and CORS settings
     */
    public AuthRegisterHandler(UserRepository userRepository, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.appConfig = appConfig;
    }

    /**
     * Processes registration requests, ensuring required fields are present, verifying email and
     * phone uniqueness, and creating a new user record when valid. Responds with appropriate HTTP
     * status codes for validation errors or conflicts.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        RegisterRequest request;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            request = gson.fromJson(reader, RegisterRequest.class);
        }

        if (request == null || isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            sendJson(exchange, 400, new RegisterResponse(false, "EMAIL_AND_PASSWORD_REQUIRED"));
            return;
        }

        if (AppConfig.ADMIN_ENABLED && appConfig.ADMIN_EMAIL.equalsIgnoreCase(request.getEmail())) {
            sendJson(exchange, 400, new RegisterResponse(false, "ADMIN_ACCOUNT_CANNOT_BE_REGISTERED"));
            return;
        }

        Optional<User> existingByEmail = userRepository.findByEmail(request.getEmail());
        if (existingByEmail.isPresent()) {
            sendJson(exchange, 409, new RegisterResponse(false, "EMAIL_EXISTS"));
            return;
        }

        if (!isBlank(request.getPhoneCountry()) && !isBlank(request.getPhone())) {
            Optional<User> existingByPhone = userRepository.findByPhone(request.getPhoneCountry(), request.getPhone());
            if (existingByPhone.isPresent()) {
                sendJson(exchange, 409, new RegisterResponse(false, "PHONE_EXISTS"));
                return;
            }
        }

        User user = new User();
        String normalizedEmail = request.getEmail().toLowerCase();
        user.setUsername(normalizedEmail);
        user.setEmail(normalizedEmail);
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDisplayName(buildDisplayName(request));
        user.setCountry(request.getCountry());
        user.setBirthMonth(request.getBirthMonth());
        user.setBirthDay(request.getBirthDay());
        user.setBirthYear(request.getBirthYear());
        user.setPhoneCountry(request.getPhoneCountry());
        user.setPhone(request.getPhone());
        user.setContactMethod(request.getContactMethod());
        user.setCreatedAt(Instant.now().toString());

        userRepository.createUser(user);

        RegisterResponse response = new RegisterResponse(true, "REGISTERED", sanitizeUser(user));
        sendJson(exchange, 201, response);
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

    private String buildDisplayName(RegisterRequest request) {
        String first = request.getFirstName();
        String last = request.getLastName();
        String combined = String.join(" ",
                (first == null ? "" : first).trim(),
                (last == null ? "" : last).trim()).trim();
        if (combined.isBlank()) {
            return request.getEmail();
        }
        return combined;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void addCorsHeaders(HttpExchange exchange) {
        Headers h = exchange.getResponseHeaders();
        h.add("Access-Control-Allow-Origin", "*");
        h.add("Access-Control-Allow-Headers", "Content-Type");
        h.add("Access-Control-Allow-Methods", "POST, OPTIONS");
    }

    private void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] bytes = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
