package hexlet.code.database;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaInitializer {

    private static final String SCHEMA_RESOURCE = "/schema.sql";

    private SchemaInitializer() {
    }

    public static void apply(DataSource dataSource) throws SQLException {
        String ddl = readSchemaResource();
        try (var conn = dataSource.getConnection();
             Statement statement = conn.createStatement()) {
            statement.execute(ddl);
        }
    }

    private static String readSchemaResource() {
        try (InputStream input = SchemaInitializer.class.getResourceAsStream(SCHEMA_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException("Classpath resource not found: " + SCHEMA_RESOURCE);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + SCHEMA_RESOURCE, e);
        }
    }
}
