package com.studyshare.client.service;

import com.studyshare.common.dto.BookDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BookService {
    CompletableFuture<List> getAllBooks();
    CompletableFuture<BookDTO> getBookById(Long id);
    CompletableFuture<BookDTO> addBook(BookDTO book);
    CompletableFuture<BookDTO> updateBook(Long id, BookDTO book);
    CompletableFuture<Void> deleteBook(Long id);
    CompletableFuture<List> searchBooks(String query);
}