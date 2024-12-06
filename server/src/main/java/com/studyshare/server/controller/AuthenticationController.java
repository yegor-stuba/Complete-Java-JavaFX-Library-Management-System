package com.studyshare.server.controller;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.security.JwtTokenProvider;
import com.studyshare.server.security.dto.AuthenticationRequest;
import com.studyshare.server.security.dto.AuthenticationResponse;
import com.studyshare.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserDTO user = userService.findByUsername(request.getUsername());

        return ResponseEntity.ok(AuthenticationResponse.builder()
            .token(jwt)
            .username(user.getUsername())
            .role(user.getRole())
            .userId(user.getUserId())
            .build());
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

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        return ResponseEntity.ok(tokenProvider.validateToken(jwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
