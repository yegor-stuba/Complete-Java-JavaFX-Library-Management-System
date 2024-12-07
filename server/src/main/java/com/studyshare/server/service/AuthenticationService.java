package com.studyshare.server.service;

import com.studyshare.server.exception.InvalidTokenException;
import com.studyshare.server.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final SecurityAuditService securityAuditService;
    private final PasswordEncoder passwordEncoder;

    public boolean validateCredentials(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    public void storeAuthToken(String token) {
        tokenProvider.validateToken(token);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String generateToken(Authentication authentication) {
        return tokenProvider.generateToken(authentication);
    }

    public void validateToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }

    public void logout(String username) {
        securityAuditService.logSecurityEvent("LOGOUT", "User logged out: " + username);
    }
}