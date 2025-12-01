package parallax.backend.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import parallax.backend.config.AppConfig;
import parallax.backend.db.VehicleRepository;
import parallax.backend.model.Vehicle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * HTTP handler that accepts an uploaded plate image, forwards it to the external Python recognition
 * service, and reports whether the recognized plate exists or is blacklisted in the system.
 * <p>
 * Expects {@code POST /api/vehicles/query-image} with a {@code multipart/form-data} body containing
 * a file field named {@code image}. The Python service is expected to return JSON with fields such
 * as {@code success}, {@code plateFound}, and {@code licenseNumber}. The handler responds with JSON
 * describing whether a plate was detected and the blacklist status if present in
 * {@link VehicleRepository}.
 * </p>
 */
public class PlateImageQueryHandler implements HttpHandler {
    private static final Gson gson = new Gson();
    private final VehicleRepository vehicleRepository;
    private final AppConfig appConfig;

    /**
     * Creates the handler with the required repositories and configuration.
     *
     * @param vehicleRepository repository used to resolve blacklist status for detected plates
     * @param appConfig         configuration providing the external recognition service URL and CORS settings
     */
    public PlateImageQueryHandler(VehicleRepository vehicleRepository, AppConfig appConfig) {
        this.vehicleRepository = vehicleRepository;
        this.appConfig = appConfig;
    }

    /**
     * Processes multipart image upload requests, sends the image to the Python detection service,
     * and returns a JSON object indicating detection success and blacklist status. Rejects
     * unsupported methods or malformed multipart payloads with appropriate status codes.
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

        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.contains("multipart/form-data")) {
            sendJson(exchange, 400, Map.of("success", false, "message", "INVALID_CONTENT_TYPE"));
            return;
        }

        String boundary = extractBoundary(contentType);
        if (boundary == null || boundary.isBlank()) {
            sendJson(exchange, 400, Map.of("success", false, "message", "INVALID_BOUNDARY"));
            return;
        }

        byte[] requestBytes = readAllBytes(exchange.getRequestBody());
        byte[] imageBytes = extractFile(requestBytes, boundary, "image");
        if (imageBytes == null) {
            sendJson(exchange, 400, Map.of("success", false, "message", "IMAGE_REQUIRED"));
            return;
        }

        Path tempFile = Files.createTempFile("plate-upload-", ".bin");
        try {
            Files.write(tempFile, imageBytes);
            JsonObject detectionResponse = callPythonService(tempFile);
            if (detectionResponse == null) {
                sendJson(exchange, 500, Map.of("success", false, "message", "Image recognition failed."));
                return;
            }

            boolean success = detectionResponse.has("success") && detectionResponse.get("success").getAsBoolean();
            if (!success) {
                sendJson(exchange, 500, Map.of("success", false, "message", "Image recognition failed."));
                return;
            }

            boolean plateFound = detectionResponse.has("plateFound") && detectionResponse.get("plateFound").getAsBoolean();
            if (!plateFound) {
                sendJson(exchange, 200, Map.of(
                        "success", true,
                        "plateFound", false,
                        "message", "No readable license plate was found in the image."
                ));
                return;
            }

            String plate = detectionResponse.has("licenseNumber") ? detectionResponse.get("licenseNumber").getAsString() : null;
            String normalizedPlate = normalizeLicense(plate);
            Optional<Vehicle> match = vehicleRepository.findByPlate(normalizedPlate);

            boolean foundInSystem = match.isPresent();
            boolean blacklisted = match.map(Vehicle::isBlacklisted).orElse(false);

            sendJson(exchange, 200, Map.of(
                    "success", true,
                    "plateFound", true,
                    "licenseNumber", normalizedPlate,
                    "foundInSystem", foundInSystem,
                    "blacklisted", blacklisted
            ));
        } catch (Exception e) {
            sendJson(exchange, 500, Map.of("success", false, "message", "Image recognition failed."));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Calls the configured Python service with a multipart request containing the temporary image
     * file. The service is expected to respond with JSON indicating detection results.
     *
     * @param imagePath path to the temporary file to send
     * @return parsed JSON response, or {@code null} if the call fails
     * @throws IOException          if the request cannot be built or read
     * @throws InterruptedException if the HTTP client is interrupted while waiting for a response
     */
    protected JsonObject callPythonService(Path imagePath) throws IOException, InterruptedException {
        String boundary = "----Parallax" + UUID.randomUUID();
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"").append(imagePath.getFileName()).append("\"\r\n");
        sb.append("Content-Type: application/octet-stream\r\n\r\n");
        body.write(sb.toString().getBytes(StandardCharsets.UTF_8));
        body.write(Files.readAllBytes(imagePath));
        body.write("\r\n".getBytes(StandardCharsets.UTF_8));
        body.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(appConfig.getPlateServiceBaseUrl() + "/detect-plate"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            return null;
        }
        return gson.fromJson(response.body(), JsonObject.class);
    }

    private String extractBoundary(String contentType) {
        for (String part : contentType.split(";")) {
            String trimmed = part.trim();
            if (trimmed.startsWith("boundary=")) {
                return trimmed.substring("boundary=".length());
            }
        }
        return null;
    }

    private byte[] extractFile(byte[] body, String boundary, String fieldName) {
        String payload = new String(body, StandardCharsets.ISO_8859_1);
        String marker = "--" + boundary;
        int index = 0;
        while (index < payload.length()) {
            int start = payload.indexOf(marker, index);
            if (start < 0) {
                break;
            }
            int headerStart = start + marker.length() + 2; // skip CRLF
            int headerEnd = payload.indexOf("\r\n\r\n", headerStart);
            if (headerEnd < 0) {
                break;
            }
            String headers = payload.substring(headerStart, headerEnd);
            if (headers.contains("name=\"" + fieldName + "\"")) {
                int dataStart = headerEnd + 4;
                int nextBoundary = payload.indexOf("\r\n" + marker, dataStart);
                if (nextBoundary < 0) {
                    nextBoundary = payload.indexOf(marker + "--", dataStart);
                    if (nextBoundary < 0) {
                        nextBoundary = payload.length();
                    }
                }
                int dataEnd = nextBoundary;
                if (dataEnd >= 2 && payload.startsWith("\r\n", dataEnd - 2)) {
                    dataEnd -= 2;
                }
                return java.util.Arrays.copyOfRange(body, dataStart, dataEnd);
            }
            index = headerEnd + 4;
        }
        return null;
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private String normalizeLicense(String licenseNumber) {
        if (licenseNumber == null) {
            return null;
        }
        return licenseNumber.trim().toUpperCase();
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
