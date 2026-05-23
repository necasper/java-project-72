package hexlet.code.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for {@link UrlNormalizer}.
 */
class UrlNormalizerTest {

    @Test
    void normalizeExtractsProtocolHostAndPort() {
        assertEquals("https://some-domain.org",
                UrlNormalizer.normalize("https://some-domain.org/example/path"));
        assertEquals("https://some-domain.org:8080",
                UrlNormalizer.normalize("https://some-domain.org:8080/example/path"));
    }

    @Test
    void normalizeReturnsNullForInvalidInput() {
        assertNull(UrlNormalizer.normalize("not-a-url"));
        assertNull(UrlNormalizer.normalize(""));
        assertNull(UrlNormalizer.normalize(null));
    }
}
