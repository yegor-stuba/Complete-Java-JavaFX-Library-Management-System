package com.studyshare.server.controller;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.server.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @PostMapping
public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
    return ResponseEntity.ok(bookService.createBook(bookDTO));
}

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.getBook(bookId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(bookService.searchBooks(query));
    }

    @PostMapping("/{bookId}/borrow")
    public ResponseEntity<BookDTO> borrowBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.borrowBook(bookId));
    }

    @PostMapping("/{bookId}/return")
    public ResponseEntity<BookDTO> returnBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookService.returnBook(bookId));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<List<BookDTO>> getBorrowedBooks() {
        return ResponseEntity.ok(bookService.getBorrowedBooks());
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }


    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok().build();
    }
}