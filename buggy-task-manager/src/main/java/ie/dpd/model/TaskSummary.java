package ie.dpd.model;

import java.time.LocalDateTime;

/**
 * @Author ZI LI
 * @Date 2025/10/2 6:51
 * @comment relate to summary
 */

public class TaskSummary {
    private Long id;
    private int completedCount;
    private int pendingCount;
    private int totalTasks;
    private LocalDateTime generatedAt;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }

    public int getTotalTasks() { return totalTasks; }
    public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
