package com.studyshare.server.mapper;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.server.model.Book;
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
                .id(transaction.getTransactionId())
                .bookId(transaction.getBook().getBookId())
                .book(bookMapper.toDto(transaction.getBook()))
                .type(transaction.getType())
                .timestamp(transaction.getDate())
                .dueDate(transaction.getDueDate())
                .status(transaction.isActive() ? "ACTIVE" : "COMPLETED")
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

    public BookDTO mapBookToDto(Book book) {
        return BookDTO.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .availableCopies(book.getAvailableCopies())
                .description(book.getDescription())
                .build();
    }

}