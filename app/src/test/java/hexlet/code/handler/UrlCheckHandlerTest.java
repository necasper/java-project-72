package hexlet.code.handler;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.App;
import hexlet.code.database.SchemaInitializer;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.TextUtils;
import io.javalin.Javalin;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlCheckHandlerTest {

    private static final String SUCCESS_HTML = """
            <html>
            <head>
                <title>Awesome page</title>
                <meta name="description" content="Statements of great people">
            </head>
            <body>
                <h1>Do not expect a miracle, miracles yourself!</h1>
            </body>
            </html>
            """;

    private static MockWebServer mockWebServer;

    private HikariDataSource dataSource;

    private UrlRepository urlRepository;

    private UrlCheckRepository urlCheckRepository;

    private Javalin app;

    private HttpClient client;

    @BeforeAll
    static void startMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopMockServer() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
        Unirest.shutDown();
    }

    @BeforeEach
    void setUp() throws Exception {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:urlCheckHandlerTest_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        config.setDriverClassName("org.h2.Driver");
        config.setMaximumPoolSize(1);
        dataSource = new HikariDataSource(config);
        SchemaInitializer.apply(dataSource);
        urlRepository = new UrlRepository(dataSource);
        urlCheckRepository = new UrlCheckRepository(dataSource);
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
    void createCheckSavesResultAndShowsSuccessMessage() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(SUCCESS_HTML)
                .addHeader("Content-Type", "text/html; charset=utf-8"));

        long urlId = createUrl(mockWebServer.url("/").toString());
        var response = post("/urls/" + urlId + "/checks", "");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Страница успешно проверена"));
        assertTrue(response.body().contains("data-test=\"checks\""));
        assertTrue(response.body().contains("Awesome page"));
        assertTrue(response.body().contains("Do not expect a miracle, miracles yourself!"));

        assertEquals(1, urlCheckRepository.countByUrlId(urlId));
        var checks = urlCheckRepository.findByUrlId(urlId);
        var check = checks.get(0);
        assertEquals(200, check.getStatusCode());
        assertEquals("Awesome page", check.getTitle());
        assertEquals("Do not expect a miracle, miracles yourself!", check.getH1());
        assertEquals("Statements of great people", check.getDescription());
    }

    @Test
    void createCheckWithErrorResponseDoesNotSaveCheck() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        long urlId = createUrl(mockWebServer.url("/").toString());
        var response = post("/urls/" + urlId + "/checks", "");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Произошла ошибка при проверке"));
        assertEquals(0, urlCheckRepository.countByUrlId(urlId));
    }

    @Test
    void createCheckShowsTruncatedLongFields() throws Exception {
        String longText = "x".repeat(TextUtils.MAX_PREVIEW_LENGTH + 10);
        String html = """
                <html>
                <head>
                    <title>%s</title>
                    <meta name="description" content="%s">
                </head>
                <body>
                    <h1>%s</h1>
                </body>
                </html>
                """.formatted(longText, longText, longText);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(html)
                .addHeader("Content-Type", "text/html; charset=utf-8"));

        long urlId = createUrl(mockWebServer.url("/").toString());
        var response = post("/urls/" + urlId + "/checks", "");

        assertEquals(200, response.statusCode());
        String expectedPreview = "x".repeat(TextUtils.MAX_PREVIEW_LENGTH) + "...";
        assertTrue(response.body().contains(expectedPreview));
    }

    @Test
    void showAllDisplaysLastCheckDateAndStatusCode() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(SUCCESS_HTML)
                .addHeader("Content-Type", "text/html; charset=utf-8"));

        long urlId = createUrl(mockWebServer.url("/").toString());
        post("/urls/" + urlId + "/checks", "");

        var response = get("/urls");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Последняя проверка"));
        assertTrue(response.body().contains("200"));
    }

    @Test
    void createCheckReturns404ForMissingUrl() throws Exception {
        var response = post("/urls/999999/checks", "");

        assertEquals(404, response.statusCode());
    }

    private long createUrl(String address) throws Exception {
        var url = new Url(address);
        urlRepository.save(url);
        return url.getId();
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
}
