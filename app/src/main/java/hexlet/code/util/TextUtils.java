package hexlet.code.util;

public final class TextUtils {

    public static final int MAX_PREVIEW_LENGTH = 200;

    private TextUtils() {
    }

    public static String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }
}
