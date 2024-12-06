package com.studyshare.server.repository;

import com.studyshare.server.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserUserId(Long userId);
    List<Transaction> findByBookBookId(Long bookId);
    List<Transaction> findByUserUserIdAndBookBookId(Long userId, Long bookId);
}