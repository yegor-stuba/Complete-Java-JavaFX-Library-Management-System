package com.studyshare.server.mapper;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.server.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {
    public BookDTO toDto(Book book) {
        return BookDTO.builder()
            .bookId(book.getBookId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .isbn(book.getIsbn())
            .availableCopies(book.getAvailableCopies())
            .ownerId(book.getOwner() != null ? book.getOwner().getUserId() : null)
            .build();
    }

   public Book toEntity(BookDTO dto) {
    Book book = new Book();
    book.setTitle(dto.getTitle());
    book.setAuthor(dto.getAuthor());
    book.setIsbn(dto.getIsbn());
    book.setAvailableCopies(dto.getAvailableCopies());
    book.setAvailable(true);
    return book;
}

    public List<BookDTO> toDtoList(List<Book> books) {
        return books.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
}
