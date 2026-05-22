package hexlet.code.template;

import gg.jte.CodeResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Loads JTE templates from the classpath as UTF-8 (platform default charset breaks Cyrillic on Windows).
 */
public final class Utf8ResourceCodeResolver implements CodeResolver {

    private final String root;

    private final ClassLoader classLoader;

    /**
     * @param root         resources root, e.g. {@code templates}
     * @param classLoader  class loader for resources (may be {@code null} for context loader)
     */
    public Utf8ResourceCodeResolver(String root, ClassLoader classLoader) {
        if (root.isEmpty()) {
            this.root = "";
        } else {
            this.root = root + "/";
        }
        this.classLoader = classLoader != null
                ? classLoader
                : Thread.currentThread().getContextClassLoader();
    }

    @Override
    public String resolve(String name) {
        try (InputStream input = classLoader.getResourceAsStream(root + name)) {
            if (input == null) {
                return null;
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean exists(String name) {
        return classLoader.getResource(root + name) != null;
    }

    @Override
    public long getLastModified(String name) {
        URL resource = classLoader.getResource(root + name);
        if (resource == null) {
            return 0;
        }
        try {
            return new File(resource.toURI()).lastModified();
        } catch (IllegalArgumentException | URISyntaxException e) {
            return 0;
        }
    }
}
