package com.studyshare.server.validation;

import com.studyshare.common.enums.UserRole;
import com.studyshare.server.model.User;
import com.studyshare.server.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class DatabaseVerifier {
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;


    public DatabaseVerifier(UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;

    }

    @PostConstruct
    public void verifyDatabase() {
        log.info("Starting database verification...");

        // Test connection
        try {
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
            log.info("Database connection verified");
        } catch (Exception e) {
            log.error("Database connection failed: {}", e.getMessage(), e);
            throw new RuntimeException("Database initialization failed", e);
        }

        // Verify users
        List<User> users = userRepository.findAll();
        log.info("Found {} users in database", users.size());

        users.forEach(user -> {
            log.debug("User found - ID: {}, Username: {}, Role: {}, Email: {}",
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                user.getEmail()
            );
        });

        // Verify admin exists
        userRepository.findByUsername("admin")
            .ifPresentOrElse(
                admin -> log.info("Admin user verified"),
                () -> {
                    log.warn("Admin user not found - creating default admin");
                    createDefaultAdmin();
                }
            );
    }

    private void createDefaultAdmin() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@studyshare.com");
        admin.setRole(UserRole.ADMIN);
        userRepository.save(admin);
        log.info("Default admin user created");
    }
}