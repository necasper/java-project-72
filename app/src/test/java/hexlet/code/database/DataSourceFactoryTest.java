package hexlet.code.database;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSourceFactoryTest {

    @Test
    void getDataSourceReturnsWorkingConnection() throws Exception {
        DataSource dataSource = DataSourceFactory.getDataSource();

        try (var connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertTrue(connection.isValid(1));
        }
    }
}
