package ie.dpd.repository;

import ie.dpd.model.TaskSummary;
import ie.dpd.repository.common.util.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ZI LI
 * @Date 2025/10/2 6:53
 * @comment base on summary
 *  Generate and save a new summary based on current tasks ({@link #generateSummary()})
 *  Retrieve all historical summaries ({@link #findAllSummaries()})
 */
@ApplicationScoped
public class SummaryRepository extends BaseRepository {

    // Generate and save a new task summary
    // Under high concurrency, transactions need to be considered.
    public TaskSummary generateSummary() throws SQLException {
        TaskSummary summary = fetchCurrentStats();
        saveSummary(summary);
        summary.setGeneratedAt(LocalDateTime.now());
        return summary;
    }

    // Calculate current stats from tasks table
    private TaskSummary fetchCurrentStats() throws SQLException {
        String sql = "SELECT " +
                "SUM(CASE WHEN completed = TRUE THEN 1 ELSE 0 END) AS completed_count, " +
                "SUM(CASE WHEN completed = FALSE THEN 1 ELSE 0 END) AS pending_count, " +
                "COUNT(*) AS total_tasks " +
                "FROM tasks";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                TaskSummary summary = new TaskSummary();
                summary.setCompletedCount(rs.getInt("completed_count"));
                summary.setPendingCount(rs.getInt("pending_count"));
                summary.setTotalTasks(rs.getInt("total_tasks"));
                return summary;
            }
        }
        return new TaskSummary();
    }

    // Insert summary into task_summaries table
    private void saveSummary(TaskSummary summary) throws SQLException {
        String insertSql = "INSERT INTO task_summaries (completed_count, pending_count, total_tasks) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, summary.getCompletedCount());
            stmt.setInt(2, summary.getPendingCount());
            stmt.setInt(3, summary.getTotalTasks());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    summary.setId(keys.getLong(1));
                }
            }
        }
    }

    // Retrieve all saved summaries
    public List<TaskSummary> findAllSummaries() throws SQLException {
        List<TaskSummary> summaries = new ArrayList<>();
        String sql = "SELECT id, completed_count, pending_count, total_tasks, generated_at FROM task_summaries ORDER BY generated_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                summaries.add(mapRow(rs));
            }
        }
        return summaries;
    }


    //Map a result set row to TaskSummary
    private TaskSummary mapRow(ResultSet rs) throws SQLException {
        TaskSummary s = new TaskSummary();
        s.setId(rs.getLong("id"));
        s.setCompletedCount(rs.getInt("completed_count"));
        s.setPendingCount(rs.getInt("pending_count"));
        s.setTotalTasks(rs.getInt("total_tasks"));
        Timestamp ts = rs.getTimestamp("generated_at");
        if (ts != null) {
            s.setGeneratedAt(ts.toLocalDateTime());
        }
        return s;
    }

}
