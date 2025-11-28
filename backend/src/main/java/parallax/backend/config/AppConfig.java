package parallax.backend.config;

public class AppConfig {
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_DB_PATH = "parallax.db";
    private static final boolean DEFAULT_ADMIN_ENABLED = true;
    private static final String DEFAULT_ADMIN_EMAIL = "admin@parallax.local";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin1234!";

    public static final boolean ADMIN_ENABLED = getBooleanEnv(
            "PARALLAX_ADMIN_ENABLED",
            DEFAULT_ADMIN_ENABLED
    );
    public static final String ADMIN_EMAIL = getEnvOrDefault(
            "PARALLAX_ADMIN_EMAIL",
            DEFAULT_ADMIN_EMAIL
    );
    public static final String ADMIN_PASSWORD = getEnvOrDefault(
            "PARALLAX_ADMIN_PASSWORD",
            DEFAULT_ADMIN_PASSWORD
    );

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

    private static boolean getBooleanEnv(String key, boolean defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }
}
