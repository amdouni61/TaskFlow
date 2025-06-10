package com.example.taskflow.service;

import com.example.taskflow.dtos.TeamDTO;
import com.example.taskflow.dtos.UserDTO;

import java.util.List;


public interface TeamService {

    List<TeamDTO> getAllTeams();

    TeamDTO getTeamById(Long id);

    TeamDTO createTeam(TeamDTO teamDTO);
    TeamDTO updateTeam(Long id, TeamDTO teamDTO);

    void deleteTeam(Long id);

    List<TeamDTO> getTeamsByTeamLead(Long teamLeadId);

    TeamDTO addUserToTeam(Long teamId, Long userId);

    TeamDTO removeUserFromTeam(Long teamId, Long userId);

    TeamDTO setTeamLead(Long teamId, Long userId);

    List<UserDTO> getTeamMembers(Long teamId);
}
