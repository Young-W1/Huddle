package com.capstone.huddle.config;

import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin if not exists
        if (!userRepository.existsByUsername("admin")) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setEmail("admin@huddle.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(UserEntity.Role.ADMIN, UserEntity.Role.USER));
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            log.info("Default admin user created");
        }

        // Create default regular user if not exists
        if (!userRepository.existsByUsername("testuser")) {
            UserEntity user = new UserEntity();
            user.setUsername("testuser");
            user.setEmail("user@huddle.com");
            user.setFirstName("Test");
            user.setLastName("User");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(UserEntity.Role.USER));
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            log.info("Default test user created");
        }
    }
}
