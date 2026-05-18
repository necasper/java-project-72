package hexlet.code;

import hexlet.code.database.DataSourceFactory;
import hexlet.code.database.SchemaInitializer;
import io.javalin.Javalin;

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
        try {
            var dataSource = DataSourceFactory.getDataSource();
            SchemaInitializer.apply(dataSource);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }

        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        });
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
