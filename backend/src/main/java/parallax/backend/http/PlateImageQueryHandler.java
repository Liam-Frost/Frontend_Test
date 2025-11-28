package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import parallax.backend.config.AppConfig;
import parallax.backend.db.VehicleRepository;
import parallax.backend.model.Vehicle;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Accepts an image upload from the browser, forwards it to the Python
 * plate-recognition HTTP service, and returns a concise status about whether
 * the detected plate exists in our repository and if it is blacklisted.
 */
public class PlateImageQueryHandler implements HttpHandler {
    private static final Gson gson = new Gson();

    private final AppConfig appConfig;
    private final VehicleRepository vehicleRepository;

    public PlateImageQueryHandler(AppConfig appConfig, VehicleRepository vehicleRepository) {
        this.appConfig = appConfig;
        this.vehicleRepository = vehicleRepository;
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

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            sendJson(exchange, 400, Map.of(
                    "success", false,
                    "message", "Please upload a valid image file."
            ));
            return;
        }

        byte[] imageBytes;
        try (InputStream inputStream = exchange.getRequestBody()) {
            imageBytes = inputStream.readAllBytes();
        }

        if (imageBytes.length == 0) {
            sendJson(exchange, 400, Map.of(
                    "success", false,
                    "message", "Image data is required."
            ));
            return;
        }

        RecognizeResponse recognizerResult;
        try {
            recognizerResult = forwardToRecognizer(imageBytes, contentType);
        } catch (Exception e) {
            // If the Python service is down or returns invalid JSON, surface a friendly error.
            sendJson(exchange, 200, Map.of(
                    "success", false,
                    "message", "Unable to analyze image at this time."
            ));
            return;
        }

        if (recognizerResult == null || !recognizerResult.success) {
            sendJson(exchange, 200, Map.of(
                    "success", false,
                    "message", "Unable to analyze image at this time."
            ));
            return;
        }

        String normalizedPlate = normalizeLicense(recognizerResult.plate);
        if (normalizedPlate == null || normalizedPlate.isBlank()) {
            sendJson(exchange, 200, Map.of(
                    "success", true,
                    "plateFound", false,
                    "foundInSystem", false,
                    "blacklisted", false,
                    "message", "No readable license plate was found in the image."
            ));
            return;
        }

        Optional<Vehicle> match = vehicleRepository.findByPlate(normalizedPlate);
        boolean foundInSystem = match.isPresent();
        boolean blacklisted = match.map(Vehicle::isBlacklisted).orElse(false);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("plateFound", true);
        response.put("licenseNumber", normalizedPlate);
        response.put("foundInSystem", foundInSystem);
        response.put("blacklisted", blacklisted);

        String message;
        if (!foundInSystem) {
            message = String.format("Detected plate %s. This plate is not registered in Parallax.", normalizedPlate);
        } else if (blacklisted) {
            message = String.format("Detected plate %s. This plate is registered and currently blacklisted.", normalizedPlate);
        } else {
            message = String.format("Detected plate %s. This plate is registered and not blacklisted.", normalizedPlate);
        }
        response.put("message", message);

        sendJson(exchange, 200, response);
    }

    private RecognizeResponse forwardToRecognizer(byte[] imageBytes, String contentType) throws IOException {
        URL url = URI.create(appConfig.getPlateServiceUrl()).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", contentType == null ? "application/octet-stream" : contentType);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(imageBytes);
        }

        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, RecognizeResponse.class);
        }
    }

    private String normalizeLicense(String licenseNumber) {
        return licenseNumber == null ? null : licenseNumber.trim().toUpperCase();
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

    private static class RecognizeResponse {
        boolean success;
        String plate;
        String message;
    }
}
