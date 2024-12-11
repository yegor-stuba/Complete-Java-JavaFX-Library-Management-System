package com.studyshare.server.service.impl;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.TransactionType;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.mapper.TransactionMapper;
import com.studyshare.server.model.Book;
import com.studyshare.server.model.Transaction;
import com.studyshare.server.model.User;
import com.studyshare.server.repository.TransactionRepository;
import com.studyshare.server.repository.BookRepository;
import com.studyshare.server.repository.UserRepository;
import com.studyshare.server.service.BookService;
import com.studyshare.server.service.SecurityAuditService;
import com.studyshare.server.service.TransactionService;
import com.studyshare.server.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final BookService bookService;
    private final UserService userService;
    private final TransactionMapper transactionMapper;
      private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final SecurityAuditService securityAuditService;

    @Override
    @Transactional
    public TransactionDTO createTransaction(Long bookId, Long userId, TransactionType type) {
        User user = userService.getCurrentUserEntity();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setType(type);
        transaction.setDate(LocalDateTime.now());
        transaction.setActive(true);

        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionDTO borrowBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        BookDTO bookDTO = bookService.getBookById(bookId);
        if (!bookDTO.isAvailable()) {
            throw new ValidationException("Book is not available for borrowing");
        }

        Transaction transaction = new Transaction();
        transaction.setBook(book);
        transaction.setUser(userService.getCurrentUserEntity());
        transaction.setType(TransactionType.BORROW);
        transaction.setDate(LocalDateTime.now());
        transaction.setDueDate(LocalDateTime.now().plusDays(14));
        transaction.setActive(true);

        bookService.updateBookStatus(bookId, false);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionDTO returnBook(Long bookId) {
        Transaction activeTransaction = transactionRepository
            .findByBook_BookIdAndActiveTrue(bookId)
            .orElseThrow(() -> new ValidationException("No active borrowing found for this book"));

        activeTransaction.setActive(false);
        activeTransaction.setReturnDate(LocalDateTime.now());

        bookService.updateBookStatus(bookId, true);
        return transactionMapper.toDto(transactionRepository.save(activeTransaction));
    }

    @Override
public TransactionDTO createTransaction(Long bookId, TransactionType type) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    Transaction transaction = new Transaction();
    transaction.setBook(book);
    transaction.setUser(userService.getCurrentUserEntity());
    transaction.setType(type);
    transaction.setDate(LocalDateTime.now());

    return transactionMapper.toDto(transactionRepository.save(transaction));
}

@Override
public TransactionDTO getTransactionById(Long id) {
    return transactionMapper.toDto(
        transactionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"))
    );
}

@Override
public List<TransactionDTO> getUserTransactions(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return transactionMapper.toDtoList(
        transactionRepository.findByUserOrderByDateDesc(user)
    );
}


    @Override
    public List<TransactionDTO> getBookTransactions(Long bookId) {
        return transactionRepository.findByBook_BookId(bookId).stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getActiveTransactionsCount() {
        return transactionRepository.countByActiveTrue();
    }


    @Override
    @Transactional
    public TransactionDTO completeTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transaction.setActive(false);
        transaction.setReturnDate(LocalDateTime.now());
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    @Override
    public List<TransactionDTO> getActiveTransactions() {
        return transactionRepository.findByActiveTrue().stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getActiveLoansCount() {
        return transactionRepository.countByTypeAndActiveTrue(TransactionType.BORROW);
    }
}