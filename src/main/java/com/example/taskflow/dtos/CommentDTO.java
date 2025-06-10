package com.example.taskflow.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;

    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;

    @NotNull(message = "Task ID is required")
    private Long taskId;

    private Long userId;
    
    private String username;
    
    private String userFullName;
    
    private String userAvatarUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
