package com.example.taskflow.service;

import com.example.taskflow.dtos.UserDTO;

import java.util.List;


public interface UserService {


    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);

    UserDTO getCurrentUser();
    boolean isCurrentUser(Long userId);

    List<UserDTO> getUsersByRole(String roleName);

    List<UserDTO> getUsersByTeam(Long teamId);
}
