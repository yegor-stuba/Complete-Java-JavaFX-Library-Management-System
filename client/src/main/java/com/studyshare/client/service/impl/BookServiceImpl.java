package com.studyshare.client.service.impl;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.RestClient;
import com.studyshare.common.dto.BookDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final RestClient restClient;
    private static final String BASE_PATH = "/api/books";

    @Override
    public CompletableFuture<List<BookDTO>> getAllBooks() {
        return restClient.getList(BASE_PATH, new ParameterizedTypeReference<List<BookDTO>>() {});
    }

    @Override
    public CompletableFuture<List<BookDTO>> getBorrowedBooks() {
        return restClient.getList(BASE_PATH + "/borrowed", new ParameterizedTypeReference<List<BookDTO>>() {});
    }

    @Override
    public CompletableFuture<BookDTO> getBookById(Long id) {
        return restClient.get(BASE_PATH + "/" + id, BookDTO.class);
    }

    @Override
    public CompletableFuture<BookDTO> updateBook(Long id, BookDTO book) {
        return restClient.put(BASE_PATH + "/" + id, book, BookDTO.class);
    }

    @Override
    public CompletableFuture<Void> deleteBook(Long id) {
        return restClient.delete(BASE_PATH + "/" + id);
    }

    @Override
    public CompletableFuture<List<BookDTO>> searchBooks(String query) {
        return restClient.getList(BASE_PATH + "/search?query=" + query,
            new ParameterizedTypeReference<List<BookDTO>>() {});
    }

 @Override
public CompletableFuture<BookDTO> borrowBook(Long bookId) {
    return restClient.post(BASE_PATH + "/" + bookId + "/borrow", null, BookDTO.class)
        .thenCompose(book -> {
            // Refresh both tables after successful borrow
            return CompletableFuture.allOf(
                getAllBooks(),
                getBorrowedBooks()
            ).thenApply(v -> book);
        });
}

    @Override
    public CompletableFuture<BookDTO> returnBook(Long bookId) {
        return restClient.post(BASE_PATH + "/" + bookId + "/return", null, BookDTO.class);
    }

    @Override
    public CompletableFuture<Long> getBookCount() {
        return restClient.get(BASE_PATH + "/count", Long.class);
    }

    @Override
    public CompletableFuture<BookDTO> addBook(BookDTO bookDTO) {
        return restClient.post(BASE_PATH, bookDTO, BookDTO.class);
    }
}