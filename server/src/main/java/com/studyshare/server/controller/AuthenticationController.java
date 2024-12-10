package com.studyshare.server.controller;


import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.common.security.dto.AuthenticationRequest;
import com.studyshare.common.security.dto.AuthenticationResponse;
import com.studyshare.server.service.AuthenticationService;
import com.studyshare.server.service.SecurityAuditService;
import com.studyshare.server.service.TransactionService;
import com.studyshare.server.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final TransactionService transactionService;


@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpSession session) {
    UserDTO user = userService.findByUsername(request.getUsername().trim());
    if (user != null && userService.authenticate(request.getUsername(), request.getPassword())) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        Authentication auth = new UsernamePasswordAuthenticationToken(
            user, null, authorities
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        session.setAttribute("USER", user);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok()
            .header("X-Auth-Token", session.getId())
            .body(user);
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
@GetMapping("/user/{userId}")
public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable Long userId) {
    return ResponseEntity.ok(transactionService.getUserTransactions(userId));
}

private Authentication createAuthentication(UserDTO user) {
    List<GrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
    );
    return new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
}

private void configureSession(HttpSession session, UserDTO user) {
    session.setAttribute("USER_ROLE", user.getRole());
    session.setAttribute("USER_ID", user.getUserId());
    session.setMaxInactiveInterval(3600);
}

private UserDTO sanitizeUserData(UserDTO user) {
    user.setPassword(null);
    return user;
}

private boolean validateLoginRequest(AuthenticationRequest request) {
    return request.getUsername() != null && !request.getUsername().trim().isEmpty()
        && request.getPassword() != null && !request.getPassword().isEmpty();
}



private void setupUserAuthentication(UserDTO user) {
    List<GrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
    );
    Authentication auth = new UsernamePasswordAuthenticationToken(
        user.getUsername(), null, authorities
    );
    SecurityContextHolder.getContext().setAuthentication(auth);
}

private void setupUserSession(HttpSession session, UserDTO user) {
    session.setAttribute("USER_ROLE", user.getRole());
    session.setAttribute("USER_ID", user.getUserId());
    session.setMaxInactiveInterval(3600);
}


@PostMapping("/logout")
public ResponseEntity<Void> logout(HttpSession session) {
    SecurityContextHolder.clearContext();
    session.invalidate();
    return ResponseEntity.ok().build();
}

@GetMapping("/current")
public ResponseEntity<UserDTO> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof UserDTO) {
        return ResponseEntity.ok((UserDTO) auth.getPrincipal());
    }
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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


}