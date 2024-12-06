package com.studyshare.server.controller;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        try {
            System.out.println("Received registration request for user: " + userDTO.getUsername());
            return ResponseEntity.ok(userService.createUser(userDTO));
        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.authenticate(userDTO.getUsername(), userDTO.getPassword()));
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        userService.logout();
        return ResponseEntity.ok().build();
    }
}