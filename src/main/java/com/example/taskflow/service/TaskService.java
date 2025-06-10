package com.example.taskflow.service;


import com.example.taskflow.dtos.CommentDTO;
import com.example.taskflow.dtos.TaskDTO;
import com.example.taskflow.model.Task;

import java.time.LocalDate;
import java.util.List;


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
}
