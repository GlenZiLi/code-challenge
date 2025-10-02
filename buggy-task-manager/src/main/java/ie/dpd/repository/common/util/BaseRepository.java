package ie.dpd.repository.common.util;

import jakarta.annotation.Resource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author ZI LI
 * @Date 2025/10/2 8:32
 * @comment we could do more extent in the furture
 */
public class BaseRepository {
    @Resource(name = "TaskDB")
    protected DataSource dataSource;

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
