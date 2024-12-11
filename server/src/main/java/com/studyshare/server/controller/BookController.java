package com.studyshare.server.controller;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.model.Book;
import com.studyshare.server.model.User;
import com.studyshare.server.service.BookService;
import com.studyshare.server.service.UserService;
import com.studyshare.server.validation.BookValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final UserService userService;
    private final BookValidator bookValidator;
    private static final Logger log = LoggerFactory.getLogger(BookController.class);


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(bookValidator);
    }


    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        log.info("Fetching all books");
        List<BookDTO> books = bookService.getAllBooks();
        log.info("Found {} books", books.size());
        try {
            return ResponseEntity.ok(bookService.getAllBooks());
        } catch (Exception e) {
            log.error("Error fetching books: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
        try {
            return ResponseEntity.ok(bookService.createBook(bookDTO));
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, bookDTO));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String query) {
        return ResponseEntity.ok(bookService.searchBooks(query));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getBookCount() {
        return ResponseEntity.ok(bookService.getBookCount());
    }

    @GetMapping("/available")
    public ResponseEntity<List<BookDTO>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }


    @GetMapping("/borrowed/{userId}")
    public ResponseEntity<List<BookDTO>> getBorrowedBooks(@PathVariable Long userId) {
        return ResponseEntity.ok(bookService.getBorrowedBooks(userId));
    }

    @GetMapping("/lent")
    public ResponseEntity<List<BookDTO>> getLentBooks() {
        return ResponseEntity.ok(bookService.getLentBooks());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Book>> getBooksByUserId(@PathVariable Long ownerId) {
        userService.getUserById(ownerId);
        return ResponseEntity.ok(bookService.getBooksByUserId(ownerId));
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDTO user = userService.findByUsername(username);
        return user.getUserId();
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ex.getMessage());
    }

    @PostMapping("/register")
public ResponseEntity<BookDTO> registerBook(@Valid @RequestBody BookDTO bookDTO) {
    try {
        bookDTO.setOwnerId(getCurrentUserId());
        return ResponseEntity.ok(bookService.createBook(bookDTO));
    } catch (ValidationException e) {
        return ResponseEntity.badRequest().build();
    }
}
}