package hexlet.code.web;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Session flash messages for redirects.
 */
public final class Flash {

    public static final String MESSAGE_KEY = "flash";

    public static final String TYPE_KEY = "flashType";

    private Flash() {
    }

    /**
     * @param ctx     request context
     * @param message text shown on the next page
     * @param type    Bootstrap alert suffix, e.g. {@code success} or {@code danger}
     */
    public static void set(Context ctx, String message, String type) {
        ctx.sessionAttribute(MESSAGE_KEY, message);
        ctx.sessionAttribute(TYPE_KEY, type);
    }

    /**
     * @param ctx request context
     * @return flash message and type for template rendering (values are removed from session)
     */
    public static Map<String, String> consume(Context ctx) {
        String message = ctx.sessionAttribute(MESSAGE_KEY);
        String type = ctx.sessionAttribute(TYPE_KEY);
        ctx.sessionAttribute(MESSAGE_KEY, null);
        ctx.sessionAttribute(TYPE_KEY, null);

        var result = new HashMap<String, String>();
        result.put("flash", message != null ? message : "");
        result.put("flashType", type != null ? type : "info");
        return result;
    }
}
