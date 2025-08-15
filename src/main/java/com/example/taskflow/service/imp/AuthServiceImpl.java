package com.example.taskflow.service.imp;

import com.example.taskflow.dtos.LoginUserDto;
import com.example.taskflow.dtos.RegisterUserDto;
import com.example.taskflow.exceptions.InvalidCredentialsException;
import com.example.taskflow.exceptions.UserAlreadyExistsException;
import com.example.taskflow.model.User;
import com.example.taskflow.model.enums.UserRole;
import com.example.taskflow.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        // Check if email is already in use
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use");
        }

        // Default role for the user
        UserRole userRole = input.getRole() != null ? input.getRole() : UserRole.USER;

        User user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setRole(userRole);
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        try {
            // Attempt authentication
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword())
            );

            return userRepository.findByEmail(input.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid credentials provided");
        }
    }
}
