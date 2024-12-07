package com.studyshare.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityAuditService {
    private static final String AUDIT_FILE = "logs/security.log";

    public void logLoginAttempt(String username, boolean success) {
        log.info("Login attempt: user={}, success={}", username, success);
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

    public void logSecurityEvent(String event, String details) {
        log.info("Security event: {}, details: {}", event, details);
    }
}
