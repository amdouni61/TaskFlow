package com.example.taskflow.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private Long userId;
    private String username;
    private String userFullName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TaskDTO> tasks;
    private long pendingTasksCount;
    private long approvedTasksCount;
    private long rejectedTasksCount;
    private long completedTasksCount;
    private long totalTasksCount;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public List<TaskDTO> getTasks() { return tasks; }
    public void setTasks(List<TaskDTO> tasks) { this.tasks = tasks; }
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
}
