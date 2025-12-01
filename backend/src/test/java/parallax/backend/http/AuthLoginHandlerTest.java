package parallax.backend.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parallax.backend.config.AppConfig;
import parallax.backend.db.InMemoryUserRepository;
import parallax.backend.model.LoginRequest;
import parallax.backend.model.LoginResponse;
import parallax.backend.model.User;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AuthLoginHandlerTest {
    private final Gson gson = new Gson();
    private InMemoryUserRepository userRepository;
    private AuthLoginHandler handler;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        handler = new AuthLoginHandler(userRepository, new AppConfig());
    }

    @Test
    void validCredentials_returnSuccess() throws Exception {
        User user = new User();
        user.setUsername("login@example.com");
        user.setEmail("login@example.com");
        user.setPassword("Secret123");
        user.setDisplayName("Login User");
        userRepository.createUser(user);

        LoginRequest request = new LoginRequest();
        request.setIdentifier("login@example.com");
        request.setPassword("Secret123");

        byte[] body = gson.toJson(request).getBytes(StandardCharsets.UTF_8);
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/auth/login"), new Headers(), body);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        LoginResponse response = gson.fromJson(exchange.getResponseBodyText(), LoginResponse.class);
        assertTrue(response.isSuccess());
        assertEquals("login@example.com", response.getUsername());
        assertEquals("Login User", response.getDisplayName());
    }

    @Test
    void wrongPassword_returnsUnauthorized() throws Exception {
        User user = new User();
        user.setUsername("login@example.com");
        user.setEmail("login@example.com");
        user.setPassword("Secret123");
        userRepository.createUser(user);

        LoginRequest request = new LoginRequest();
        request.setIdentifier("login@example.com");
        request.setPassword("WrongPass");

        byte[] body = gson.toJson(request).getBytes(StandardCharsets.UTF_8);
        TestHttpExchange exchange = new TestHttpExchange("POST", new URI("/api/auth/login"), new Headers(), body);

        handler.handle(exchange);

        assertEquals(401, exchange.getResponseCode());
        LoginResponse response = gson.fromJson(exchange.getResponseBodyText(), LoginResponse.class);
        assertFalse(response.isSuccess());
    }
}
