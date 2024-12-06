package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.TransactionService;
import com.studyshare.common.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    @Override
    public CompletableFuture<Integer> getActiveLoansCount() {
        return restClient.get("/api/transactions/active-loans-count", Integer.class);
    }
    private final RestClient restClient;

    @Override
    public CompletableFuture<TransactionDTO> createTransaction(TransactionDTO transactionDTO) {
        return restClient.post("/api/transactions", transactionDTO, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<List> getUserTransactions(Long userId) {
        return restClient.get("/api/transactions/user/" + userId, List.class);
    }

    @Override
    public CompletableFuture<TransactionDTO> completeTransaction(Long transactionId) {
        return restClient.post("/api/transactions/" + transactionId + "/complete", null, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<List> getOverdueTransactions() {
        return restClient.get("/api/transactions/overdue", List.class);
    }

    @Override
    public CompletableFuture<TransactionDTO> getTransactionDetails(Long transactionId) {
        return restClient.get("/api/transactions/" + transactionId, TransactionDTO.class);
    }
}