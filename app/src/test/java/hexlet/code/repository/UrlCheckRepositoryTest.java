package hexlet.code.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.database.SchemaInitializer;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlCheckRepositoryTest {

    private HikariDataSource dataSource;

    private UrlRepository urlRepository;

    private UrlCheckRepository urlCheckRepository;

    @BeforeEach
    void setUp() throws Exception {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:urlCheckRepositoryTest_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(1);
        dataSource = new HikariDataSource(config);
        SchemaInitializer.apply(dataSource);
        urlRepository = new UrlRepository(dataSource);
        urlCheckRepository = new UrlCheckRepository(dataSource);
    }

    @AfterEach
    void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void saveAndFindByUrlId() throws Exception {
        var url = new Url("https://example.com");
        urlRepository.save(url);

        var check = new UrlCheck();
        check.setUrlId(url.getId());
        check.setStatusCode(200);
        check.setH1("Heading");
        check.setTitle("Title");
        check.setDescription("Description");
        urlCheckRepository.save(check);

        var checks = urlCheckRepository.findByUrlId(url.getId());

        assertEquals(1, checks.size());
        assertNotNull(checks.get(0).getId());
        assertEquals(200, checks.get(0).getStatusCode());
        assertNotNull(checks.get(0).getCreatedAt());
    }

    @Test
    void findLatestChecksReturnsLatestCheckPerUrl() throws Exception {
        var firstUrl = new Url("https://first.example.com");
        urlRepository.save(firstUrl);
        var secondUrl = new Url("https://second.example.com");
        urlRepository.save(secondUrl);

        var firstCheck = new UrlCheck();
        firstCheck.setUrlId(firstUrl.getId());
        firstCheck.setStatusCode(200);
        firstCheck.setH1("First");
        firstCheck.setTitle("First");
        firstCheck.setDescription("First");
        urlCheckRepository.save(firstCheck);

        var secondCheck = new UrlCheck();
        secondCheck.setUrlId(secondUrl.getId());
        secondCheck.setStatusCode(404);
        secondCheck.setH1("Second");
        secondCheck.setTitle("Second");
        secondCheck.setDescription("Second");
        urlCheckRepository.save(secondCheck);

        var latest = urlCheckRepository.findLatestChecks();

        assertEquals(2, latest.size());
        assertEquals(firstCheck.getId(), latest.get(firstUrl.getId()).getId());
        assertEquals(secondCheck.getId(), latest.get(secondUrl.getId()).getId());
    }

    @Test
    void findLatestByUrlIdReturnsMostRecentCheck() throws Exception {
        var url = new Url("https://hexlet.io");
        urlRepository.save(url);

        var first = new UrlCheck();
        first.setUrlId(url.getId());
        first.setStatusCode(200);
        first.setH1("First");
        first.setTitle("First");
        first.setDescription("First");
        urlCheckRepository.save(first);

        var second = new UrlCheck();
        second.setUrlId(url.getId());
        second.setStatusCode(201);
        second.setH1("Second");
        second.setTitle("Second");
        second.setDescription("Second");
        urlCheckRepository.save(second);

        var latest = urlCheckRepository.findLatestByUrlId(url.getId());

        assertTrue(latest.isPresent());
        assertEquals(second.getId(), latest.get().getId());
        assertEquals(201, latest.get().getStatusCode());
    }
}
