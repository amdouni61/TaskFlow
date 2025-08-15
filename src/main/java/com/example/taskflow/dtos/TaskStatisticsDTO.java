package com.example.taskflow.dtos;

import com.example.taskflow.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatisticsDTO {

    // Task count by status
    private long pendingTasksCount;
    private long approvedTasksCount;
    private long rejectedTasksCount;
    private long completedTasksCount;
    private long totalTasksCount;

    // Task distribution by user
    private Map<String, Long> taskCountByUser;

    // Task distribution by type
    private Map<Task.TaskType, Long> taskCountByType;

    // Task distribution by status
    private Map<Task.TaskStatus, Long> taskCountByStatus;

    // Task distribution by month
    private Map<String, Long> taskCountByMonth;

    public long getPendingTasksCount() { return pendingTasksCount; }
    public void setPendingTasksCount(long pendingTasksCount) { this.pendingTasksCount = pendingTasksCount; }
    public long getApprovedTasksCount() { return approvedTasksCount; }
    public void setApprovedTasksCount(long approvedTasksCount) { this.approvedTasksCount = approvedTasksCount; }
    public long getRejectedTasksCount() { return rejectedTasksCount; }
    public void setRejectedTasksCount(long rejectedTasksCount) { this.rejectedTasksCount = rejectedTasksCount; }
    public long getCompletedTasksCount() { return completedTasksCount; }
    public void setCompletedTasksCount(long completedTasksCount) { this.completedTasksCount = completedTasksCount; }
    public long getTotalTasksCount() { return totalTasksCount; }
    public void setTotalTasksCount(long totalTasksCount) { this.totalTasksCount = totalTasksCount; }
    public Map<String, Long> getTaskCountByUser() { return taskCountByUser; }
    public void setTaskCountByUser(Map<String, Long> taskCountByUser) { this.taskCountByUser = taskCountByUser; }
    public Map<Task.TaskType, Long> getTaskCountByType() { return taskCountByType; }
    public void setTaskCountByType(Map<Task.TaskType, Long> taskCountByType) { this.taskCountByType = taskCountByType; }
    public Map<Task.TaskStatus, Long> getTaskCountByStatus() { return taskCountByStatus; }
    public void setTaskCountByStatus(Map<Task.TaskStatus, Long> taskCountByStatus) { this.taskCountByStatus = taskCountByStatus; }
    public Map<String, Long> getTaskCountByMonth() { return taskCountByMonth; }
    public void setTaskCountByMonth(Map<String, Long> taskCountByMonth) { this.taskCountByMonth = taskCountByMonth; }
}
