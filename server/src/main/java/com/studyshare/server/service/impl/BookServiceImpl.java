package com.studyshare.server.service.impl;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.enums.TransactionType;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.mapper.BookMapper;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final UserService userService;
    private final TransactionServiceImpl transactionService;


    @Override
    public BookDTO getBookById(Long id) {
        Book book = findById(id);
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDTO> getBorrowedBooks() {
        User user = userService.getCurrentUserEntity();
        return bookRepository.findByBorrower(user)
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        Book book = bookMapper.toEntity(bookDTO);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }


@Override
public List<Book> getUserBooks(Long userId) {
    return bookRepository.findByBorrowerUserId(userId);
}

@Override
@Transactional
public BookDTO createBook(BookDTO bookDTO) {
    Book book = bookMapper.toEntity(bookDTO);
    book.setBookId(null); // Ensure ID is null for auto-generation
    book.setTotalCopies(bookDTO.getAvailableCopies());
    return bookMapper.toDto(bookRepository.save(book));
}

    @Override
    @Transactional
    public BookDTO updateBook(Long bookId, BookDTO bookDTO) {
        Book book = findById(bookId);
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setAvailableCopies(bookDTO.getAvailableCopies());
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        bookRepository.deleteById(bookId);
    }

    @Override
    public BookDTO getBook(Long bookId) {
        return bookMapper.toDto(findById(bookId));
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query)
                .stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
public BookDTO borrowBook(Long bookId) {
    Book book = bookRepository.findByIdWithLock(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    User user = userService.getCurrentUserEntity();

    if (book.getAvailableCopies() <= 0) {
        throw new ValidationException("No copies available");
    }

    book.setAvailableCopies(book.getAvailableCopies() - 1);
    book.setBorrower(user);
    Book savedBook = bookRepository.save(book);

    transactionService.createTransaction(bookId, TransactionType.BORROW);
    return bookMapper.toDto(savedBook);
}

    @Override
    @Transactional
    public BookDTO returnBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        User user = userService.getCurrentUserEntity();

        if (!user.equals(book.getBorrower())) {
            throw new ValidationException("Book was not borrowed by this user");
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        book.setBorrower(null);
        Book savedBook = bookRepository.saveAndFlush(book);

        // Create transaction after book is saved
        transactionService.createTransaction(bookId, TransactionType.RETURN);

        return bookMapper.toDto(savedBook);
    }
}