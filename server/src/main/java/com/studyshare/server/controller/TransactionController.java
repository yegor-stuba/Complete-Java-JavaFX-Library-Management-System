package com.studyshare.server.controller;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.studyshare.common.enums.TransactionType;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;



    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

@PostMapping
public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
    return ResponseEntity.ok(transactionService.createTransaction(
        transactionDTO.getBookId(),
        transactionDTO.getType()
    ));
}

    @PostMapping("/borrow")
    public ResponseEntity<TransactionDTO> borrowBook(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(transactionService.createTransaction(
            transactionDTO.getBookId(),
            transactionDTO.getType()
        ));
    }


// In TransactionController.java
@PostMapping("/books/{bookId}/borrow")
public ResponseEntity<TransactionDTO> borrowBook(@PathVariable Long bookId) {
    return ResponseEntity.ok(transactionService.borrowBook(bookId));
}

@PostMapping("/books/{bookId}/return")
public ResponseEntity<TransactionDTO> returnBook(@PathVariable Long bookId) {
    return ResponseEntity.ok(transactionService.returnBook(bookId));
}

@GetMapping
public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
    return ResponseEntity.ok(transactionService.getAllTransactions());
}

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getUserTransactions(userId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<TransactionDTO>> getBookTransactions(@PathVariable Long bookId) {
        return ResponseEntity.ok(transactionService.getBookTransactions(bookId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<TransactionDTO>> getActiveTransactions() {
        return ResponseEntity.ok(transactionService.getActiveTransactions());
    }

@GetMapping("/count/active-transactions")
public ResponseEntity<Long> getActiveTransactionsCount() {
    return ResponseEntity.ok(transactionService.getActiveTransactionsCount());
}

@GetMapping("/count/active-loans")
public ResponseEntity<Long> getActiveLoansCount() {
    return ResponseEntity.ok(transactionService.getActiveLoansCount());
}

    @PostMapping("/{bookId}/user/{userId}")
    public ResponseEntity<TransactionDTO> createTransaction(
            @PathVariable Long bookId,
            @PathVariable Long userId,
            @RequestParam TransactionType type) {
        return ResponseEntity.ok(transactionService.createTransaction(bookId, userId, type));
    }

    @PutMapping("/{transactionId}/complete")
    public ResponseEntity<TransactionDTO> completeTransaction(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.completeTransaction(transactionId));
    }
}