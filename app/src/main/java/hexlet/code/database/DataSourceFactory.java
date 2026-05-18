package hexlet.code.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Builds a shared {@link HikariDataSource}: PostgreSQL URL from {@code JDBC_DATABASE_URL}
 * or in-memory H2 database named {@code project} for local development.
 */
public final class DataSourceFactory {

    private static final String JDBC_DATABASE_URL = "JDBC_DATABASE_URL";

    private static final String H2_IN_MEMORY_PROJECT = "jdbc:h2:mem:project";

    private static volatile HikariDataSource dataSource;

    private DataSourceFactory() {
    }

    /**
     * Returns the singleton {@link DataSource} (Hikari pool).
     *
     * @return shared pool
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    dataSource = createPool();
                }
            }
        }
        return dataSource;
    }

    private static HikariDataSource createPool() {
        var config = new HikariConfig();
        var jdbcUrl = System.getenv(JDBC_DATABASE_URL);
        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            config.setJdbcUrl(jdbcUrl.trim());
        } else {
            config.setJdbcUrl(H2_IN_MEMORY_PROJECT);
        }
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }
}
