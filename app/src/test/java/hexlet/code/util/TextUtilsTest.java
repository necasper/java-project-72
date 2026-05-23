package hexlet.code.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextUtilsTest {

    @Test
    void truncateReturnsOriginalTextWhenShortEnough() {
        assertEquals("short", TextUtils.truncate("short", TextUtils.MAX_PREVIEW_LENGTH));
    }

    @Test
    void truncateAddsEllipsisWhenTextIsTooLong() {
        String longText = "a".repeat(TextUtils.MAX_PREVIEW_LENGTH + 1);
        String expected = "a".repeat(TextUtils.MAX_PREVIEW_LENGTH) + "...";

        assertEquals(expected, TextUtils.truncate(longText, TextUtils.MAX_PREVIEW_LENGTH));
    }
}
