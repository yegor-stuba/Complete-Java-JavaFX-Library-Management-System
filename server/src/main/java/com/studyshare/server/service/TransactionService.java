package com.studyshare.server.service;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.TransactionType;

import java.util.List;

public interface TransactionService {
    TransactionDTO createTransaction(Long bookId, TransactionType type);
    TransactionDTO getTransactionById(Long id);
    List<TransactionDTO> getUserTransactions(Long userId);
    List<TransactionDTO> getBookTransactions(Long bookId);
    List<TransactionDTO> getActiveTransactions();
    Long getActiveLoansCount();
    TransactionDTO completeTransaction(Long transactionId);

    TransactionDTO borrowBook(Long bookId);
    TransactionDTO returnBook(Long bookId);
}