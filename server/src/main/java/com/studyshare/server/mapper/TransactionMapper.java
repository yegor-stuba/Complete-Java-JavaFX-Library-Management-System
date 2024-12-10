package com.studyshare.server.mapper;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.server.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final BookMapper bookMapper;

    public TransactionDTO toDto(Transaction transaction) {
        return TransactionDTO.builder()
            .transactionId(transaction.getTransactionId())
            .userId(transaction.getUser().getUserId())
            .bookId(transaction.getBook().getBookId())
            .bookTitle(transaction.getBook().getTitle())
            .type(transaction.getType())
            .date(transaction.getDate())
            .dueDate(transaction.getDueDate())
            .active(transaction.isActive())
            .build();
    }

    public Transaction toEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setType(dto.getType());
        transaction.setDate(LocalDateTime.now());
        transaction.setDueDate(dto.getDueDate());
        transaction.setActive(true);
        return transaction;
    }

    public List<TransactionDTO> toDtoList(List<Transaction> transactions) {
        return transactions.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
}