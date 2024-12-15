package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.TransactionService;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final RestClient restClient;
    private static final String BASE_PATH = "/api/transactions";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 1000L;


    @Override
    public CompletableFuture<TransactionDTO> createTransaction(Long bookId, TransactionType type) {
        return CompletableFuture.supplyAsync(() -> {
            int attempts = 0;
            while (attempts < MAX_RETRIES) {
                try {
                    return restClient.post(BASE_PATH + "?bookId=" + bookId + "&type=" + type,
                            null, TransactionDTO.class).join();
                } catch (Exception e) {
                    attempts++;
                    if (attempts == MAX_RETRIES) {
                        throw e;
                    }
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(ie);
                    }
                }
            }
            throw new RuntimeException("Failed after " + MAX_RETRIES + " attempts");
        });
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getCurrentUserTransactions() {
        return restClient.getList(BASE_PATH + "/current",
                new ParameterizedTypeReference<List<TransactionDTO>>() {});
    }

@Override
public CompletableFuture<List<TransactionDTO>> getAllTransactions() {
    return restClient.executeWithRetry(() ->
        restClient.getList(BASE_PATH, new ParameterizedTypeReference<List<TransactionDTO>>() {})
    ).exceptionally(throwable -> {
        log.error("Failed to fetch transactions: {}", throwable.getMessage());
        throw new RuntimeException("Failed to fetch transactions", throwable);
    });
}

    @Override
    public CompletableFuture<TransactionDTO> borrowBook(Long bookId) {
        return restClient.post(BASE_PATH + "/borrow?bookId=" + bookId, null, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<TransactionDTO> returnBook(Long bookId) {
        return restClient.post(BASE_PATH + "/return?bookId=" + bookId, null, TransactionDTO.class);
    }

@Override
public CompletableFuture<List<TransactionDTO>> getUserTransactions() {
    return restClient.getList(BASE_PATH + "/current", new ParameterizedTypeReference<List<TransactionDTO>>() {});
}

    @Override
    public CompletableFuture<TransactionDTO> getLatestTransaction(Long bookId) {
        return restClient.get(BASE_PATH + "/latest/" + bookId, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<List<TransactionDTO>> getTransactions(int page, int size) {
        return restClient.getList(BASE_PATH + "?page=" + page + "&size=" + size,
            new ParameterizedTypeReference<List<TransactionDTO>>() {});
    }

    @Override
    public CompletableFuture<TransactionDTO> completeTransaction(Long transactionId) {
        return restClient.put(BASE_PATH + "/" + transactionId + "/complete", null, TransactionDTO.class);
    }

    @Override
    public CompletableFuture<Long> getTransactionCount() {
        return restClient.get(BASE_PATH + "/count", Long.class);
    }
}