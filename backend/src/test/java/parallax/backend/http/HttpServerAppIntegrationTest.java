package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parallax.backend.config.AppConfig;
import parallax.backend.db.InMemoryUserRepository;
import parallax.backend.db.InMemoryVehicleRepository;
import parallax.backend.model.LoginResponse;
import parallax.backend.model.RegisterResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpServerAppIntegrationTest {
    private final Gson gson = new Gson();
    private HttpServer server;
    private String baseUrl;
    private InMemoryUserRepository userRepository;
    private InMemoryVehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() throws Exception {
        userRepository = new InMemoryUserRepository();
        vehicleRepository = new InMemoryVehicleRepository();
        server = HttpServerApp.startServer(new TestConfig(0), userRepository, vehicleRepository);
        baseUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void registrationAndLoginRoundtrip() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String registerJson = gson.toJson(Map.of(
                "email", "integ@example.com",
                "password", "Password123"
        ));
        HttpResponse<String> registerResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/register"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(registerJson))
                .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(201, registerResponse.statusCode());
        RegisterResponse registerBody = gson.fromJson(registerResponse.body(), RegisterResponse.class);
        assertTrue(registerBody.isSuccess());

        String loginJson = gson.toJson(Map.of(
                "identifier", "integ@example.com",
                "password", "Password123"
        ));
        HttpResponse<String> loginResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/login"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginJson))
                .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, loginResponse.statusCode());
        LoginResponse loginBody = gson.fromJson(loginResponse.body(), LoginResponse.class);
        assertTrue(loginBody.isSuccess());
        assertEquals("integ@example.com", loginBody.getUsername());
    }

    @Test
    void vehicleLifecycle_blacklistAndQueryFlow() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String registerJson = gson.toJson(Map.of(
                "email", "vehicle@example.com",
                "password", "Password123"
        ));
        client.send(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/auth/register"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(registerJson))
                .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        String vehicleJson = gson.toJson(Map.of(
                "username", "vehicle@example.com",
                "licenseNumber", "abc123",
                "make", "Honda",
                "model", "Civic",
                "year", "2019"
        ));
        HttpResponse<String> createVehicle = client.send(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/vehicles"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(vehicleJson))
                .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(201, createVehicle.statusCode());

        String blacklistJson = gson.toJson(Map.of(
                "username", AppConfig.ADMIN_EMAIL,
                "licenseNumber", "abc123",
                "blacklisted", true
        ));
        HttpResponse<String> blacklistResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/vehicles/blacklist"))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(blacklistJson))
                .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        assertEquals(200, blacklistResponse.statusCode());

        HttpResponse<String> queryResponse = client.send(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/vehicles/query?license=abc123"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, queryResponse.statusCode());
        Map<?, ?> queryBody = gson.fromJson(queryResponse.body(), Map.class);
        assertEquals(Boolean.TRUE, queryBody.get("found"));
        assertEquals(Boolean.TRUE, queryBody.get("blacklisted"));
    }

    @Test
    void healthEndpoint_returnsOk() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/health"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("ok"));
    }

    private static class TestConfig extends AppConfig {
        private final int port;

        TestConfig(int port) {
            this.port = port;
        }

        @Override
        public int getPort() {
            return port;
        }
    }
}
