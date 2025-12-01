package parallax.backend.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Minimal JDBC data source wrapper intended as a seam for future SQLite support.
 * <p>
 * The current backend uses in-memory repositories, but this class centralizes connection
 * creation so repository implementations can transition to a persistent database without
 * altering call sites.
 * </p>
 */
public class DataSource {
    private final String url;

    /**
        * Creates a data source pointing at the provided JDBC URL.
        *
        * @param url JDBC connection string, typically {@code jdbc:sqlite:<file>} for SQLite
        */
    public DataSource(String url) {
        this.url = url;
    }

    /**
        * Opens a new JDBC connection using the configured URL.
        * Callers are responsible for closing the returned connection.
        *
        * @return a new {@link Connection} to the configured database
        * @throws SQLException if the underlying driver cannot establish a connection
        */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
}
