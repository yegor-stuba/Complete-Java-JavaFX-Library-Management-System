package com.studyshare.client.service;

import com.studyshare.common.dto.BookDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BookService {
    CompletableFuture<List<BookDTO>> getAllBooks();
    CompletableFuture<List<BookDTO>> getBorrowedBooks();
    CompletableFuture<List<BookDTO>> getLentBooks();
    CompletableFuture<BookDTO> getBookById(Long id);
    CompletableFuture<BookDTO> createBook(BookDTO book);
    CompletableFuture<BookDTO> updateBook(Long id, BookDTO book);
    CompletableFuture<Void> deleteBook(Long id);
    CompletableFuture<List<BookDTO>> searchBooks(String query);
    CompletableFuture<BookDTO> borrowBook(Long bookId);
CompletableFuture<BookDTO> returnBook(Long bookId);
CompletableFuture<Long> getBookCount();
CompletableFuture<BookDTO> addBook(BookDTO bookDTO);
    CompletableFuture<BookDTO> registerBook(BookDTO book);
}