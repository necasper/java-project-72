package hexlet.code.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UrlTest {

    @Test
    void defaultConstructorCreatesEmptyUrl() {
        var url = new Url();
        assertNull(url.getId());
        assertNull(url.getName());
        assertNull(url.getCreatedAt());
    }

    @Test
    void nameConstructorSetsName() {
        var url = new Url("https://example.com");

        assertEquals("https://example.com", url.getName());
        assertNull(url.getId());
        assertNull(url.getCreatedAt());
    }

    @Test
    void settersUpdateFields() {
        var url = new Url();
        var createdAt = LocalDateTime.of(2024, 6, 15, 9, 30);

        url.setId(42L);
        url.setName("https://hexlet.io");
        url.setCreatedAt(createdAt);

        assertEquals(42L, url.getId());
        assertEquals("https://hexlet.io", url.getName());
        assertEquals(createdAt, url.getCreatedAt());
    }
}
