package com.studyshare.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "StudyShare Library Management System API";
    }

    @GetMapping("/api/auth/login")
    public ResponseEntity<String> loginPage() {
        return ResponseEntity.ok("Please use POST method for login");
    }
}