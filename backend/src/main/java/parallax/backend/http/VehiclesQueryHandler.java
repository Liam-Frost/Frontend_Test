package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import parallax.backend.db.UserRepository;
import parallax.backend.db.VehicleRepository;
import parallax.backend.model.Vehicle;
import parallax.backend.model.VehicleView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class VehiclesQueryHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehiclesQueryHandler(VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCorsHeaders(exchange);

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String license = getQueryParam(exchange.getRequestURI(), "license");
        String normalized = license == null ? null : license.trim().toUpperCase();
        if (!isValidLicense(normalized)) {
            sendJson(exchange, 400, Map.of("message", "INVALID_LICENSE"));
            return;
        }

        Optional<Vehicle> vehicle = vehicleRepository.findByPlate(normalized);
        if (vehicle.isEmpty()) {
            sendJson(exchange, 200, Map.of("found", false));
            return;
        }

        Vehicle found = vehicle.get();
        VehicleView view = VehicleView.from(found, userRepository.findByEmail(found.getUsername()).orElse(null));
        sendJson(exchange, 200, Map.of("found", true, "vehicle", view));
    }

    private boolean isValidLicense(String licenseNumber) {
        if (licenseNumber == null) {
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

    private void addCorsHeaders(HttpExchange exchange) {
        Headers h = exchange.getResponseHeaders();
        h.add("Access-Control-Allow-Origin", "*");
        h.add("Access-Control-Allow-Headers", "Content-Type");
        h.add("Access-Control-Allow-Methods", "GET, OPTIONS");
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
