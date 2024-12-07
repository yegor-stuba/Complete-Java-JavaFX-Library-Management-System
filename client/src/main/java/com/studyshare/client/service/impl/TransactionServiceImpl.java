package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.TransactionService;
import com.studyshare.common.dto.TransactionDTO;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TransactionServiceImpl implements TransactionService {
    private final RestClient restClient;

    public TransactionServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public CompletableFuture<TransactionDTO> createTransaction(TransactionDTO transactionDTO) {
        return restClient.post("/api/transactions", transactionDTO, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getUserTransactions(Long userId) {
        return restClient.get("/api/transactions/user/" + userId,
                new ParameterizedTypeReference<>() {});
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getOverdueTransactions() {
        return restClient.get("/api/transactions/overdue",
                new ParameterizedTypeReference<>() {});
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