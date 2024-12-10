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
import com.studyshare.server.service.TransactionService;
import com.studyshare.server.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    @Transactional
    public TransactionDTO borrowBook(Long bookId) {
        BookDTO book = bookService.getBookById(bookId);
        if (!book.isAvailable()) {
            throw new ValidationException("Book is not available for borrowing");
        }

        Transaction transaction = new Transaction();

        transaction.setUser(userService.getCurrentUserEntity());
        transaction.setType(TransactionType.BORROW);
        transaction.setDate(LocalDateTime.now());
        transaction.setDueDate(LocalDateTime.now().plusDays(14));
        transaction.setActive(true);

        bookService.updateBookStatus(bookId, false);
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

@Override
public List<TransactionDTO> getBookTransactions(Long bookId) {
    return transactionMapper.toDtoList(
        transactionRepository.findByBook_BookId(bookId)
    );
}
    public List<TransactionDTO> findByBook_BookId(Long bookId) {
        return transactionMapper.toDtoList(transactionRepository.findByBook_BookId(bookId));
    }

    public Optional<Transaction> findByBook_BookIdAndActiveTrue(Long bookId) {
        return transactionRepository.findByBook_BookIdAndActiveTrue(bookId);
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


    public List<TransactionDTO> getUserTransactions() {
        User currentUser = userService.getCurrentUserEntity();
        return transactionMapper.toDtoList(
            transactionRepository.findByUserOrderByDateDesc(currentUser)
        );
    }

    @Override
    public List<TransactionDTO> getActiveTransactions() {
        return transactionMapper.toDtoList(
            transactionRepository.findByActiveTrue()
        );
    }

    @Override
    public Long getActiveLoansCount() {
        return transactionRepository.countByActiveTrue();
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
public TransactionDTO completeTransaction(Long transactionId) {
    Transaction transaction = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    transaction.setActive(false);
    transaction.setReturnDate(LocalDateTime.now());
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


}