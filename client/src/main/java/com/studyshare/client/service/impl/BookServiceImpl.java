package com.studyshare.client.service.impl;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.RestClient;
import com.studyshare.common.dto.BookDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BookServiceImpl implements BookService {
    private final RestClient restClient;

    public BookServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public CompletableFuture<List> getAllBooks() {
        return restClient.get("/api/books", List.class);
    }

    @Override
    public CompletableFuture<BookDTO> getBookById(Long id) {
        return restClient.get("/api/books/" + id, BookDTO.class);
    }

    @Override
    public CompletableFuture<BookDTO> addBook(BookDTO book) {
        return restClient.post("/api/books", book, BookDTO.class);
    }

    @Override
    public CompletableFuture<BookDTO> updateBook(Long id, BookDTO book) {
        return restClient.post("/api/books/" + id, book, BookDTO.class);
    }

    @Override
    public CompletableFuture<Void> deleteBook(Long id) {
        return restClient.post("/api/books/" + id + "/delete", null, Void.class);
    }

    @Override
    public CompletableFuture<List> searchBooks(String query) {
        return restClient.get("/api/books/search?query=" + query, List.class);
    }
}