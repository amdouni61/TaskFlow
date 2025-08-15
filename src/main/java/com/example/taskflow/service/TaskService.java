package com.example.taskflow.service;


import com.example.taskflow.dtos.CommentDTO;
import com.example.taskflow.dtos.TaskDTO;
import com.example.taskflow.dtos.TaskStatisticsDTO;
import com.example.taskflow.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface TaskService {


    List<TaskDTO> getAllTasks();


    TaskDTO getTaskById(Long id);


    TaskDTO createTask(TaskDTO taskDTO);


    TaskDTO updateTask(Long id, TaskDTO taskDTO);


    void deleteTask(Long id);

    List<TaskDTO> getTasksByUser(Long userId);

    List<TaskDTO> getTasksBySupervisor(Long supervisorId);

    List<TaskDTO> getTasksByStatus(Task.TaskStatus status);

    List<TaskDTO> getTasksByDate(LocalDate date);
    List<TaskDTO> getTasksByDateRange(LocalDate startDate, LocalDate endDate);

    TaskDTO approveTask(Long id, CommentDTO comment);

    TaskDTO rejectTask(Long id, CommentDTO comment);

    boolean isTaskCreatedByCurrentUser(Long taskId);

    List<TaskDTO> getCurrentUserTasks();

    // Get task statistics for dashboard
    TaskStatisticsDTO getTaskStatistics();

    // Get recent tasks
    List<TaskDTO> getRecentTasks(int limit);

    // Get related tasks (same team or assignee)
    List<TaskDTO> getRelatedTasks(Long taskId);

    // Get tasks grouped by team
    Map<String, List<TaskDTO>> getTasksByTeam();

    // Get filtered tasks
    List<TaskDTO> getFilteredTasks(Map<String, Object> filters);

    // Get tasks by user ID
    List<TaskDTO> getTasksByUserId(Long userId);
}
