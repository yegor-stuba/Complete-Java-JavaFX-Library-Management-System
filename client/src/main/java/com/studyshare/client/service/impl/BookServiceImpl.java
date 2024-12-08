package com.studyshare.client.service.impl;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.exception.BookOperationException;
import com.studyshare.client.service.exception.ConflictException;
import com.studyshare.client.service.exception.ResourceNotFoundException;
import com.studyshare.common.dto.BookDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.ParameterizedTypeReference;

public class BookServiceImpl implements BookService {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
    private final RestClient restClient;

    @Override
public CompletableFuture<Long> getBookCount() {
    return restClient.get("/api/books/count", Long.class);
}

    public BookServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    // Add validation method
    private void validateBookInput(BookDTO book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("Book title is required");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new ValidationException("Book author is required");
        }
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new ValidationException("Book ISBN is required");
        }
    }

    public CompletableFuture<BookDTO> addBook(BookDTO book) {
        validateBookInput(book);
        return restClient.post("/api/books", book, BookDTO.class)
                .exceptionally(throwable -> {
                    log.error("Failed to add book: {}", throwable.getMessage());
                    throw new CompletionException(throwable);
                });
    }

    @Override
    public CompletableFuture<BookDTO> updateBook(Long id, BookDTO book) {
        return restClient.put("/api/books/" + id, book, BookDTO.class);
    }

    @Override
    public CompletableFuture<Void> deleteBook(Long id) {
        return restClient.delete("/api/books/" + id);
    }

  @Override
public CompletableFuture<List<BookDTO>> getAllBooks() {
    return restClient.getList("/api/books", new ParameterizedTypeReference<List<BookDTO>>() {});
}

@Override
public CompletableFuture<BookDTO> getBookById(Long id) {
    return restClient.get("/api/books/" + id, BookDTO.class);
}

@Override
public CompletableFuture<List<BookDTO>> searchBooks(String query) {
    return restClient.getList("/api/books/search?query=" + query,
        new ParameterizedTypeReference<List<BookDTO>>() {});
}

@Override
public CompletableFuture<List<BookDTO>> getAvailableBooks() {
        return restClient.getList("/api/books/available",
            new ParameterizedTypeReference<List<BookDTO>>() {});
}

    @Override
public CompletableFuture<BookDTO> borrowBook(Long bookId) {
    return restClient.post("/api/books/" + bookId + "/borrow", null, BookDTO.class)
        .exceptionally(throwable -> {
            if (throwable instanceof ResourceNotFoundException) {
                throw new BookOperationException("borrow", bookId, "Book not found");
            }
            if (throwable instanceof ConflictException) {
                throw new BookOperationException("borrow", bookId, "Book already borrowed");
            }
            throw new BookOperationException("borrow", bookId, "Failed to borrow book");
        });
}

    @Override
    public CompletableFuture<BookDTO> returnBook(Long bookId) {
        return restClient.post("/api/books/" + bookId + "/return", null, BookDTO.class);
    }
}