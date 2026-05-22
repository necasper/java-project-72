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

    private static final String JDBC_POSTGRESQL_PREFIX = "jdbc:postgresql:";

    private static final String DRIVER_POSTGRESQL = "org.postgresql.Driver";

    private static final String DRIVER_H2 = "org.h2.Driver";

    private DataSourceFactory() {
    }

    /**
     * Returns the singleton {@link DataSource} (Hikari pool).
     *
     * @return shared pool
     */
    public static DataSource getDataSource() {
        return Holder.INSTANCE;
    }

    private static final class Holder {

        private static final HikariDataSource INSTANCE = createPool();
    }

    private static HikariDataSource createPool() {
        var config = new HikariConfig();
        var jdbcUrl = System.getenv(JDBC_DATABASE_URL);
        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            var url = jdbcUrl.trim();
            config.setJdbcUrl(url);
            if (url.startsWith(JDBC_POSTGRESQL_PREFIX)) {
                config.setDriverClassName(DRIVER_POSTGRESQL);
            }
        } else {
            config.setJdbcUrl(H2_IN_MEMORY_PROJECT);
            config.setDriverClassName(DRIVER_H2);
        }
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }
}
