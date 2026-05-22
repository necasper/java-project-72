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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link App#getApp()} HTTP routes.
 */
class AppTest {

    private Javalin app;

    @BeforeEach
    void startApp() {
        app = App.getApp();
        app.start(0);
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
        var client = HttpClient.newHttpClient();
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
        assertFalse(body.contains("@template.layout"));
    }
}
