package hexlet.code;

import io.javalin.Javalin;

public final class App {

    private static final String PORT_ENV = "PORT";

    private static final String DEFAULT_PORT = "7070";

    public static Javalin getApp() {
        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        });
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        int port = Integer.parseInt(System.getenv().getOrDefault(PORT_ENV, DEFAULT_PORT));
        app.start(port);
    }
}
