package com.studyshare.server.repository;

import com.studyshare.server.model.Book;
import com.studyshare.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    List<Book> findByAvailableCopiesGreaterThan(Integer copies);

    boolean existsByIsbn(String isbn);  // Changed from existsByIsbn

    Optional<Book> findByIsbn(String isbn);


    List<Book> findByOwnerAndAvailableFalse(User owner);

    Long countByAvailableTrue();

    List<Book> findByAvailableTrue();

    List<Book> findByOwner_UserId(Long userId);
    List<Book> findByBorrowerAndAvailableFalse(User borrower);

}
