package hexlet.code;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.database.DataSourceFactory;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies {@link App#getApp()} wraps database errors; runs in an isolated JVM (see Gradle).
 */
class AppDatabaseFailureTest {

    @Test
    void getAppWrapsSqlExceptionWhenPoolIsClosed() {
        var pool = (HikariDataSource) DataSourceFactory.getDataSource();
        pool.close();

        var exception = assertThrows(RuntimeException.class, App::getApp);

        assertEquals("Database initialization failed", exception.getMessage());
        assertInstanceOf(SQLException.class, exception.getCause());
    }
}
