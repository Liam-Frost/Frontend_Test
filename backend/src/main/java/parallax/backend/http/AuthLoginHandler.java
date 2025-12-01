package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import parallax.backend.config.AppConfig;
import parallax.backend.db.UserRepository;
import parallax.backend.model.LoginRequest;
import parallax.backend.model.LoginResponse;
import parallax.backend.model.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * HTTP handler for user authentication.
 * <p>
 * Accepts {@code POST /api/auth/login} with a JSON {@link LoginRequest} body containing an email or
 * phone identifier and plaintext password. Successful authentication returns a {@link
 * LoginResponse} detailing the username, display name, and admin status. CORS preflight requests
 * are also handled.
 * </p>
 */
public class AuthLoginHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private final UserRepository userRepository;
    private final AppConfig appConfig;

    /**
     * Creates a login handler backed by the provided repository and configuration.
     *
     * @param userRepository repository used to validate credentials
     * @param appConfig      configuration supplying admin credentials and CORS settings
     */
    public AuthLoginHandler(UserRepository userRepository, AppConfig appConfig) {
        this.userRepository = userRepository;
        this.appConfig = appConfig;
    }

    /**
     * Processes login requests by validating the HTTP method, parsing the JSON body, checking
     * administrator credentials when enabled, and delegating user lookup to the repository.
     * Returns 401 on invalid credentials and 200 with {@link LoginResponse} on success.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        LoginRequest request;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            request = gson.fromJson(reader, LoginRequest.class);
        }

        if (request == null || request.getIdentifier() == null || request.getIdentifier().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            sendJson(exchange, 400, new LoginResponse(false, "Identifier and password are required", null, null));
            return;
        }

        // Admin login path - validate solely against configuration
        if (AppConfig.ADMIN_ENABLED
                && appConfig.ADMIN_EMAIL.equalsIgnoreCase(request.getIdentifier())
                && appConfig.ADMIN_PASSWORD.equals(request.getPassword())) {
            LoginResponse adminResponse = new LoginResponse(true, "Login successful", AppConfig.ADMIN_EMAIL, "Admin");
            adminResponse.setAdmin(true);
            sendJson(exchange, 200, adminResponse);
            return;
        }

        Optional<User> user = userRepository.findByIdentifierAndPassword(request.getIdentifier(), request.getPassword());
        if (user.isEmpty()) {
            sendJson(exchange, 401, new LoginResponse(false, "Invalid credentials", null, null));
            return;
        }

        User found = user.get();
        LoginResponse response = new LoginResponse(true, "Login successful", found.getUsername(), found.getDisplayName());
        response.setAdmin(false);
        // TODO: plug in real authentication (sessions / tokens) when SQLite integration arrives
        sendJson(exchange, 200, response);

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
