package hexlet.code.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaInitializerTest {

    @Test
    void schemaSqlCreatesUrlsTableIdempotently() throws Exception {
        var cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:h2:mem:schemaTest;DB_CLOSE_DELAY=-1");
        cfg.setMaximumPoolSize(1);
        try (HikariDataSource ds = new HikariDataSource(cfg)) {
            SchemaInitializer.apply(ds);
            assertTrue(urlsTableExists(ds), "urls table missing after first apply");
            SchemaInitializer.apply(ds);
            assertTrue(urlsTableExists(ds), "urls table missing after second apply");
        }
    }

    @Test
    void applyPropagatesSqlExceptionWhenConnectionFails() throws Exception {
        var cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:h2:mem:schemaFailureTest;DB_CLOSE_DELAY=-1");
        cfg.setMaximumPoolSize(1);
        try (HikariDataSource ds = new HikariDataSource(cfg)) {
            ds.close();
            assertThrows(SQLException.class, () -> SchemaInitializer.apply(ds));
        }
    }

    private static boolean urlsTableExists(HikariDataSource ds) throws Exception {
        try (var conn = ds.getConnection();
             ResultSet rs = conn.getMetaData().getTables(null, null, "URLS", null)) {
            return rs.next();
        }
    }
}
