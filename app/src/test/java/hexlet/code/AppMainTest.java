package hexlet.code;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Covers {@link App#main(String[])} startup path.
 */
class AppMainTest {

    @Test
    void mainStartsServerWithoutThrowing() {
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }
}
