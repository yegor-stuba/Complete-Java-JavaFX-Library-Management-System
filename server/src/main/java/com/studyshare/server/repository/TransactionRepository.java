package com.studyshare.server.repository;

import com.studyshare.common.enums.TransactionType;
import com.studyshare.server.model.Transaction;
import com.studyshare.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByActiveTrue();
    List<Transaction> findByUser_UserId(Long userId);
    List<Transaction> findByBook_BookId(Long bookId);
    Optional<Transaction> findByBook_BookIdAndActiveTrue(Long bookId);
    Long countByTypeAndActiveTrue(TransactionType type);
    List<Transaction> findByUserOrderByDateDesc(User user);
    Long countByActiveTrue();
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.active = true")
    Long countActiveTransactions();
}