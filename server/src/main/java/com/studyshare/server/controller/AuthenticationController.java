package com.studyshare.server.controller;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.security.JwtTokenProvider;
import com.studyshare.server.security.dto.AuthenticationResponse;
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
import org.springframework.validation.BindingResult;
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

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserDTO userDTO) {
        if (loginAttemptService.isBlocked(userDTO.getUsername())) {
            log.warn("User {} is blocked due to too many failed attempts", userDTO.getUsername());
            securityAuditService.logAccountLock(userDTO.getUsername(), "Too many failed attempts");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
            );

            String jwt = tokenProvider.generateToken(authentication);
            UserDTO user = userService.findByUsername(userDTO.getUsername());
            loginAttemptService.loginSucceeded(userDTO.getUsername());
            securityAuditService.logLoginAttempt(userDTO.getUsername(), true);

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(jwt)
                    .username(user.getUsername())
                    .role(user.getRole())
                    .userId(user.getUserId())
                    .build());
        } catch (Exception e) {
            loginAttemptService.loginFailed(userDTO.getUsername());
            securityAuditService.logLoginAttempt(userDTO.getUsername(), false);
            log.error("Login failed for user {}: {}", userDTO.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        try {
            userValidator.validate(userDTO, bindingResult);
            if (bindingResult.hasErrors()) {
                log.warn("Registration validation failed for username {}", userDTO.getUsername());
                throw new ValidationException("Registration validation failed: " + bindingResult.getAllErrors());
            }

            UserDTO createdUser = userService.createUser(userDTO);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            log.info("User registered successfully: {}", userDTO.getUsername());
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(jwt)
                    .username(createdUser.getUsername())
                    .role(createdUser.getRole())
                    .userId(createdUser.getUserId())
                    .build());
        } catch (Exception e) {
            log.error("Registration failed for username {}: {}", userDTO.getUsername(), e.getMessage());
            throw new ValidationException("Registration failed: " + e.getMessage());
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