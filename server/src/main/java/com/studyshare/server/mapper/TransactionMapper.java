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
    private final UserMapper userMapper;

    public TransactionDTO toDto(Transaction transaction) {
        return TransactionDTO.builder()
                .transactionId(transaction.getTransactionId())
                .user(userMapper.toDto(transaction.getUser()))
                .book(bookMapper.toDto(transaction.getBook()))
                .type(transaction.getType())
                .date(transaction.getDate())
                .active(transaction.isActive())
                .dueDate(transaction.getDueDate())
                .returnDate(transaction.getReturnDate())
                .build();
    }

    public Transaction toEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setUser(userMapper.toEntity(dto.getUser()));
        transaction.setBook(bookMapper.toEntity(dto.getBook()));
        transaction.setType(dto.getType());
        transaction.setDate(dto.getDate());
        transaction.setActive(dto.isActive());
        transaction.setDueDate(dto.getDueDate());
        transaction.setReturnDate(dto.getReturnDate());
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