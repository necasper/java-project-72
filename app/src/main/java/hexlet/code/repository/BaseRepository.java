package hexlet.code.repository;

import javax.sql.DataSource;

/**
 * Shared data access base class that keeps a JDBC {@link DataSource}.
 */
public abstract class BaseRepository {

    /**
     * Pool / JDBC data source for subclasses.
     */
    protected final DataSource dataSource;

    /**
     * @param dataSource connection source (e.g. HikariCP)
     */
    protected BaseRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
