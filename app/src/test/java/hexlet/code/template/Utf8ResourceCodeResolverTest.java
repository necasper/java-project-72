package hexlet.code.template;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertNull(resolver.resolve("missing.jte"));
    }

    @Test
    void emptyRootResolvesTemplatesFromClasspathRoot() {
        var resolver = new Utf8ResourceCodeResolver("", getClass().getClassLoader());
        assertTrue(resolver.exists("schema.sql"));
    }

    @Test
    void nullClassLoaderUsesContextClassLoader() {
        var resolver = new Utf8ResourceCodeResolver("templates", null);
        assertTrue(resolver.exists(TEMPLATE));
    }

    @Test
    void getLastModifiedReturnsValueForExistingTemplate() {
        var resolver = new Utf8ResourceCodeResolver("templates", getClass().getClassLoader());
        assertTrue(resolver.getLastModified(TEMPLATE) >= 0);
    }

    @Test
    void resolveWrapsIOExceptionWhenStreamFails() {
        var classLoader = new ClassLoader(getClass().getClassLoader()) {
            @Override
            public InputStream getResourceAsStream(String name) {
                if ("templates/broken.jte".equals(name)) {
                    return new InputStream() {
                        @Override
                        public int read() throws IOException {
                            throw new IOException("read failed");
                        }
                    };
                }
                return super.getResourceAsStream(name);
            }
        };
        var resolver = new Utf8ResourceCodeResolver("templates", classLoader);

        assertThrows(UncheckedIOException.class, () -> resolver.resolve("broken.jte"));
    }

    @Test
    void getLastModifiedReturnsZeroWhenResourceUriIsInvalid() throws Exception {
        var classLoader = new ClassLoader(getClass().getClassLoader()) {
            @Override
            public URL getResource(String name) {
                if ("templates/invalid-uri.jte".equals(name)) {
                    try {
                        return new URL("http://example.com/template.jte");
                    } catch (java.net.MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
                return super.getResource(name);
            }
        };
        var resolver = new Utf8ResourceCodeResolver("templates", classLoader);

        assertEquals(0, resolver.getLastModified("invalid-uri.jte"));
    }
}
