package com.studyshare.server.service.impl;

import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.enums.TransactionType;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.model.Book;
import com.studyshare.server.model.Transaction;
import com.studyshare.server.model.User;
import com.studyshare.server.repository.TransactionRepository;
import com.studyshare.server.repository.BookRepository;
import com.studyshare.server.repository.UserRepository;
import com.studyshare.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public List<TransactionDTO> getUserTransactions(Long userId) {
        return transactionRepository.findByUser_UserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getBookTransactions(Long bookId) {
        return transactionRepository.findByBook_BookId(bookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long getActiveLoansCount() {
        return transactionRepository.countByTypeAndActiveTrue(TransactionType.BORROW);
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        return convertToDTO(transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found")));
    }

    @Override
    public void completeTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        transaction.setActive(false);
        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDTO> getActiveTransactions() {
        return transactionRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        User user = userRepository.findById(transactionDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(transactionDTO.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setType(transactionDTO.getType());
        transaction.setDate(LocalDateTime.now());
        transaction.setDueDate(transactionDTO.getDueDate());
        transaction.setActive(true);

        return convertToDTO(transactionRepository.save(transaction));
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
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
}



