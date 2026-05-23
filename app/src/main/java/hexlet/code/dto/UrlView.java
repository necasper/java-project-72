package hexlet.code.dto;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

public final class UrlView {

    private final Url url;

    private final UrlCheck lastCheck;

    public UrlView(Url urlValue, UrlCheck lastCheckValue) {
        this.url = urlValue;
        this.lastCheck = lastCheckValue;
    }

    public Url getUrl() {
        return url;
    }

    public UrlCheck getLastCheck() {
        return lastCheck;
    }
}
