package com.example.taskflow.service.imp;

import com.example.taskflow.dtos.UserDTO;
import com.example.taskflow.exceptions.ResourceNotFoundException;
import com.example.taskflow.model.Team;
import com.example.taskflow.model.User;
import com.example.taskflow.model.enums.UserRole;
import com.example.taskflow.repositories.TeamRepository;
import com.example.taskflow.repositories.UserRepository;
import com.example.taskflow.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = convertToEntity(userDTO);
        // Encode the password before saving it
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        if (user.getRole() == null ) {
            user.setRole(UserRole.USER);
        }

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (!user.getUsername().equals(userDTO.getUsername()) && userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        if (!user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setUsername(userDTO.getUsername());
        user.setFullName(userDTO.getFullName());
        user.setEmail(userDTO.getEmail());
        user.setAvatarUrl(userDTO.getAvatarUrl());
        user.setEnabled(userDTO.isEnabled());

        // If password is provided, update it
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getTeamId() != null) {
            Team team = teamRepository.findById(userDTO.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + userDTO.getTeamId()));
            user.setTeam(team);
        }

        if (userDTO.getRole() != null ) {

            user.setRole(userDTO.getRole());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO getCurrentUser() {
        return null;
    }

    @Override
    public boolean isCurrentUser(Long userId) {
        return false;
    }

    @Override
    public List<UserDTO> getUsersByRole(String roleName) {
        return null;
    }

    @Override
    public List<UserDTO> getUsersByTeam(Long teamId) {
        return null;
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        if (user.getTeam() != null) {
            userDTO.setTeamId(user.getTeam().getId());
            userDTO.setTeamName(user.getTeam().getName());
        }
        return userDTO;
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        if (userDTO.getTeamId() != null) {
            Team team = teamRepository.findById(userDTO.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + userDTO.getTeamId()));
            user.setTeam(team);
        }
        if (userDTO.getRole() != null ) {

            user.setRole(userDTO.getRole());
        }
        return user;
    }
}
