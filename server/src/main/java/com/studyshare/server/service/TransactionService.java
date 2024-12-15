package com.studyshare.server.service;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.TransactionType;

import java.util.List;

public interface TransactionService {
    TransactionDTO createTransaction(Long bookId, TransactionType type);
    List<TransactionDTO> getAllTransactions();
    List<TransactionDTO> getUserTransactions();
    TransactionDTO getTransaction(Long transactionId);
    List<TransactionDTO> getTransactions(int page, int size);
    Long getTransactionCount();
}