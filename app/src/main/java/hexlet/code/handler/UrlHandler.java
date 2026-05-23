package hexlet.code.handler;

import hexlet.code.dto.UrlView;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.service.PageChecker;
import hexlet.code.service.PageCheckResult;
import hexlet.code.util.UrlNormalizer;
import hexlet.code.web.Flash;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class UrlHandler {

    private static final String MSG_INVALID_URL = "Некорректный URL";

    private static final String MSG_ALREADY_EXISTS = "Страница уже существует";

    private static final String MSG_CREATED = "Страница успешно добавлена";

    private static final String MSG_CHECK_SUCCESS = "Страница успешно проверена";

    private static final String MSG_CHECK_ERROR = "Произошла ошибка при проверке";

    private final UrlRepository urlRepository;

    private final UrlCheckRepository urlCheckRepository;

    private final PageChecker pageChecker;

    public UrlHandler(UrlRepository urlRepository, UrlCheckRepository urlCheckRepository, PageChecker pageChecker) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
        this.pageChecker = pageChecker;
    }

    public void showIndex(Context ctx) {
        ctx.render("pages/index.jte", viewModel(ctx));
    }

    public void showAll(Context ctx) {
        try {
            var model = viewModel(ctx);
            var views = new ArrayList<UrlView>();
            for (Url url : urlRepository.findAll()) {
                var lastCheck = urlCheckRepository.findLatestByUrlId(url.getId()).orElse(null);
                views.add(new UrlView(url, lastCheck));
            }
            model.put("urls", views);
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
            model.put("checks", urlCheckRepository.findByUrlId(id));
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

    public void createCheck(Context ctx) {
        try {
            long id = parseId(ctx.pathParam("id"));
            var url = urlRepository.findById(id).orElseThrow(NotFoundResponse::new);
            PageCheckResult result = pageChecker.check(url.getName());
            if (!result.isSuccess()) {
                Flash.set(ctx, MSG_CHECK_ERROR, "danger");
                ctx.redirect("/urls/" + id);
                return;
            }

            var check = new UrlCheck();
            check.setUrlId(id);
            check.setStatusCode(result.getStatusCode());
            check.setH1(result.getH1());
            check.setTitle(result.getTitle());
            check.setDescription(result.getDescription());
            urlCheckRepository.save(check);

            Flash.set(ctx, MSG_CHECK_SUCCESS, "success");
            ctx.redirect("/urls/" + id);
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
