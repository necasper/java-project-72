package hexlet.code.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrlNormalizerTest {

    @Test
    void normalizeExtractsProtocolHostAndPort() {
        assertEquals("https://some-domain.org",
                UrlNormalizer.normalize("https://some-domain.org/example/path").orElseThrow());
        assertEquals("https://some-domain.org:8080",
                UrlNormalizer.normalize("https://some-domain.org:8080/example/path").orElseThrow());
    }

    @Test
    void normalizeReturnsEmptyForInvalidInput() {
        assertTrue(UrlNormalizer.normalize("not-a-url").isEmpty());
        assertTrue(UrlNormalizer.normalize("").isEmpty());
        assertTrue(UrlNormalizer.normalize(null).isEmpty());
    }
}
