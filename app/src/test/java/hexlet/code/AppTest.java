package hexlet.code;

import io.javalin.Javalin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link App#getApp()} HTTP routes.
 */
class AppTest {

    private static final Pattern URLS_PATH = Pattern.compile("/urls/\\d+");

    private Javalin app;

    private HttpClient client;

    @BeforeEach
    void startApp() {
        app = App.getApp();
        app.start(0);
        client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .cookieHandler(new java.net.CookieManager())
                .build();
    }

    @AfterEach
    void stopApp() {
        if (app != null) {
            app.stop();
        }
    }

    @Test
    void appCanBeInstantiated() {
        assertNotNull(new App());
    }

    @Test
    void getRootReturnsRenderedHtml() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + app.port() + "/"))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        String body = response.body();
        assertTrue(body.contains("Анализатор страниц"));
        assertTrue(body.contains("bootstrap"));
        assertTrue(body.contains("method=\"post\""));
        assertTrue(body.contains("action=\"/urls\""));
        assertTrue(body.contains("name=\"url\""));
        assertFalse(body.contains("@template.layout"));
    }

    @Test
    void postUrlsCreatesSiteAndRedirectsToShowPage() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + app.port() + "/urls"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("url=https://example.com/page"))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        assertTrue(URLS_PATH.matcher(response.uri().getPath()).find());
        assertTrue(response.body().contains("Страница успешно добавлена"));
        assertTrue(response.body().contains("data-test=\"url\""));
        assertTrue(response.body().contains("https://example.com"));
    }

    @Test
    void postUrlsWithInvalidAddressReturns422() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + app.port() + "/urls"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("url=invalid"))
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(422, response.statusCode());
        assertTrue(response.body().contains("Некорректный URL"));
        assertTrue(response.body().contains("method=\"post\""));
    }

    @Test
    void postUrlsWithExistingSiteShowsAlreadyExistsMessage() throws Exception {
        var createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + app.port() + "/urls"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("url=https://hexlet.io/courses"))
                .build();
        client.send(createRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        var duplicateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + app.port() + "/urls"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("url=https://hexlet.io/other"))
                .build();
        var response = client.send(duplicateRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Страница уже существует"));
        assertTrue(response.body().contains("https://hexlet.io"));
    }

    @Test
    void getUrlsReturnsListPage() throws Exception {
        var createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + app.port() + "/urls"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("url=https://list.example.com/path"))
                .build();
        client.send(createRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + app.port() + "/urls"))
                .GET()
                .build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("data-test=\"urls\""));
        assertTrue(response.body().contains("https://list.example.com"));
        assertTrue(response.body().contains("Дата создания"));
    }
}
