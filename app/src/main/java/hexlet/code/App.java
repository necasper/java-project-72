package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import hexlet.code.database.SchemaInitializer;
import hexlet.code.handler.UrlHandler;
import hexlet.code.repository.UrlRepository;
import hexlet.code.template.Utf8ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import javax.sql.DataSource;
import java.sql.SQLException;

public final class App {

    private static final String PORT_ENV = "PORT";

    private static final String DEFAULT_PORT = "7070";

    /**
     * Builds a Javalin application with routes and database schema applied.
     *
     * @return configured app instance
     */
    public static Javalin getApp() {
        return buildApp(hexlet.code.database.DataSourceFactory.getDataSource());
    }

    public static Javalin buildApp(DataSource dataSource) {
        try {
            SchemaInitializer.apply(dataSource);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }

        var urlHandler = new UrlHandler(new UrlRepository(dataSource));

        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
            config.routes.get("/", urlHandler::showIndex);
            config.routes.get("/urls", urlHandler::showAll);
            config.routes.get("/urls/{id}", urlHandler::showOne);
            config.routes.post("/urls", urlHandler::create);
        });
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        var codeResolver = new Utf8ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    /**
     * Starts the HTTP server on {@code PORT} or {@link #DEFAULT_PORT}.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Javalin app = getApp();
        int port = Integer.parseInt(System.getenv().getOrDefault(PORT_ENV, DEFAULT_PORT));
        app.start(port);
    }
}
