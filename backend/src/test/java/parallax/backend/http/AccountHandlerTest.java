package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parallax.backend.config.AppConfig;
import parallax.backend.db.InMemoryUserRepository;
import parallax.backend.db.InMemoryVehicleRepository;
import parallax.backend.model.User;
import parallax.backend.model.Vehicle;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AccountHandlerTest {
    private final Gson gson = new Gson();
    private InMemoryUserRepository userRepository;
    private InMemoryVehicleRepository vehicleRepository;
    private AccountHandler handler;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        vehicleRepository = new InMemoryVehicleRepository();
        handler = new AccountHandler(userRepository, vehicleRepository, new AppConfig());
    }

    @Test
    void updateContact_movesVehiclesAndUpdatesUsername() throws Exception {
        User user = new User();
        user.setUsername("owner@example.com");
        user.setEmail("owner@example.com");
        user.setPassword("Password123");
        userRepository.createUser(user);

        Vehicle vehicle = new Vehicle();
        vehicle.setUsername("owner@example.com");
        vehicle.setLicenseNumber("AAA111");
        vehicle.setMake("Ford");
        vehicle.setModel("Focus");
        vehicle.setYear("2018");
        vehicleRepository.addVehicle(vehicle);

        Map<String, Object> payload = Map.of(
                "username", "owner@example.com",
                "currentPassword", "Password123",
                "email", "new@example.com",
                "phoneCountry", "+1",
                "phone", "5551234"
        );
        byte[] body = gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
        Headers headers = new Headers();
        headers.add("Content-Type", "application/json");
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/account/contact"), headers, body);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        assertTrue(userRepository.findByEmail("owner@example.com").isEmpty());
        assertTrue(userRepository.findByEmail("new@example.com").isPresent());
        List<Vehicle> reassigned = vehicleRepository.findByUsername("new@example.com");
        assertEquals(1, reassigned.size());
        assertEquals("new@example.com", reassigned.get(0).getUsername());
    }

    @Test
    void changePassword_withCorrectOldPasswordSucceeds() throws Exception {
        User user = new User();
        user.setUsername("user@example.com");
        user.setEmail("user@example.com");
        user.setPassword("OldPass1A");
        userRepository.createUser(user);

        Map<String, Object> payload = Map.of(
                "username", "user@example.com",
                "oldPassword", "OldPass1A",
                "newPassword", "NewPass1A",
                "confirmPassword", "NewPass1A"
        );
        byte[] body = gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/account/password"), new Headers(), body);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        assertEquals("NewPass1A", userRepository.findByEmail("user@example.com").orElseThrow().getPassword());
    }

    @Test
    void deleteAccount_removesUserAndVehicles() throws Exception {
        User user = new User();
        user.setUsername("delete@example.com");
        user.setEmail("delete@example.com");
        user.setPassword("Secret123");
        userRepository.createUser(user);

        Vehicle vehicle = new Vehicle();
        vehicle.setUsername("delete@example.com");
        vehicle.setLicenseNumber("DEL123");
        vehicleRepository.addVehicle(vehicle);

        Map<String, Object> payload = Map.of(
                "username", "delete@example.com",
                "currentPassword", "Secret123"
        );
        byte[] body = gson.toJson(payload).getBytes(StandardCharsets.UTF_8);
        TestHttpExchange exchange = new TestHttpExchange("DELETE", new URI("/api/account"), new Headers(), body);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        assertTrue(userRepository.findByEmail("delete@example.com").isEmpty());
        assertTrue(vehicleRepository.findByUsername("delete@example.com").isEmpty());
    }
}
