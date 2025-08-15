package com.example.taskflow.service;

import com.example.taskflow.dtos.UserDTO;
import com.example.taskflow.model.enums.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> getAllUsers();

    Optional<UserDTO> getUserById(Long id);

    Optional<UserDTO> getUserByEmail(String email);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);
    
    void deleteUser(Long id);

    UserDTO getCurrentUser();
    
    boolean isCurrentUser(Long userId);

    List<UserDTO> getUsersByRole(String roleName);

    List<UserDTO> getUsersByTeam(Long teamId);

    // New methods for user management
    UserDTO updateUserRole(Long id, UserRole role);
    
    boolean hideUser(Long id);
    
    boolean unhideUser(Long id);
    
    boolean isUserHidden(Long id);

    // Get active users for dashboard
    List<UserDTO> getActiveUsers();
}
