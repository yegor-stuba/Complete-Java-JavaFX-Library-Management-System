package com.studyshare.client.service.impl;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.RestClient;
import com.studyshare.common.dto.BookDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.core.ParameterizedTypeReference;

public class BookServiceImpl implements BookService {
    private final RestClient restClient;

    @Override
public CompletableFuture<Long> getBookCount() {
    return restClient.get("/api/books/count", Long.class);
}

    public BookServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }



    @Override
    public CompletableFuture<BookDTO> addBook(BookDTO book) {
        return restClient.post("/api/books", book, BookDTO.class);
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
        return restClient.post("/api/books/" + bookId + "/borrow", null, BookDTO.class);
    }

    @Override
    public CompletableFuture<BookDTO> returnBook(Long bookId) {
        return restClient.post("/api/books/" + bookId + "/return", null, BookDTO.class);
    }
}