package com.studyshare.client.service;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.TransactionType;

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

    List<TransactionDTO> getAllTransactions();// Add this method
    CompletableFuture<List<TransactionDTO>> getTransactions(Long bookId);
    CompletableFuture<TransactionDTO> getLatestTransaction(Long bookId);

    CompletableFuture<TransactionDTO> createTransaction(Long bookId, TransactionType type);

    CompletableFuture<TransactionDTO> completeTransaction(Long id);

}