package com.example.taskflow;

import com.example.taskflow.model.User;
import com.example.taskflow.model.enums.UserRole;
import com.example.taskflow.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableScheduling
public class TaskFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskFlowApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("=== CommandLineRunner Starting ===");
            
            // Check if admin user already exists
            var existingAdmin = userRepository.findByEmail("abdennour.amdouni@gmail.com");
            if (existingAdmin.isEmpty()) {
                System.out.println("Admin user not found, creating new one...");
                User adminUser = new User();
                adminUser.setEmail("abdennour.amdouni@gmail.com");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setFullName("System Administrator");
                adminUser.setUsername("admin");
                adminUser.setRole(UserRole.ADMIN);
                adminUser.setEnabled(true);
                adminUser.setIsHidden(false);
                adminUser.setAvatarUrl("https://ui-avatars.com/api/?name=Admin&background=0D47A1&color=fff");
                
                userRepository.save(adminUser);
                System.out.println("Default admin user created: abdennour.amdouni@gmail.com / admin123");
            } else {
                // Reactivate admin user if it exists but is deactivated
                User adminUser = existingAdmin.get();
                Boolean isHidden = adminUser.getIsHidden();
                boolean isEnabled = adminUser.isEnabled();
                
                System.out.println("Admin user found:");
                System.out.println("  - Email: " + adminUser.getEmail());
                System.out.println("  - isHidden: " + isHidden);
                System.out.println("  - isEnabled: " + isEnabled);
                System.out.println("  - Role: " + adminUser.getRole());
                
                if ((isHidden != null && isHidden) || !isEnabled) {
                    System.out.println("Reactivating admin user...");
                    adminUser.setIsHidden(false);
                    adminUser.setEnabled(true);
                    adminUser.setLastActivityAt(LocalDateTime.now());
                    userRepository.save(adminUser);
                    System.out.println("Admin user reactivated: abdennour.amdouni@gmail.com");
                } else {
                    System.out.println("Admin user is already active");
                    // Always update lastActivityAt to prevent session expiration
                    if (adminUser.getLastActivityAt() == null) {
                        adminUser.setLastActivityAt(LocalDateTime.now());
                        userRepository.save(adminUser);
                        System.out.println("Updated lastActivityAt for admin user");
                    }
                }
            }
            
            System.out.println("=== CommandLineRunner Completed ===");
        };
    }
}
