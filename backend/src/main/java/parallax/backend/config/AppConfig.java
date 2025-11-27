package parallax.backend.config;

public class AppConfig {
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_DB_PATH = "parallax.db";
    private static final boolean DEFAULT_ADMIN_ENABLED = true;
    private static final String DEFAULT_ADMIN_EMAIL = "admin@parallax.demo";
    private static final String DEFAULT_ADMIN_PASSWORD = "AdminDemo123";

    public int getPort() {
        String portValue = System.getenv("PARALLAX_PORT");
        if (portValue != null && !portValue.isBlank()) {
            try {
                return Integer.parseInt(portValue);
            } catch (NumberFormatException ignored) {
                // fall through to default
            }
        }
        return DEFAULT_PORT;
    }

    public String getDatabaseUrl() {
        String path = System.getenv("PARALLAX_DB_PATH");
        if (path == null || path.isBlank()) {
            path = DEFAULT_DB_PATH;
        }
        return "jdbc:sqlite:" + path;
    }

    public boolean isAdminEnabled() {
        return DEFAULT_ADMIN_ENABLED;
    }

    public String getAdminEmail() {
        // TODO: allow overriding via environment variables or system properties
        return DEFAULT_ADMIN_EMAIL;
    }

    public String getAdminPassword() {
        // TODO: allow overriding via environment variables or system properties
        return DEFAULT_ADMIN_PASSWORD;
    }
}
