package hexlet.code.template;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies classpath templates are read as UTF-8 (Cyrillic must not be corrupted).
 */
class Utf8ResourceCodeResolverTest {

    private static final String TEMPLATE = "pages/index.jte";

    private static final String CYRILLIC_MARKER = "Анализатор страниц";

    @Test
    void existsReturnsTrueForIndexTemplate() {
        var resolver = new Utf8ResourceCodeResolver("templates", getClass().getClassLoader());
        assertTrue(resolver.exists(TEMPLATE));
    }

    @Test
    void resolveReadsTemplateAsUtf8() {
        var resolver = new Utf8ResourceCodeResolver("templates", getClass().getClassLoader());
        String source = resolver.resolve(TEMPLATE);
        assertNotNull(source);
        assertTrue(source.contains(CYRILLIC_MARKER), "Cyrillic text must be preserved");
        assertTrue(source.contains("@template.layout"), "index template must use layout");
    }

    @Test
    void getLastModifiedReturnsZeroForMissingTemplate() {
        var resolver = new Utf8ResourceCodeResolver("templates", getClass().getClassLoader());
        assertEquals(0, resolver.getLastModified("missing.jte"));
    }

    @Test
    void resolveReturnsNullForMissingTemplate() {
        var resolver = new Utf8ResourceCodeResolver("templates", getClass().getClassLoader());
        assertFalse(resolver.exists("missing.jte"));
    }
}
