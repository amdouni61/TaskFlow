package com.example.taskflow.dtos;

import com.example.taskflow.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String username;
    private String password;
    private UserRole role;
    private boolean enabled;
    private String avatarUrl;
    private Long teamId;
    private String teamName;
    private LocalDateTime lastActivityAt;
    private boolean isHidden;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
