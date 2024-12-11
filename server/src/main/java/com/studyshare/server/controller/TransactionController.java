package com.studyshare.server.controller;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDTO>> getUserTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getUserTransactions(userId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<TransactionDTO>> getBookTransactions(@PathVariable Long bookId) {
        return ResponseEntity.ok(transactionService.getBookTransactions(bookId));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeTransaction(@PathVariable Long id) {
        transactionService.completeTransaction(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveLoansCount() {
        try {
            return ResponseEntity.ok(transactionService.getActiveLoansCount());
        } catch (Exception e) {
            log.error("Failed to get active loans count", e);
            return ResponseEntity.internalServerError().build();
        }
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
}