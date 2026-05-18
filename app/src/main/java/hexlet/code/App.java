package hexlet.code;

import io.javalin.Javalin;

public final class App {

    public static Javalin getApp() {
        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.routes.get("/", ctx -> ctx.result("Hello World"));
        });
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7070"));
        app.start(port);
    }
}
