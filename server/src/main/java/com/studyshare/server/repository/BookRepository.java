package com.studyshare.server.repository;

import com.studyshare.server.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByOwnerUserId(Long ownerId);  // Changed from findByOwnerId
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);
    List<Book> findByAvailableCopiesGreaterThan(Integer copies);
    boolean existsByIsbn(String isbn);  // Changed from existsByIsbn
    Optional<Book> findByIsbn(String isbn);
}