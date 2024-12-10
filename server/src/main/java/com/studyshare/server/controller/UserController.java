package com.studyshare.server.controller;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.server.model.Book;
import com.studyshare.server.service.BookService;
import com.studyshare.server.service.TransactionService;
import com.studyshare.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);




@GetMapping
@PreAuthorize("hasRole('ADMIN')")  // Ensure this annotation is present
public ResponseEntity<List<UserDTO>> getAllUsers() {
    try {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    } catch (Exception e) {
        log.error("Failed to fetch users", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userService.getUserCount());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            log.debug("Creating new user: {}", userDTO.getUsername());
            UserDTO createdUser = userService.createUser(userDTO);
            log.info("User created successfully: {}", createdUser.getUsername());
            return ResponseEntity.ok(createdUser);
        } catch (DataIntegrityViolationException e) {
            log.error("User creation failed - duplicate data", e);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        } catch (Exception e) {
            log.error("User creation failed", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user: " + e.getMessage());
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: Insufficient privileges");
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUser().join());
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable Long id, @RequestBody UserRole role) {
        UserDTO userDTO = userService.getCurrentUser().join();
        userDTO.setRole(role);
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @GetMapping("/{id}/books")
    public ResponseEntity<List<Book>> getUserBooks(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBooksByUserId(id));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getUserTransactions(id));
    }
}