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

public class AuthLoginHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private final UserRepository userRepository;
    private final AppConfig config;

    public AuthLoginHandler(UserRepository userRepository, AppConfig config) {
        this.userRepository = userRepository;
        this.config = config;
    }

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
            sendJson(exchange, 400, new LoginResponse(false, "Identifier and password are required", null, null, false));
            return;
        }

        Optional<User> user = userRepository.findByIdentifierAndPassword(request.getIdentifier(), request.getPassword());
        if (user.isEmpty()) {
            sendJson(exchange, 401, new LoginResponse(false, "Invalid credentials", null, null, false));
            return;
        }

        User found = user.get();
        boolean isAdmin = found.isAdmin() && config.isAdminEnabled();
        LoginResponse response = new LoginResponse(true, "Login successful", found.getUsername(), found.getDisplayName(), isAdmin);
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
