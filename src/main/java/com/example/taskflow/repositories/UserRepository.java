package com.example.taskflow.repositories;

import com.example.taskflow.model.Team;
import com.example.taskflow.model.User;
import com.example.taskflow.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by username
    Optional<User> findByUsername(String username);

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Check if a user with the given username exists
    boolean existsByUsername(String username);

    // Check if a user with the given email exists
    boolean existsByEmail(String email);

    // Find all users by team
    List<User> findByTeam(Team team);


    List<User> findByRoleContaining(UserRole role);

    // Find users by enabled status
    List<User> findByEnabled(boolean enabled);
}
