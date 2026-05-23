package hexlet.code.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public final class HtmlParser {

    private HtmlParser() {
    }

    public static String extractTitle(String html) {
        return textOrEmpty(parse(html).title());
    }

    public static String extractH1(String html) {
        Element h1 = parse(html).selectFirst("h1");
        return h1 == null ? "" : textOrEmpty(h1.text());
    }

    public static String extractDescription(String html) {
        Element meta = parse(html).selectFirst("meta[name=description]");
        return meta == null ? "" : textOrEmpty(meta.attr("content"));
    }

    private static Document parse(String html) {
        if (html == null || html.isBlank()) {
            return Jsoup.parse("");
        }
        return Jsoup.parse(html);
    }

    private static String textOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
