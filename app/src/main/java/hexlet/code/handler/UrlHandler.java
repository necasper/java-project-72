package hexlet.code.handler;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.UrlNormalizer;
import hexlet.code.web.Flash;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class UrlHandler {

    private static final String MSG_INVALID_URL = "Некорректный URL";

    private static final String MSG_ALREADY_EXISTS = "Страница уже существует";

    private static final String MSG_CREATED = "Страница успешно добавлена";

    private final UrlRepository urlRepository;

    public UrlHandler(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public void showIndex(Context ctx) {
        ctx.render("pages/index.jte", viewModel(ctx));
    }

    public void showAll(Context ctx) {
        try {
            var model = viewModel(ctx);
            model.put("urls", urlRepository.findAll());
            ctx.render("pages/urls/index.jte", model);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void showOne(Context ctx) {
        try {
            long id = parseId(ctx.pathParam("id"));
            var url = urlRepository.findById(id).orElseThrow(NotFoundResponse::new);
            var model = viewModel(ctx);
            model.put("url", url);
            ctx.render("pages/urls/show.jte", model);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void create(Context ctx) {
        String normalized = UrlNormalizer.normalize(ctx.formParam("url"));
        if (normalized == null) {
            var model = viewModel(ctx);
            model.put("flash", MSG_INVALID_URL);
            model.put("flashType", "danger");
            ctx.status(422);
            ctx.render("pages/index.jte", model);
            return;
        }

        try {
            var existing = urlRepository.findByName(normalized);
            if (existing.isPresent()) {
                redirectToUrl(ctx, existing.get().getId(), MSG_ALREADY_EXISTS, "info");
                return;
            }

            var url = new Url();
            url.setName(normalized);
            urlRepository.save(url);
            redirectToUrl(ctx, url.getId(), MSG_CREATED, "success");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void redirectToUrl(Context ctx, long id, String message, String type) {
        Flash.set(ctx, message, type);
        ctx.redirect("/urls/" + id);
    }

    private static Map<String, Object> viewModel(Context ctx) {
        var model = new HashMap<String, Object>();
        var flash = Flash.consume(ctx);
        model.put("flash", flash.get("flash"));
        model.put("flashType", flash.get("flashType"));
        return model;
    }

    private static long parseId(String rawId) {
        try {
            return Long.parseLong(rawId);
        } catch (NumberFormatException e) {
            throw new NotFoundResponse();
        }
    }
}
