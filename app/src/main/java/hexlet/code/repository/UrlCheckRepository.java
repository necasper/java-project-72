package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UrlCheckRepository extends BaseRepository {

    private static final String SELECT_COLUMNS =
            "SELECT id, url_id, status_code, h1, title, description, created_at FROM url_checks";

    public UrlCheckRepository(DataSource dataSource) {
        super(dataSource);
    }

    public List<UrlCheck> findByUrlId(long urlId) throws SQLException {
        var sql = SELECT_COLUMNS + " WHERE url_id = ? ORDER BY created_at DESC";
        var checks = new ArrayList<UrlCheck>();
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, urlId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    checks.add(mapRow(rs));
                }
            }
        }
        return checks;
    }

    public Optional<UrlCheck> findLatestByUrlId(long urlId) throws SQLException {
        var sql = SELECT_COLUMNS + " WHERE url_id = ? ORDER BY created_at DESC LIMIT 1";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, urlId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public UrlCheck save(UrlCheck check) throws SQLException {
        var sql = """
                INSERT INTO url_checks (url_id, status_code, h1, title, description)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, check.getUrlId());
            ps.setInt(2, check.getStatusCode());
            ps.setString(3, check.getH1());
            ps.setString(4, check.getTitle());
            ps.setString(5, check.getDescription());
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    check.setId(keys.getLong(1));
                }
            }
        }
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement("SELECT created_at FROM url_checks WHERE id = ?")) {
            ps.setLong(1, check.getId());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    check.setCreatedAt(createdAt.toLocalDateTime());
                }
            }
        }
        return check;
    }

    public int countByUrlId(long urlId) throws SQLException {
        var sql = "SELECT COUNT(*) FROM url_checks WHERE url_id = ?";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, urlId);
            try (var rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static UrlCheck mapRow(ResultSet rs) throws SQLException {
        var check = new UrlCheck();
        check.setId(rs.getLong("id"));
        check.setUrlId(rs.getLong("url_id"));
        check.setStatusCode(rs.getInt("status_code"));
        check.setH1(rs.getString("h1"));
        check.setTitle(rs.getString("title"));
        check.setDescription(rs.getString("description"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        check.setCreatedAt(createdAt.toLocalDateTime());
        return check;
    }
}
