package parallax.backend.http;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parallax.backend.config.AppConfig;
import parallax.backend.db.InMemoryVehicleRepository;
import parallax.backend.model.Vehicle;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlateImageQueryHandlerTest {
    private InMemoryVehicleRepository vehicleRepository;
    private PlateImageQueryHandler handler;

    @BeforeEach
    void setUp() {
        vehicleRepository = new InMemoryVehicleRepository();
        handler = new StubPlateImageQueryHandler(vehicleRepository, new AppConfig());
    }

    @Test
    void whenPlateFound_returnsBlacklistStatus() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setUsername("user@example.com");
        vehicle.setLicenseNumber("ABC123");
        vehicle.setBlacklisted(true);
        vehicleRepository.addVehicle(vehicle);

        byte[] body = buildMultipart("----boundary", "image", "content");
        Headers headers = new Headers();
        headers.add("Content-Type", "multipart/form-data; boundary=----boundary");
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/vehicles/query-image"), headers, body);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        Map<?, ?> response = new com.google.gson.Gson().fromJson(exchange.getResponseBodyText(), Map.class);
        assertEquals(Boolean.TRUE, response.get("plateFound"));
        assertEquals(Boolean.TRUE, response.get("foundInSystem"));
        assertEquals(Boolean.TRUE, response.get("blacklisted"));
    }

    @Test
    void whenNoPlateFound_returnsNotFoundMessage() throws Exception {
        handler = new StubPlateImageQueryHandler(vehicleRepository, new AppConfig(), false);
        byte[] body = buildMultipart("----boundary", "image", "content");
        Headers headers = new Headers();
        headers.add("Content-Type", "multipart/form-data; boundary=----boundary");
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/vehicles/query-image"), headers, body);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        Map<?, ?> response = new com.google.gson.Gson().fromJson(exchange.getResponseBodyText(), Map.class);
        assertEquals(Boolean.FALSE, response.get("plateFound"));
    }

    private byte[] buildMultipart(String boundary, String fieldName, String content) {
        String payload = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"file.bin\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n" +
                content + "\r\n" +
                "--" + boundary + "--\r\n";
        return payload.getBytes(StandardCharsets.ISO_8859_1);
    }

    private static class StubPlateImageQueryHandler extends PlateImageQueryHandler {
        private final boolean plateFound;

        StubPlateImageQueryHandler(InMemoryVehicleRepository repository, AppConfig config) {
            this(repository, config, true);
        }

        StubPlateImageQueryHandler(InMemoryVehicleRepository repository, AppConfig config, boolean plateFound) {
            super(repository, config);
            this.plateFound = plateFound;
        }

        @Override
        protected JsonObject callPythonService(Path imagePath) {
            JsonObject response = new JsonObject();
            response.addProperty("success", true);
            response.addProperty("plateFound", plateFound);
            if (plateFound) {
                response.addProperty("licenseNumber", "ABC123");
            }
            return response;
        }
    }
}
