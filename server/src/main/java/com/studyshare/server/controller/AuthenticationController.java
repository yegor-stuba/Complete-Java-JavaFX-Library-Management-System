package com.studyshare.server.controller;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.security.JwtTokenProvider;
import com.studyshare.server.security.dto.AuthenticationRequest;
import com.studyshare.server.security.dto.AuthenticationResponse;
import com.studyshare.server.service.AuthenticationService;
import com.studyshare.server.service.LoginAttemptService;
import com.studyshare.server.service.SecurityAuditService;
import com.studyshare.server.service.UserService;
import com.studyshare.server.validation.UserValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final UserValidator userValidator;
    private final LoginAttemptService loginAttemptService;
    private final SecurityAuditService securityAuditService;
    private final AuthenticationService authService;

@PostMapping("/login")
public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
    try {
        // Remove any extra spaces from username
        String username = request.getUsername().trim();
        log.debug("Login attempt for user: '{}' with password: '{}'", username, request.getPassword());

        UserDTO user = userService.findByUsername(username);
        if (user != null) {
            log.debug("Found user with details: {}", user);
            if (userService.authenticate(username, request.getPassword())) {
                return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token("token-" + user.getUserId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .userId(user.getUserId())
                    .build());
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
        log.error("Login failed: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

@PostMapping("/register")
public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid UserDTO userDTO) {
    try {
        // Set default role
        userDTO.setRole(UserRole.USER);

        // Create user
        UserDTO createdUser = userService.createUser(userDTO);

        return ResponseEntity.ok(AuthenticationResponse.builder()
            .token("token-" + createdUser.getUserId())
            .username(createdUser.getUsername())
            .role(createdUser.getRole())
            .userId(createdUser.getUserId())
            .build());
    } catch (ValidationException e) {
        log.error("Registration failed: {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    } catch (Exception e) {
        log.error("Registration failed: {}", e.getMessage());
        return ResponseEntity.internalServerError().build();
    }
}

    @GetMapping("/current")
    public ResponseEntity<CompletableFuture<UserDTO>> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(tokenProvider.validateToken(token.substring(7)));
    }


}