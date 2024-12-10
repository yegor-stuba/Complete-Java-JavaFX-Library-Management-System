package com.studyshare.server.service;

import com.studyshare.client.service.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SecurityAuditService {
    private static final String AUDIT_FILE = "logs/security.log";
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;


    public void logLoginAttempt(String username, boolean success) {
        if (success) {
            loginAttempts.remove(username);
            log.info("Successful login for user: {}", username);
        } else {
            int attempts = loginAttempts.getOrDefault(username, 0) + 1;
            loginAttempts.put(username, attempts);
            log.warn("Failed login attempt {} for user: {}", attempts, username);

            if (attempts >= MAX_ATTEMPTS) {
                log.error("Account locked for user: {} after {} failed attempts", username, attempts);
                throw new AuthenticationException("Account locked due to multiple failed attempts");
            }
        }
    }

public void logSecurityEvent(String event, String details) {
    log.info("Security event: {}, details: {}", event, details);
    if (event.contains("UNAUTHORIZED") || event.contains("FORBIDDEN")) {
        log.warn("Security violation: {}", details);
    }
}

    public void logRegistration(String username) {
        log.info("New user registration: {}", username);
    }

    public void logPasswordReset(String username) {
        log.info("Password reset requested: {}", username);
    }

    public void logRoleChange(String username, String newRole) {
        log.info("Role changed for user: {}, new role: {}", username, newRole);
    }

    public void logAccountLock(String username, String reason) {
        log.warn("Account locked: user={}, reason={}", username, reason);
    }


}
