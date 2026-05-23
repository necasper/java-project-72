package hexlet.code.repository;

import hexlet.code.model.Url;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class UrlRepository extends BaseRepository {

    private static final String SELECT_COLUMNS = "SELECT id, name, created_at FROM urls";

    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    public List<Url> findAll() throws SQLException {
        var sql = SELECT_COLUMNS + " ORDER BY created_at DESC";
        var urls = new ArrayList<Url>();
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                urls.add(mapRow(rs));
            }
        }
        return urls;
    }

    public Optional<Url> findById(long id) throws SQLException {
        var sql = SELECT_COLUMNS + " WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Url> findByName(String name) throws SQLException {
        var sql = SELECT_COLUMNS + " WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Url save(Url url) throws SQLException {
        var sql = url.getCreatedAt() == null
                ? "INSERT INTO urls (name) VALUES (?)"
                : "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

    private static Url mapRow(ResultSet rs) throws SQLException {
        var url = new Url();
        url.setId(rs.getLong("id"));
        url.setName(rs.getString("name"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        url.setCreatedAt(createdAt.toLocalDateTime());
        return url;
    }
}
