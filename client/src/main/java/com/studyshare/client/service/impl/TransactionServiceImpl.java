package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.TransactionService;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class TransactionServiceImpl implements TransactionService {
    @Override
    public CompletableFuture<List<TransactionDTO>> getCurrentUserLentBooks() {
        return restClient.getList("/api/transactions/lent",
            new ParameterizedTypeReference<List<TransactionDTO>>() {});
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return List.of();
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getTransactions(Long bookId) {
        return null;
    }

    @Override
    public CompletableFuture<TransactionDTO> getLatestTransaction(Long bookId) {
        return null;
    }

    @Override
    public CompletableFuture<TransactionDTO> createTransaction(Long bookId, TransactionType type) {
        return null;
    }

    private final RestClient restClient;

    public TransactionServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

@Override
public CompletableFuture<List<TransactionDTO>> getCurrentUserBorrowedBooks() {
    return restClient.getList("/api/transactions/borrowed",
        new ParameterizedTypeReference<List<TransactionDTO>>() {});
}
    @Override
    public CompletableFuture<TransactionDTO> borrowBook(Long bookId) {
        return restClient.post("/api/transactions/borrow/" + bookId, null, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<TransactionDTO> returnBook(Long bookId) {
        return restClient.post("/api/transactions/return/" + bookId, null, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getActiveTransactions() {
        return restClient.getList("/api/transactions/active",
            new ParameterizedTypeReference<List<TransactionDTO>>() {});
    }

    @Override
    public CompletableFuture<Long> getActiveLoansCount() {
        return restClient.get("/api/transactions/count/active", Long.class);
    }
    @Override
public CompletableFuture<List<TransactionDTO>> getUserTransactions() {
    return restClient.getList("/api/transactions/user",
        new ParameterizedTypeReference<List<TransactionDTO>>() {});
}

@Override
public CompletableFuture<TransactionDTO> completeTransaction(Long transactionId) {
    return restClient.post("/api/transactions/" + transactionId + "/complete",
        null, TransactionDTO.class);
}



}