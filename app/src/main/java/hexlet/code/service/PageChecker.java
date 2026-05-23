package hexlet.code.service;

import hexlet.code.util.HtmlParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

public final class PageChecker {

    public PageCheckResult check(String url) {
        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            int statusCode = response.getStatus();
            if (statusCode >= 400) {
                return PageCheckResult.failure();
            }
            String body = response.getBody() != null ? response.getBody() : "";
            return PageCheckResult.success(
                    statusCode,
                    HtmlParser.extractH1(body),
                    HtmlParser.extractTitle(body),
                    HtmlParser.extractDescription(body)
            );
        } catch (UnirestException e) {
            return PageCheckResult.failure();
        }
    }
}
