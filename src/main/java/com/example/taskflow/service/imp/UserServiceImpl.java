package com.example.taskflow.service.imp;

import com.example.taskflow.dtos.UserDTO;
import com.example.taskflow.model.User;
import com.example.taskflow.model.enums.UserRole;
import com.example.taskflow.repositories.UserRepository;
import com.example.taskflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::convertToDTO);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFullName(userDTO.getFullName());
        user.setUsername(userDTO.getUsername());
        user.setRole(userDTO.getRole());
        user.setEnabled(true);
        user.setHidden(false);
        user.setAvatarUrl(userDTO.getAvatarUrl());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setLastActivityAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setEmail(userDTO.getEmail());
            user.setFullName(userDTO.getFullName());
            user.setUsername(userDTO.getUsername());
            user.setRole(userDTO.getRole());
            user.setEnabled(userDTO.isEnabled());
            user.setAvatarUrl(userDTO.getAvatarUrl());
            user.setUpdatedAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(user);
            return convertToDTO(savedUser);
        }
        return null;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO updateUserRole(Long id, UserRole role) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setRole(role);
            user.setUpdatedAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(user);
            return convertToDTO(savedUser);
        }
        return null;
    }

    @Override
    public boolean hideUser(Long id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setHidden(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean unhideUser(Long id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setHidden(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean isUserHidden(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user != null && user.isHidden();
    }

    @Override
    public List<UserDTO> getActiveUsers() {
        // Get users who have been active recently (logged in within last 7 days)
        // For now, we'll return all non-hidden users
        // In a real application, you'd track user activity and filter by that
        List<User> activeUsers = userRepository.findAll().stream()
                .filter(user -> !user.isHidden())
                .limit(20) // Limit to 20 most recent active users
                .collect(Collectors.toList());
        
        return activeUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getCurrentUser() {
        // This method should be implemented based on security context
        // For now, return null as it requires security context injection
        return null;
    }

    @Override
    public boolean isCurrentUser(Long userId) {
        // This method should be implemented based on security context
        // For now, return false as it requires security context injection
        return false;
    }

    @Override
    public List<UserDTO> getUsersByRole(String roleName) {
        try {
            UserRole role = UserRole.valueOf(roleName.toUpperCase());
            return userRepository.findByRoleContaining(role).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    public List<UserDTO> getUsersByTeam(Long teamId) {
        // Since findByTeamId doesn't exist, we'll need to implement this differently
        // For now, return empty list - this can be implemented later
        return List.of();
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setHidden(user.isHidden());
        dto.setLastActivityAt(user.getLastActivityAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        if (user.getTeam() != null) {
            dto.setTeamId(user.getTeam().getId());
            dto.setTeamName(user.getTeam().getName());
        }
        
        return dto;
    }
}
