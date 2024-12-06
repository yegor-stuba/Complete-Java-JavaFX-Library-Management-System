package com.studyshare.server.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Map<String, Object>> handleDisabledAccount() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Account is disabled");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Map<String, Object>> handleLockedAccount() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Account is locked");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(SessionAuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleSessionAuthenticationError() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Session authentication error");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationError() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Authentication failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}