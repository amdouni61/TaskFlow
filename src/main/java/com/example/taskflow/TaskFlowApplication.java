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

@SpringBootApplication
@EnableScheduling
public class TaskFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskFlowApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if admin user already exists
            if (userRepository.findByEmail("abdennour.amdouni@gmail.com").isEmpty()) {
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
            }
        };
    }
}
