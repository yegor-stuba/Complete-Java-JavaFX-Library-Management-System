package com.studyshare.server.service;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.server.model.Book;
import jakarta.validation.Valid;

import java.util.List;

public interface BookService {
    BookDTO createBook(BookDTO bookDTO);
    BookDTO updateBook(Long bookId, BookDTO bookDTO);
    void deleteBook(Long bookId);
    List<BookDTO> getAllBooks();
    List<BookDTO> searchBooks(String query);
    Book findById(Long bookId);
    BookDTO borrowBook(Long bookId);
    BookDTO returnBook(Long bookId);

    List<Book> getUserBooks(Long id);
    BookDTO addBook(@Valid BookDTO bookDTO);
    List<BookDTO> getBorrowedBooks();

    BookDTO getBook(Long bookId);
    BookDTO getBookById(Long id);

}
