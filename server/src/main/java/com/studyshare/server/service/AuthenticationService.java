package com.studyshare.server.service;

import com.studyshare.client.service.exception.AuthenticationException;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserService userService;
    private final SecurityAuditService securityAuditService;



    public UserDTO register(UserDTO userDTO) {
        log.debug("Registering new user: {}", userDTO.getUsername());
        return userService.createUser(userDTO);
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }



public UserDTO authenticate(String username, String password) {
    log.debug("Authenticating user: {}", username);
    UserDTO user = userService.findByUsername(username);
    log.debug("Found user during authentication: {}", user);
    return user;
}
}
