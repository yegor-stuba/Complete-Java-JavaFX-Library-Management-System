package com.studyshare.server.service;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.server.model.Book;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    List<BookDTO> getAllBooks();
    List<BookDTO> getLentBooks();
    List<BookDTO> searchBooks(String query);
    Long getBookCount();
    List<BookDTO> getAvailableBooks();
    List<Book> getBooksByUserId(Long userId);
    BookDTO updateBookStatus(Long id, boolean available);
    void validateBookAvailability(Long bookId);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    BookDTO getBookById(Long id);
    void deleteBook(Long id);
    List<BookDTO> getBorrowedBooks(Long userId);
    BookDTO borrowBook(Long bookId, Long userId);
    BookDTO returnBook(Long bookId);


}