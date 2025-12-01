package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parallax.backend.config.AppConfig;
import parallax.backend.db.InMemoryUserRepository;
import parallax.backend.db.InMemoryVehicleRepository;
import parallax.backend.model.Vehicle;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VehiclesHandlerTest {
    private final Gson gson = new Gson();
    private InMemoryVehicleRepository vehicleRepository;
    private InMemoryUserRepository userRepository;
    private VehiclesHandler handler;

    @BeforeEach
    void setUp() {
        vehicleRepository = new InMemoryVehicleRepository();
        userRepository = new InMemoryUserRepository();
        handler = new VehiclesHandler(vehicleRepository, userRepository, new AppConfig());
    }

    @Test
    void createVehicle_persistsForUser() throws Exception {
        Vehicle request = new Vehicle();
        request.setUsername("owner@example.com");
        request.setLicenseNumber("abc123");
        request.setMake("Toyota");
        request.setModel("Camry");
        request.setYear("2020");

        byte[] body = gson.toJson(request).getBytes(StandardCharsets.UTF_8);
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/vehicles"), new Headers(), body);

        handler.handle(exchange);

        assertEquals(201, exchange.getResponseCode());
        List<Vehicle> vehicles = vehicleRepository.findByUsername("owner@example.com");
        assertEquals(1, vehicles.size());
        assertEquals("ABC123", vehicles.get(0).getLicenseNumber());
    }

    @Test
    void blacklistVehicle_requiresAdminAndUpdatesFlag() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setUsername("owner@example.com");
        vehicle.setLicenseNumber("admin1");
        vehicle.setMake("Tesla");
        vehicle.setModel("Model Y");
        vehicle.setYear("2022");
        vehicleRepository.addVehicle(vehicle);

        Vehicle request = new Vehicle();
        request.setUsername(AppConfig.ADMIN_EMAIL);
        request.setLicenseNumber("admin1");
        request.setBlacklisted(true);

        byte[] body = gson.toJson(request).getBytes(StandardCharsets.UTF_8);
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/vehicles/blacklist"), new Headers(), body);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        assertTrue(vehicleRepository.findByLicense("ADMIN1").orElseThrow().isBlacklisted());
    }

    @Test
    void queryVehicle_returnsBlacklistStatus() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setUsername("owner@example.com");
        vehicle.setLicenseNumber("abc123");
        vehicle.setBlacklisted(true);
        vehicleRepository.addVehicle(vehicle);

        TestHttpExchange exchange = new TestHttpExchange("GET", new URI("/api/vehicles/query?license=abc123"), new Headers(), new byte[0]);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        Map<?, ?> response = gson.fromJson(exchange.getResponseBodyText(), Map.class);
        assertEquals(Boolean.TRUE, response.get("found"));
        assertEquals(Boolean.TRUE, response.get("blacklisted"));
    }
}
