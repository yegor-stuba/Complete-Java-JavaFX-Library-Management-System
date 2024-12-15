package com.studyshare.server.controller;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.TransactionType;
import com.studyshare.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

@GetMapping("/count")
public ResponseEntity<Long> getTransactionCount() {
    return ResponseEntity.ok(transactionService.getTransactionCount());
}


@GetMapping("/current")
public ResponseEntity<List<TransactionDTO>> getCurrentUserTransactions() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDTO userDTO = (UserDTO) auth.getPrincipal();
    return ResponseEntity.ok(transactionService.getUserTransactions());
}

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getUserTransactions());
    }

    @PostMapping("/borrow")
    public ResponseEntity<TransactionDTO> borrowBook(@RequestParam Long bookId) {
        return ResponseEntity.ok(transactionService.createTransaction(bookId, TransactionType.BORROW));
    }

    @PostMapping("/return")
    public ResponseEntity<TransactionDTO> returnBook(@RequestParam Long bookId) {
        return ResponseEntity.ok(transactionService.createTransaction(bookId, TransactionType.RETURN));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestParam Long bookId,
            @RequestParam TransactionType type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        return ResponseEntity.ok(transactionService.createTransaction(bookId, type));
    }



    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }
}