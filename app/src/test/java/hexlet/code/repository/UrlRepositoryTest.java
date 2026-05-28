package hexlet.code.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.database.SchemaInitializer;
import hexlet.code.model.Url;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlRepositoryTest {

    private HikariDataSource dataSource;

    private UrlRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:urlRepositoryTest_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
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
    void saveAssignsIdAndTimestamp() throws Exception {
        var url = new Url("https://example.com");

        var saved = repository.save(url);

        assertNotNull(saved.getId());
        assertEquals("https://example.com", saved.getName());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void findByNameReturnsSavedUrl() throws Exception {
        var url = new Url("https://example.org");
        repository.save(url);

        var found = repository.findByName("https://example.org");

        assertTrue(found.isPresent());
        assertEquals(url.getId(), found.get().getId());
    }

    @Test
    void findAllReturnsUrlsOrderedByCreatedAtDesc() throws Exception {
        repository.save(new Url("https://older.com"));
        Thread.sleep(5);
        var newer = repository.save(new Url("https://newer.com"));

        var urls = repository.findAll();

        assertEquals(newer.getId(), urls.get(0).getId());
    }
}
