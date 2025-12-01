package parallax.backend.http;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthHandlerTest {
    @Test
    void healthEndpoint_returnsOkStatus() throws Exception {
        HealthHandler handler = new HealthHandler();
        TestHttpExchange exchange = new TestHttpExchange("GET", new URI("/api/health"), new com.sun.net.httpserver.Headers(), new byte[0]);

        handler.handle(exchange);

        assertEquals(200, exchange.getResponseCode());
        assertEquals("{\"status\":\"ok\"}", exchange.getResponseBodyText());
    }
}
