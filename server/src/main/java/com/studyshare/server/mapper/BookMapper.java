package com.studyshare.server.mapper;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
   public BookDTO toDto(Book book) {
    return BookDTO.builder()
            .bookId(book.getBookId())
            .title(book.getTitle())
            .author(book.getAuthor())
            .isbn(book.getIsbn())
            .availableCopies(book.getAvailableCopies())
            .totalCopies(book.getTotalCopies())
            .borrower(book.getBorrower() != null ?
                UserDTO.builder()
                    .userId(book.getBorrower().getUserId())
                    .username(book.getBorrower().getUsername())
                    .build() : null)
            .build();
}

    public Book toEntity(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setTotalCopies(dto.getTotalCopies());
        return book;
    }
}