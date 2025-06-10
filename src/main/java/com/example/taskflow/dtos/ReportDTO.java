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
}
