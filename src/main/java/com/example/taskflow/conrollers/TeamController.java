package com.example.taskflow.conrollers;

import com.example.taskflow.dtos.TeamDTO;
import com.example.taskflow.dtos.UserDTO;
import com.example.taskflow.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamService teamService;

   //Get all teams.

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    // Get team by ID.
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

   //Create a new team.

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        return new ResponseEntity<>(teamService.createTeam(teamDTO), HttpStatus.CREATED);
    }

   //Update a team.

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamDTO teamDTO) {
        return ResponseEntity.ok(teamService.updateTeam(id, teamDTO));
    }

    //Delete a team.

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    //Get teams by team lead.

    @GetMapping("/by-team-lead/{teamLeadId}")
    public ResponseEntity<List<TeamDTO>> getTeamsByTeamLead(@PathVariable Long teamLeadId) {
        return ResponseEntity.ok(teamService.getTeamsByTeamLead(teamLeadId));
    }

    //Add a user to a team.

    @PutMapping("/{teamId}/add-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> addUserToTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(teamService.addUserToTeam(teamId, userId));
    }

   //Remove a user from a team.

    @PutMapping("/{teamId}/remove-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> removeUserFromTeam(
            @PathVariable Long teamId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(teamService.removeUserFromTeam(teamId, userId));
    }

   // Set team lead.

    @PutMapping("/{teamId}/set-team-lead/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> setTeamLead(
            @PathVariable Long teamId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(teamService.setTeamLead(teamId, userId));
    }

   // Get team members.

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<UserDTO>> getTeamMembers(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }
}
