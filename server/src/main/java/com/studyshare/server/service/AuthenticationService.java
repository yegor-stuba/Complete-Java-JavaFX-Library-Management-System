package com.studyshare.server.service;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.server.exception.InvalidTokenException;
import com.studyshare.server.security.JwtTokenProvider;
import com.studyshare.server.security.dto.AuthenticationRequest;
import com.studyshare.server.security.dto.AuthenticationResponse;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final SecurityAuditService securityAuditService;
    private final PasswordEncoder passwordEncoder;

    public String generateToken(String username, UserRole role) {
        return tokenProvider.generateToken(username, role);
    }

public boolean authenticate(String username, String password) {
    try {
        UserDTO user = userService.findByUsername(username);
        return user != null && password.equals(user.getPassword());
    } catch (Exception e) {
        return false;
    }
}
}