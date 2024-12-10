package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.exception.RestClientException;
import com.studyshare.common.dto.TransactionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
public class TransactionServiceImpl implements TransactionService {
    @Override
    public CompletableFuture<List<TransactionDTO>> getCurrentUserLentBooks() {
        return restClient.getList("/api/transactions/lent",
            new ParameterizedTypeReference<List<TransactionDTO>>() {});
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