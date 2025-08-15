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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<UserDTO> getMembers() { return members; }
    public void setMembers(List<UserDTO> members) { this.members = members; }
    public Long getTeamLeadId() { return teamLeadId; }
    public void setTeamLeadId(Long teamLeadId) { this.teamLeadId = teamLeadId; }
    public String getTeamLeadUsername() { return teamLeadUsername; }
    public void setTeamLeadUsername(String teamLeadUsername) { this.teamLeadUsername = teamLeadUsername; }
    public String getTeamLeadFullName() { return teamLeadFullName; }
    public void setTeamLeadFullName(String teamLeadFullName) { this.teamLeadFullName = teamLeadFullName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
