package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.TransactionService;
import com.studyshare.common.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final RestClient restClient;

    @Override
    public CompletableFuture<List<TransactionDTO>> getCurrentUserTransactions() {
        return restClient.get("/api/transactions/current", List.class);
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getBookTransactions(Long bookId) {
        return restClient.get("/api/transactions/book/" + bookId, List.class);
    }

    @Override
    public CompletableFuture<TransactionDTO> createTransaction(TransactionDTO transactionDTO) {
        return restClient.post("/api/transactions", transactionDTO, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<TransactionDTO> completeTransaction(Long transactionId) {
        return restClient.post("/api/transactions/" + transactionId + "/complete", null, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getOverdueTransactions() {
        return restClient.get("/api/transactions/overdue", List.class);
    }
}
