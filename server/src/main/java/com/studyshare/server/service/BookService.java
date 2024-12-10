package com.studyshare.server.service;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.server.model.Book;

import java.util.List;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    BookDTO getBookById(Long id);
    List<BookDTO> getAllBooks();
    List<BookDTO> getLentBooks();
    List<BookDTO> searchBooks(String query);
    Long getBookCount();
    List<BookDTO> getAvailableBooks();
    List<Book> getBooksByUserId(Long userId);
    BookDTO updateBookStatus(Long id, boolean available);
    void validateBookAvailability(Long bookId);
    BookDTO updateBook(Long id, BookDTO bookDTO);
void deleteBook(Long id);
    List<BookDTO> getBorrowedBooks(Long userId);
}