package com.studyshare.client.service;

import com.studyshare.common.dto.TransactionDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {
    CompletableFuture<TransactionDTO> borrowBook(Long bookId);
    CompletableFuture<TransactionDTO> returnBook(Long bookId);
    CompletableFuture<List<TransactionDTO>> getUserTransactions();
    CompletableFuture<List<TransactionDTO>> getActiveTransactions();
    CompletableFuture<Long> getActiveLoansCount();
    CompletableFuture<List<TransactionDTO>> getCurrentUserBorrowedBooks();
    CompletableFuture<List<TransactionDTO>> getCurrentUserLentBooks();// Remove Long parameter
CompletableFuture<TransactionDTO> completeTransaction(Long transactionId);  // Add this method
}