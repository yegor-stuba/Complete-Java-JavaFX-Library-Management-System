package com.studyshare.server.repository;

import com.studyshare.server.model.Book;
import com.studyshare.server.model.Transaction;
import com.studyshare.server.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser_UserId(Long userId);
    List<Transaction> findByBook(Book book);
    Optional<Transaction> findFirstByBookOrderByTransactionDateDesc(Book book);
    Page<Transaction> findAllByOrderByTransactionDateDesc(Pageable pageable);

    @Query("SELECT t FROM Transaction t ORDER BY t.transactionDate DESC")
    Page<Transaction> findAllPaged(Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.user.userId = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdOrderByTransactionDateDesc(@Param("userId") Long userId);



}