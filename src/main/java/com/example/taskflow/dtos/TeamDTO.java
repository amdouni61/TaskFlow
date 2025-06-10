package com.example.taskflow.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {

    private Long id;

    @Size(min = 3, max = 50, message = "Team name must be between 3 and 50 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private List<UserDTO> members;

    private Long teamLeadId;
    
    private String teamLeadUsername;
    
    private String teamLeadFullName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
