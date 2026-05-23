package hexlet.code.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlParserTest {

    @Test
    void extractsTitleH1AndDescription() {
        String html = """
                <html>
                <head>
                    <title>Awesome page</title>
                    <meta name="description" content="Statements of great people">
                </head>
                <body>
                    <h1>Do not expect a miracle, miracles yourself!</h1>
                </body>
                </html>
                """;

        assertEquals("Awesome page", HtmlParser.extractTitle(html));
        assertEquals("Do not expect a miracle, miracles yourself!", HtmlParser.extractH1(html));
        assertEquals("Statements of great people", HtmlParser.extractDescription(html));
    }
}
