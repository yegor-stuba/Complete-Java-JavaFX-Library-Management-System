package com.studyshare.server.controller;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.server.service.BookService;
import com.studyshare.server.service.TransactionService;
import com.studyshare.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
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
    public ResponseEntity<List<BookDTO>> getUserBooks(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBooksByOwner(id));
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getUserTransactions(id));
    }
}