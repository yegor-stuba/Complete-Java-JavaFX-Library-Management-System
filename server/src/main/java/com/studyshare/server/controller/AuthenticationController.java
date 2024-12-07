package com.studyshare.server.controller;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.security.JwtTokenProvider;
import com.studyshare.server.security.dto.AuthenticationResponse;
import com.studyshare.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

   @PostMapping("/login")
public ResponseEntity<AuthenticationResponse> login(@RequestBody UserDTO userDTO) {
    try {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserDTO user = userService.findByUsername(userDTO.getUsername());

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(AuthenticationResponse.builder()
                .token(jwt)
                .username(user.getUsername())
                .role(user.getRole())
                .userId(user.getUserId())
                .build());
    } catch (Exception e) {
        log.error("Authentication failed", e);
        throw new RuntimeException("Authentication failed: " + e.getMessage());
    }
}


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(AuthenticationResponse.builder()
            .token(jwt)
            .username(createdUser.getUsername())
            .role(createdUser.getRole())
            .userId(createdUser.getUserId())
            .build());
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