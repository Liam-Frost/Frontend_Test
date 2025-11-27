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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class VehiclesBlacklistHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehiclesBlacklistHandler(VehicleRepository vehicleRepository, UserRepository userRepository) {
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

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Vehicle request;
        try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
            request = gson.fromJson(reader, Vehicle.class);
        }

        if (request == null || isBlank(request.getUsername()) || isBlank(request.getLicenseNumber())) {
            sendJson(exchange, 400, Map.of("message", "USERNAME_AND_LICENSE_REQUIRED"));
            return;
        }

        boolean callerIsAdmin = userRepository.isAdminUser(request.getUsername());
        Optional<Vehicle> targetVehicle;
        if (callerIsAdmin) {
            targetVehicle = vehicleRepository.findByPlate(request.getLicenseNumber());
        } else {
            targetVehicle = vehicleRepository.findByOwnerAndPlate(request.getUsername(), request.getLicenseNumber());
        }

        if (targetVehicle.isEmpty()) {
            sendJson(exchange, 404, Map.of("message", "NOT_FOUND"));
            return;
        }

        Vehicle existing = targetVehicle.get();
        Optional<Vehicle> updated;
        if (callerIsAdmin) {
            updated = vehicleRepository.updateBlacklist(existing.getUsername(), existing.getLicenseNumber(), request.isBlacklisted());
        } else {
            updated = vehicleRepository.updateBlacklist(request.getUsername(), request.getLicenseNumber(), request.isBlacklisted());
        }

        VehicleView view = VehicleView.from(updated.orElse(existing), userRepository.findByEmail(existing.getUsername()).orElse(null));
        sendJson(exchange, 200, view);
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
