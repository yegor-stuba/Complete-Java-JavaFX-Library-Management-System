package com.studyshare.server.service;

import com.studyshare.common.dto.BookDTO;
import java.util.List;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    BookDTO getBookById(Long id);
    List<BookDTO> getAllBooks();
    List<BookDTO> getBooksByOwner(Long ownerId);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
    List<BookDTO> searchBooks(String query);
    Long getBookCount();
    boolean isBookAvailable(Long bookId);
    List<BookDTO> getAvailableBooks();
    BookDTO borrowBook(Long bookId, Long userId);
    BookDTO returnBook(Long bookId, Long userId);
}