package com.studyshare.client.service;

import com.studyshare.common.dto.TransactionDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransactionService {
    CompletableFuture<List<TransactionDTO>> getCurrentUserTransactions();
    CompletableFuture<List<TransactionDTO>> getBookTransactions(Long bookId);
    CompletableFuture<TransactionDTO> createTransaction(TransactionDTO transactionDTO);
    CompletableFuture<TransactionDTO> completeTransaction(Long transactionId);
    CompletableFuture<List<TransactionDTO>> getOverdueTransactions();
}
