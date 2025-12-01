package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parallax.backend.config.AppConfig;
import parallax.backend.db.InMemoryUserRepository;
import parallax.backend.model.RegisterRequest;
import parallax.backend.model.RegisterResponse;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AuthRegisterHandlerTest {
    private final Gson gson = new Gson();
    private InMemoryUserRepository userRepository;
    private AuthRegisterHandler handler;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        handler = new AuthRegisterHandler(userRepository, new AppConfig());
    }

    @Test
    void registerNewUser_returnsCreatedUser() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("Password123");
        request.setPhoneCountry("+1");
        request.setPhone("5551234");

        byte[] body = gson.toJson(request).getBytes(StandardCharsets.UTF_8);
        Headers headers = new Headers();
        headers.add("Content-Type", "application/json");
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/auth/register"), headers, body);

        handler.handle(exchange);

        assertEquals(201, exchange.getResponseCode());
        RegisterResponse response = gson.fromJson(exchange.getResponseBodyText(), RegisterResponse.class);
        assertTrue(response.isSuccess());
        assertNotNull(response.getUser());
        assertEquals("newuser@example.com", response.getUser().getUsername());
    }

    @Test
    void duplicateEmail_returnsConflict() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("Password123");
        byte[] body = gson.toJson(request).getBytes(StandardCharsets.UTF_8);

        Headers headers = new Headers();
        headers.add("Content-Type", "application/json");
        TestHttpExchange first = new TestHttpExchange("POST", new URI("/api/auth/register"), headers, body);
        handler.handle(first);

        TestHttpExchange duplicate = new TestHttpExchange("POST", new URI("/api/auth/register"), headers, body);
        handler.handle(duplicate);

        assertEquals(409, duplicate.getResponseCode());
        RegisterResponse response = gson.fromJson(duplicate.getResponseBodyText(), RegisterResponse.class);
        assertFalse(response.isSuccess());
        assertEquals("EMAIL_EXISTS", response.getMessage());
    }
}
