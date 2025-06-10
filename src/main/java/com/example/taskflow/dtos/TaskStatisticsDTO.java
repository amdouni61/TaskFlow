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
}
