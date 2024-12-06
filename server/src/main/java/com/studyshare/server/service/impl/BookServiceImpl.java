package com.studyshare.server.service.impl;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.model.Book;
import com.studyshare.server.model.User;
import com.studyshare.server.repository.BookRepository;
import com.studyshare.server.repository.UserRepository;
import com.studyshare.server.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
public BookDTO createBook(BookDTO bookDTO) {
    Book book = new Book();
    book.setTitle(bookDTO.getTitle());
    book.setAuthor(bookDTO.getAuthor());
    book.setIsbn(bookDTO.getIsbn());
    book.setAvailableCopies(bookDTO.getAvailableCopies());

    // Get current user as owner
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    book.setOwner(currentUser);

    Book savedBook = bookRepository.save(book);
    return convertToDTO(savedBook);
}
    @Override
    public BookDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> getBooksByOwner(Long ownerId) {
        return bookRepository.findByOwnerUserId(ownerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setAvailableCopies(bookDTO.getAvailableCopies());

        return convertToDTO(bookRepository.save(book));
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDTO> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setOwnerId(book.getOwner().getUserId());
        return dto;
    }
}