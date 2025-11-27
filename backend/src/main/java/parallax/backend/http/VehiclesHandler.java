package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import parallax.backend.db.UserRepository;
import parallax.backend.db.VehicleRepository;
import parallax.backend.model.User;
import parallax.backend.model.Vehicle;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehiclesHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehiclesHandler(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        switch (method.toUpperCase()) {
            case "GET" -> handleGet(exchange);
            case "POST" -> handlePost(exchange);
            case "DELETE" -> handleDelete(exchange);
            default -> exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String username = getQueryParam(exchange.getRequestURI(), "username");
        if (isBlank(username)) {
            sendJson(exchange, 400, Map.of("message", "USERNAME_REQUIRED"));
            return;
        }

        List<Vehicle> vehicles = vehicleRepository.findByUsername(username.toLowerCase());
        sendJson(exchange, 200, vehicles);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        Vehicle request;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            request = gson.fromJson(reader, Vehicle.class);
        }

        if (request == null || isBlank(request.getUsername())) {
            sendJson(exchange, 400, Map.of("message", "USERNAME_REQUIRED"));
            return;
        }

        if (!isValidLicense(request.getLicenseNumber())) {
            sendJson(exchange, 400, Map.of("message", "INVALID_LICENSE"));
            return;
        }

        if (isBlank(request.getMake()) || isBlank(request.getModel()) || isBlank(request.getYear())) {
            sendJson(exchange, 400, Map.of("message", "VEHICLE_DETAILS_REQUIRED"));
            return;
        }

        Optional<Vehicle> existing = vehicleRepository.findByUsernameAndLicense(request.getUsername(), request.getLicenseNumber());
        if (existing.isPresent()) {
            sendJson(exchange, 409, Map.of("message", "LICENSE_EXISTS"));
            return;
        }

        // Optional: ensure the user exists in the repository for now
        Optional<User> user = userRepository.findByEmail(request.getUsername());
        if (user.isEmpty()) {
            // For now just create a placeholder user so the vehicle can be associated
            User placeholder = new User();
            placeholder.setUsername(request.getUsername().toLowerCase());
            placeholder.setEmail(request.getUsername().toLowerCase());
            placeholder.setDisplayName(request.getUsername());
            userRepository.createUser(placeholder);
        }

        Vehicle newVehicle = new Vehicle();
        newVehicle.setUsername(request.getUsername().toLowerCase());
        newVehicle.setLicenseNumber(request.getLicenseNumber().toUpperCase());
        newVehicle.setMake(request.getMake());
        newVehicle.setModel(request.getModel());
        newVehicle.setYear(request.getYear());
        newVehicle.setBlacklisted(false);
        newVehicle.setCreatedAt(Instant.now().toString());

        vehicleRepository.addVehicle(newVehicle);
        sendJson(exchange, 201, newVehicle);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Vehicle request;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            request = gson.fromJson(reader, Vehicle.class);
        }

        if (request == null || isBlank(request.getUsername()) || isBlank(request.getLicenseNumber())) {
            sendJson(exchange, 400, Map.of("message", "USERNAME_AND_LICENSE_REQUIRED"));
            return;
        }

        Optional<Vehicle> existing = vehicleRepository.findByUsernameAndLicense(request.getUsername(), request.getLicenseNumber());
        if (existing.isEmpty()) {
            sendJson(exchange, 404, Map.of("message", "NOT_FOUND"));
            return;
        }

        vehicleRepository.removeVehicle(request.getUsername(), request.getLicenseNumber());
        exchange.sendResponseHeaders(204, -1);
    }

    private boolean isValidLicense(String licenseNumber) {
        if (isBlank(licenseNumber)) {
            return false;
        }
        String trimmed = licenseNumber.trim();
        return trimmed.length() >= 1 && trimmed.length() <= 7 && trimmed.matches("[A-Z0-9-]+");
    }

    private String getQueryParam(URI uri, String key) {
        if (uri == null || uri.getQuery() == null) {
            return null;
        }
        String[] pairs = uri.getQuery().split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && key.equalsIgnoreCase(kv[0])) {
                return java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void addCorsHeaders(HttpExchange exchange) {
        Headers h = exchange.getResponseHeaders();
        h.add("Access-Control-Allow-Origin", "*");
        h.add("Access-Control-Allow-Headers", "Content-Type");
        h.add("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
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
