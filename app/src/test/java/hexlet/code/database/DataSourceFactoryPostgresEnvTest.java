package hexlet.code.database;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DataSourceFactoryPostgresEnvTest {

    @Test
    void getDataSourceUsesPostgresDriverForPostgresUrl() {
        DataSource dataSource = DataSourceFactory.getDataSource();

        assertInstanceOf(HikariDataSource.class, dataSource);
        assertEquals("org.postgresql.Driver", ((HikariDataSource) dataSource).getDriverClassName());
    }
}
