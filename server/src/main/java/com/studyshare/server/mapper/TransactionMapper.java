package com.studyshare.server.mapper;


import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.server.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public TransactionDTO toDto(Transaction transaction) {
        return TransactionDTO.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUser().getUserId())
                .username(transaction.getUser().getUsername())
                .bookId(transaction.getBook().getBookId())
                .bookTitle(transaction.getBook().getTitle())
                .type(transaction.getType())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}