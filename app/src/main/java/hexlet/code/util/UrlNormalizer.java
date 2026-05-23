package hexlet.code.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Normalizes user input to {@code protocol://host[:port]} (domain with protocol and optional port).
 */
public final class UrlNormalizer {

    private UrlNormalizer() {
    }

    /**
     * @param raw user-entered address
     * @return normalized URL or {@code null} when input is invalid
     */
    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            URI uri = new URI(raw.trim());
            URL url = uri.toURL();
            String protocol = url.getProtocol();
            String host = url.getHost();
            if (protocol == null || protocol.isBlank() || host == null || host.isBlank()) {
                return null;
            }
            int port = url.getPort();
            if (port == -1) {
                return protocol + "://" + host;
            }
            return protocol + "://" + host + ":" + port;
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return null;
        }
    }
}
