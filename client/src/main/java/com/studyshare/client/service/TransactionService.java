package com.studyshare.client.service;

import com.studyshare.common.dto.TransactionDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {
    CompletableFuture<TransactionDTO> createTransaction(TransactionDTO transactionDTO);
    CompletableFuture<List<TransactionDTO>> getUserTransactions(Long userId);
    CompletableFuture<TransactionDTO> completeTransaction(Long transactionId);
    CompletableFuture<List<TransactionDTO>> getOverdueTransactions();
    CompletableFuture<TransactionDTO> getTransactionDetails(Long transactionId);
    CompletableFuture<Integer> getActiveLoansCount();
}