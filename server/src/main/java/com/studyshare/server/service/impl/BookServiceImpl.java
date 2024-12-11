package com.studyshare.server.service.impl;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.mapper.BookMapper;
import com.studyshare.server.mapper.UserMapper;
import com.studyshare.server.model.Book;
import com.studyshare.server.model.User;
import com.studyshare.server.repository.BookRepository;
import com.studyshare.server.repository.UserRepository;
import com.studyshare.server.service.BookService;
import com.studyshare.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final UserService userService;


    @Override
    public void validateBookAvailability(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        if (!book.isAvailable()) {
            throw new ValidationException("Book is not available");
        }
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }


@Override
@Transactional
public BookDTO borrowBook(Long bookId, Long userId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    if (!book.isAvailable()) {
        throw new ValidationException("Book is not available");
    }

    User borrower = userService.getCurrentUserEntity();
    book.setBorrower(borrower);
    book.setAvailable(false);

    return bookMapper.toDto(bookRepository.save(book));
}

@Override
@Transactional
public BookDTO returnBook(Long bookId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    book.setBorrower(null);
    book.setAvailable(true);

    return bookMapper.toDto(bookRepository.save(book));
}

    @Override
    public List<BookDTO> getBorrowedBooks(Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        User user = userMapper.toEntity(userDTO);
        List<Book> books = bookRepository.findByBorrowerAndAvailableFalse(user);
        return bookMapper.toDtoList(books);
    }

    @Override
    public List<BookDTO> getLentBooks() {
        User currentUser = userService.getCurrentUserEntity();
        return bookMapper.toDtoList(
            bookRepository.findByOwnerAndAvailableFalse(currentUser)
        );
    }

    @Override
    public Long getBookCount() {
        return bookRepository.count();
    }

    @Override
    public List<BookDTO> getAvailableBooks() {
        return bookMapper.toDtoList(
                bookRepository.findByAvailableTrue()
        );
    }

    @Override
    public List<Book> getBooksByUserId(Long userId) {
        return bookRepository.findByOwner_UserId(userId);
    }

    @Override
public BookDTO getBookById(Long id) {
    return bookMapper.toDto(
        bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"))
    );
}
@Override
public List<BookDTO> searchBooks(String query) {
    return bookMapper.toDtoList(
        bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query)
    );
}
@Override
public void deleteBook(Long id) {
    bookRepository.deleteById(id);
}



@Override
public BookDTO createBook(BookDTO bookDTO) {
    log.debug("Creating new book: {}", bookDTO);
    Book book = bookMapper.toEntity(bookDTO);
    book.setAvailable(true);
    Book savedBook = bookRepository.save(book);
    return bookMapper.toDto(savedBook);
}

@Override
public BookDTO updateBook(Long id, BookDTO bookDTO) {
    Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    existingBook.setTitle(bookDTO.getTitle());
    existingBook.setAuthor(bookDTO.getAuthor());
    existingBook.setIsbn(bookDTO.getIsbn());
    existingBook.setAvailable(bookDTO.isAvailable());

    return bookMapper.toDto(bookRepository.save(existingBook));
}

@Override
public BookDTO updateBookStatus(Long id, boolean available) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    book.setAvailable(available);
    return bookMapper.toDto(bookRepository.save(book));
}
}