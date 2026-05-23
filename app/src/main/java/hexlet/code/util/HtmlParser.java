package hexlet.code.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HtmlParser {

    private static final Pattern TITLE = Pattern.compile("<title[^>]*>(.*?)</title>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern H1 = Pattern.compile("<h1[^>]*>(.*?)</h1>",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern DESCRIPTION = Pattern.compile(
            "<meta\\s+name=\"description\"\\s+content=\"([^\"]*)\"",
            Pattern.CASE_INSENSITIVE);

    private HtmlParser() {
    }

    public static String extractTitle(String html) {
        return extractFirstGroup(html, TITLE);
    }

    public static String extractH1(String html) {
        return stripTags(extractFirstGroup(html, H1));
    }

    public static String extractDescription(String html) {
        return extractFirstGroup(html, DESCRIPTION);
    }

    private static String extractFirstGroup(String html, Pattern pattern) {
        if (html == null || html.isBlank()) {
            return "";
        }
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return stripTags(matcher.group(1)).trim();
        }
        return "";
    }

    private static String stripTags(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("<[^>]+>", "").trim();
    }
}
