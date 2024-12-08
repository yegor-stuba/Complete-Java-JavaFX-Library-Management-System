package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.exception.RestClientException;
import com.studyshare.common.dto.TransactionDTO;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class TransactionServiceImpl implements TransactionService {
    private final RestClient restClient;

    public TransactionServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

public CompletableFuture<TransactionDTO> createTransaction(TransactionDTO transactionDTO) {
    return restClient.post("/api/transactions", transactionDTO, TransactionDTO.class)
        .exceptionally(throwable -> {
            if (throwable instanceof RestClientException) {
                RestClientException restError = (RestClientException) throwable;
                throw new CompletionException(new RuntimeException("Transaction failed: " + restError.getErrorBody()));
            }
            throw new CompletionException(throwable);
        });
}

    @Override
    public CompletableFuture<List<TransactionDTO>> getUserTransactions(Long userId) {
        return restClient.getList("/api/transactions/user/" + userId,
                new ParameterizedTypeReference<List<TransactionDTO>>() {});
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getOverdueTransactions() {
        return restClient.getList("/api/transactions/overdue",
                new ParameterizedTypeReference<List<TransactionDTO>>() {});
    }

    @Override
    public CompletableFuture<TransactionDTO> getTransactionDetails(Long transactionId) {
        return restClient.get("/api/transactions/" + transactionId, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<Integer> getActiveLoansCount() {
        return restClient.get("/api/transactions/active-loans-count", Integer.class);
    }

    @Override
    public CompletableFuture<TransactionDTO> completeTransaction(Long transactionId) {
        return restClient.post("/api/transactions/" + transactionId + "/complete", null, TransactionDTO.class);
    }
}