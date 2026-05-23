package hexlet.code.template;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexTemplateTest {

    @Test
    void rendersIndexPageWithBootstrapAndForm() {
        var classLoader = IndexTemplateTest.class.getClassLoader();
        var resolver = new Utf8ResourceCodeResolver("templates", classLoader);
        var engine = TemplateEngine.create(resolver, ContentType.Html);
        var output = new StringOutput();
        engine.render("pages/index.jte", Map.of("flash", ""), output);
        String html = output.toString();

        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("bootstrap"));
        assertTrue(html.contains("Анализатор страниц"));
        assertTrue(html.contains("action=\"/urls\""));
        assertTrue(html.contains("name=\"url\""));
        assertTrue(html.contains("Проверить"));
        assertFalse(html.contains("@template.layout"), "template source must not leak to output");
    }
}
