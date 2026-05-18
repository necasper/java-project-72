package hexlet.code.repository;

import hexlet.code.model.Url;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Persistence layer for {@link Url} entities (table {@code urls}).
 */
public final class UrlRepository extends BaseRepository {

    /**
     * @param dataSource Hikari (or other) {@link DataSource}
     */
    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Inserts a new URL; {@code created_at} defaults to DB {@code CURRENT_TIMESTAMP} when {@code null}.
     *
     * @param url model with {@code name} set; {@code id} is ignored
     * @return the same instance with {@code id} and {@code createdAt} populated from the database
     * @throws SQLException on JDBC errors
     */
    public Url save(Url url) throws SQLException {
        var sql = url.getCreatedAt() == null
                ? "INSERT INTO urls (name) VALUES (?)"
                : "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var ps = url.getCreatedAt() == null
                     ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                     : conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, url.getName());
            if (url.getCreatedAt() != null) {
                ps.setTimestamp(2, Timestamp.valueOf(url.getCreatedAt()));
            }
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    url.setId(keys.getLong(1));
                }
            }
        }
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement("SELECT created_at FROM urls WHERE id = ?")) {
            ps.setLong(1, url.getId());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("created_at");
                    url.setCreatedAt(ts.toLocalDateTime());
                }
            }
        }
        return url;
    }
}
