package hexlet.code.handler;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.App;
import hexlet.code.database.SchemaInitializer;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlHandlerTest {

    private static final Pattern URL_ID_PATH = Pattern.compile("/urls/(\\d+)");

    private HikariDataSource dataSource;

    private UrlRepository urlRepository;

    private Javalin app;

    private HttpClient client;

    @BeforeEach
    void setUp() throws Exception {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:urlHandlerTest_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(1);
        dataSource = new HikariDataSource(config);
        SchemaInitializer.apply(dataSource);
        urlRepository = new UrlRepository(dataSource);
        app = App.buildApp(dataSource);
        app.start(0);
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .cookieHandler(new java.net.CookieManager())
                .build();
    }

    @AfterEach
    void tearDown() {
        if (app != null) {
            app.stop();
        }
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void showIndexReturnsMainPageWithForm() throws Exception {
        var response = get("/");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Анализатор страниц"));
        assertTrue(response.body().contains("method=\"post\""));
        assertTrue(response.body().contains("action=\"/urls\""));
        assertTrue(response.body().contains("name=\"url\""));
    }

    @Test
    void showAllReturnsEmptyListPage() throws Exception {
        var response = get("/urls");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("data-test=\"urls\""));
        assertTrue(response.body().contains("Дата создания"));
    }

    @Test
    void createAddsUrlToDatabaseAndOpensShowPage() throws Exception {
        var response = post("/urls", "url=https://example.com/page");

        assertEquals(200, response.statusCode());
        long id = extractUrlId(response);
        assertTrue(response.body().contains("Страница успешно добавлена"));
        assertTrue(response.body().contains("data-test=\"url\""));
        assertTrue(response.body().contains("https://example.com"));

        var saved = urlRepository.findById(id);
        assertTrue(saved.isPresent());
        assertEquals("https://example.com", saved.get().getName());
        assertEquals(1, urlRepository.findAll().size());
    }

    @Test
    void createWithExistingUrlOpensSamePageWithoutDuplicate() throws Exception {
        var firstResponse = post("/urls", "url=https://hexlet.io/courses");
        long firstId = extractUrlId(firstResponse);

        var secondResponse = post("/urls", "url=https://hexlet.io/other-path");
        long secondId = extractUrlId(secondResponse);

        assertEquals(firstId, secondId);
        assertEquals(200, secondResponse.statusCode());
        assertTrue(secondResponse.body().contains("Страница уже существует"));
        assertTrue(secondResponse.body().contains("https://hexlet.io"));
        assertEquals(1, urlRepository.findAll().size());
        assertTrue(urlRepository.findByName("https://hexlet.io").isPresent());
    }

    @Test
    void createWithInvalidUrlReturns422AndDoesNotSave() throws Exception {
        var response = post("/urls", "url=not-a-url");

        assertEquals(422, response.statusCode());
        assertTrue(response.body().contains("Некорректный URL"));
        assertTrue(response.body().contains("method=\"post\""));
        assertEquals(0, urlRepository.findAll().size());
    }

    @Test
    void showOneReturnsUrlPageWithChecksFormAndTable() throws Exception {
        var createResponse = post("/urls", "url=https://show.example.com/path");
        long id = extractUrlId(createResponse);

        var response = get("/urls/" + id);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("data-test=\"url\""));
        assertTrue(response.body().contains("https://show.example.com"));
        assertTrue(response.body().contains("method=\"post\""));
        assertTrue(response.body().contains("action=\"/urls/" + id + "/checks\""));
        assertTrue(response.body().contains("type=\"submit\""));
        assertTrue(response.body().contains("value=\"Запустить проверку\""));
        assertTrue(response.body().contains("data-test=\"checks\""));
    }

    @Test
    void showAllListsSavedUrls() throws Exception {
        post("/urls", "url=https://list-a.example.com");
        post("/urls", "url=https://list-b.example.com");

        var response = get("/urls");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("data-test=\"urls\""));
        assertTrue(response.body().contains("https://list-a.example.com"));
        assertTrue(response.body().contains("https://list-b.example.com"));
    }

    @Test
    void showOneReturns404ForMissingUrl() throws Exception {
        var response = get("/urls/999999");

        assertEquals(404, response.statusCode());
    }

    @Test
    void showOneReturns404ForInvalidId() throws Exception {
        var response = get("/urls/not-a-number");

        assertEquals(404, response.statusCode());
    }

    private HttpResponse<String> get(String path) throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(baseUri(path))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> post(String path, String formBody) throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(baseUri(path))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private URI baseUri(String path) {
        return URI.create("http://localhost:" + app.port() + path);
    }

    private static long extractUrlId(HttpResponse<String> response) {
        Matcher matcher = URL_ID_PATH.matcher(response.uri().getPath());
        assertTrue(matcher.find(), "Expected redirect to /urls/{id}, got: " + response.uri());
        return Long.parseLong(matcher.group(1));
    }
}
