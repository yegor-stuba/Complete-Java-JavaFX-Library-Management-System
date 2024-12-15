package com.studyshare.server.repository;

import com.studyshare.server.model.Book;
import com.studyshare.server.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    List<Book> findByBorrower(User borrower);
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    List<Book> findByBorrowerUserId(Long borrowerUserId);

    void deleteByBorrowerAndIsbn(User borrower, String isbn);

@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT b FROM Book b WHERE b.bookId = :id")
Optional<Book> findByIdWithLock(@Param("id") Long id);

    List<Book> findByBorrowerIsNull();

    @Query("SELECT b FROM Book b WHERE b.title LIKE %:query% OR b.author LIKE %:query% OR b.isbn LIKE %:query%")
    List<Book> searchBooks(@Param("query") String query);

    boolean existsByBorrower(User user);
}