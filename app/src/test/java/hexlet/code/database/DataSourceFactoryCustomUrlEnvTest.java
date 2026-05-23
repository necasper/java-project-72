package hexlet.code.database;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSourceFactoryCustomUrlEnvTest {

    @Test
    void getDataSourceUsesJdbcUrlFromEnvironment() {
        DataSource dataSource = DataSourceFactory.getDataSource();

        assertInstanceOf(HikariDataSource.class, dataSource);
        assertTrue(((HikariDataSource) dataSource).getJdbcUrl().contains("fromEnv"));
    }
}
