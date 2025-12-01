package parallax.backend.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Minimal {@link HttpExchange} implementation for unit tests.
 * Captures request metadata and response output without touching the network.
 */
public class TestHttpExchange extends HttpExchange {
    private final URI uri;
    private final String method;
    private final Headers requestHeaders;
    private final Headers responseHeaders = new Headers();
    private final ByteArrayInputStream requestBody;
    private final ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
    private int responseCode;
    private long responseLength;

    public TestHttpExchange(String method, URI uri, Headers headers, byte[] body) {
        this.method = method;
        this.uri = uri;
        this.requestHeaders = headers == null ? new Headers() : headers;
        this.requestBody = new ByteArrayInputStream(body == null ? new byte[0] : body);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public byte[] getResponseBodyBytes() {
        return responseBody.toByteArray();
    }

    public String getResponseBodyText() {
        return responseBody.toString(StandardCharsets.UTF_8);
    }

    @Override
    public Headers getRequestHeaders() {
        return requestHeaders;
    }

    @Override
    public Headers getResponseHeaders() {
        return responseHeaders;
    }

    @Override
    public URI getRequestURI() {
        return uri;
    }

    @Override
    public String getRequestMethod() {
        return method;
    }

    @Override
    public com.sun.net.httpserver.HttpContext getHttpContext() {
        return null;
    }

    @Override
    public void close() {
        responseBody.reset();
    }

    @Override
    public InputStream getRequestBody() {
        return requestBody;
    }

    @Override
    public OutputStream getResponseBody() {
        return responseBody;
    }

    @Override
    public void sendResponseHeaders(int rCode, long responseLength) throws IOException {
        this.responseCode = rCode;
        this.responseLength = responseLength;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(0);
    }

    @Override
    public int getResponseCode() {
        return responseCode;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(0);
    }

    @Override
    public String getProtocol() {
        return "HTTP/1.1";
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {
    }

    @Override
    public void setStreams(InputStream i, OutputStream o) {
    }

    @Override
    public HttpPrincipal getPrincipal() {
        return null;
    }
}
