package hexlet.code.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.database.SchemaInitializer;
import hexlet.code.model.Url;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for {@link UrlRepository#save(Url)}.
 */
class UrlRepositoryTest {

    private HikariDataSource dataSource;

    private UrlRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:urlRepositoryTest;DB_CLOSE_DELAY=-1");
        config.setMaximumPoolSize(1);
        dataSource = new HikariDataSource(config);
        SchemaInitializer.apply(dataSource);
        repository = new UrlRepository(dataSource);
    }

    @AfterEach
    void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void saveWithoutCreatedAtAssignsIdAndTimestamp() throws Exception {
        var url = new Url();
        url.setName("https://example.com");

        var saved = repository.save(url);

        assertNotNull(saved.getId());
        assertEquals("https://example.com", saved.getName());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void saveWithCreatedAtPersistsProvidedTimestamp() throws Exception {
        var createdAt = LocalDateTime.of(2023, 1, 15, 10, 20, 30);
        var url = new Url();
        url.setName("https://hexlet.io");
        url.setCreatedAt(createdAt);

        var saved = repository.save(url);

        assertNotNull(saved.getId());
        assertEquals("https://hexlet.io", saved.getName());
        assertEquals(createdAt, saved.getCreatedAt());
    }
}
