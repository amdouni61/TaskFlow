package com.example.taskflow.service.imp;

import com.example.taskflow.dtos.TeamDTO;
import com.example.taskflow.dtos.UserDTO;
import com.example.taskflow.exceptions.ResourceNotFoundException;
import com.example.taskflow.model.Team;
import com.example.taskflow.model.User;
import com.example.taskflow.repositories.TeamRepository;
import com.example.taskflow.repositories.UserRepository;
import com.example.taskflow.service.TeamService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        return convertToDTO(team);
    }

    @Override
    @Transactional
    public TeamDTO createTeam(TeamDTO teamDTO) {
        if (teamRepository.existsByName(teamDTO.getName())) {
            throw new IllegalArgumentException("Team name is already taken");
        }
        
        Team team = convertToEntity(teamDTO);
        
        // Set team lead if provided
        if (teamDTO.getTeamLeadId() != null) {
            User teamLead = userRepository.findById(teamDTO.getTeamLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + teamDTO.getTeamLeadId()));
            team.setTeamLead(teamLead);
        }
        
        Team savedTeam = teamRepository.save(team);
        return convertToDTO(savedTeam);
    }

    @Override
    @Transactional
    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + id));
        
        // Check if team name is taken by another team
        if (!team.getName().equals(teamDTO.getName()) &&
                teamRepository.existsByName(teamDTO.getName())) {
            throw new IllegalArgumentException("Team name is already taken");
        }
        
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        
        // Update team lead if provided
        if (teamDTO.getTeamLeadId() != null) {
            User teamLead = userRepository.findById(teamDTO.getTeamLeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + teamDTO.getTeamLeadId()));
            team.setTeamLead(teamLead);
        } else {
            team.setTeamLead(null);
        }
        
        Team updatedTeam = teamRepository.save(team);
        return convertToDTO(updatedTeam);
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new ResourceNotFoundException("Team not found with id: " + id);
        }
        
        Team team = teamRepository.findById(id).get();
        
        // Remove team reference from members
        for (User user : team.getMembers()) {
            user.setTeam(null);
            userRepository.save(user);
        }
        
        teamRepository.deleteById(id);
    }

    @Override
    public List<TeamDTO> getTeamsByTeamLead(Long teamLeadId) {
        User teamLead = userRepository.findById(teamLeadId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + teamLeadId));
        
        return teamRepository.findByTeamLead(teamLead).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamDTO addUserToTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setTeam(team);
        userRepository.save(user);
        
        return convertToDTO(teamRepository.findById(teamId).get());
    }

    @Override
    @Transactional
    public TeamDTO removeUserFromTeam(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (user.getTeam() == null || !user.getTeam().getId().equals(teamId)) {
            throw new IllegalStateException("User is not a member of this team");
        }
        
        user.setTeam(null);
        userRepository.save(user);
        
        // If user is team lead, remove team lead
        if (team.getTeamLead() != null && team.getTeamLead().getId().equals(userId)) {
            team.setTeamLead(null);
            teamRepository.save(team);
        }
        
        return convertToDTO(teamRepository.findById(teamId).get());
    }

    @Override
    @Transactional
    public TeamDTO setTeamLead(Long teamId, Long userId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Make sure user is a member of the team
        if (user.getTeam() == null || !user.getTeam().getId().equals(teamId)) {
            user.setTeam(team);
            userRepository.save(user);
        }
        
        team.setTeamLead(user);
        Team updatedTeam = teamRepository.save(team);
        
        return convertToDTO(updatedTeam);
    }

    @Override
    public List<UserDTO> getTeamMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        
        return userRepository.findByTeam(team).stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }


    private TeamDTO convertToDTO(Team team) {
        TeamDTO teamDTO = modelMapper.map(team, TeamDTO.class);
        
        if (team.getTeamLead() != null) {
            teamDTO.setTeamLeadId(team.getTeamLead().getId());
            teamDTO.setTeamLeadUsername(team.getTeamLead().getUsername());
            teamDTO.setTeamLeadFullName(team.getTeamLead().getFullName());
        }
        
        return teamDTO;
    }


    private Team convertToEntity(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        if (teamDTO.getTeamLeadId() != null) {
            User teamLead = userRepository.findById(teamDTO.getTeamLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + teamDTO.getTeamLeadId()));
            team.setTeamLead(teamLead);
        } else {
            team.setTeamLead(null);
        }
        return team;
    }
}
