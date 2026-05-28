package hexlet.code.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

public final class UrlNormalizer {

    private UrlNormalizer() {
    }

    public static Optional<String> normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            URI uri = new URI(raw.trim());
            URL url = uri.toURL();
            String protocol = url.getProtocol();
            String host = url.getHost();
            if (protocol == null || protocol.isBlank() || host == null || host.isBlank()) {
                return Optional.empty();
            }
            int port = url.getPort();
            if (port == -1) {
                return Optional.of(protocol + "://" + host);
            }
            return Optional.of(protocol + "://" + host + ":" + port);
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
