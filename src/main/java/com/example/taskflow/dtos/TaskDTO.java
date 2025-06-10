package com.example.taskflow.dtos;

import com.example.taskflow.model.Task;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Task type is required")
    private Task.TaskType type;

    private Task.TaskStatus status;

    private Task.TaskPriority priority;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private LocalDate deadline;
    
    private String attachmentUrl;

    private Long userId;
    
    private String username;
    
    private String userFullName;
    
    private String userAvatarUrl;

    private Long supervisorId;
    
    private String supervisorUsername;
    
    private String supervisorFullName;

    private List<CommentDTO> comments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
