package com.studyshare.server.service.impl;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.TransactionType;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.mapper.TransactionMapper;
import com.studyshare.server.model.Book;
import com.studyshare.server.model.Transaction;
import com.studyshare.server.model.User;
import com.studyshare.server.repository.BookRepository;
import com.studyshare.server.repository.TransactionRepository;
import com.studyshare.server.service.BookService;
import com.studyshare.server.service.TransactionService;
import com.studyshare.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final TransactionMapper transactionMapper;



    @Override
    public Long getTransactionCount() {
        return transactionRepository.count();
    }

@Override
@Transactional
public TransactionDTO createTransaction(Long bookId, TransactionType type) {
    User currentUser = userService.getCurrentUserEntity();
    Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    // Update book status first
    if (type == TransactionType.BORROW) {
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        book.setBorrower(currentUser);
    } else {
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setBorrower(null);
    }
    bookRepository.save(book);

    // Create transaction record
    Transaction transaction = Transaction.builder()
            .user(currentUser)
            .book(book)
            .type(type)
            .transactionDate(LocalDateTime.now())
            .build();

    Transaction savedTransaction = transactionRepository.save(transaction);
    return transactionMapper.toDto(savedTransaction);
}


    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAllByOrderByTransactionDateDesc(PageRequest.of(0, Integer.MAX_VALUE))
                .stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

public List<TransactionDTO> getUserTransactions() {
    User currentUser = userService.getCurrentUserEntity();
    return transactionRepository.findByUserIdOrderByTransactionDateDesc(currentUser.getUserId())
            .stream()
            .map(transactionMapper::toDto)
            .collect(Collectors.toList());
}

    @Override
    public List<TransactionDTO> getTransactions(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        return transactionRepository.findAllByOrderByTransactionDateDesc(pageRequest)
                .map(transactionMapper::toDto)
                .getContent();
    }


    public List<TransactionDTO> getCurrentUserTransactions() {
        User currentUser = userService.getCurrentUserEntity();
        return transactionRepository.findByUser_UserId(currentUser.getUserId())
                .stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }



    @Override
    public TransactionDTO getTransaction(Long transactionId) {
        return transactionMapper.toDto(
                transactionRepository.findById(transactionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"))
        );
    }

}