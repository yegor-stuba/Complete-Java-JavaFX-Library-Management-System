package com.studyshare.server.mapper;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.model.Book;
import com.studyshare.server.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

public UserDTO toDto(User user) {
    UserDTO dto = new UserDTO();
    dto.setUserId(user.getUserId());
    dto.setUsername(user.getUsername());
    dto.setEmail(user.getEmail());
    dto.setRole(user.getRole());
    return dto;
}

public User toEntity(UserDTO dto) {
    User user = new User();
    user.setUserId(dto.getUserId());
    user.setUsername(dto.getUsername());
    user.setEmail(dto.getEmail());
    user.setPassword(dto.getPassword());
    user.setRole(dto.getRole());
    return user;
}

    public void updateEntity(User user, UserDTO dto) {
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());
        if (dto.getRole() != null) user.setRole(dto.getRole());
    }

    public List<UserDTO> toDtoList(List<User> users) {
        return users.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    public Book mapBookDtoToEntity(BookDTO dto) {
        Book book = new Book();
        book.setBookId(dto.getBookId());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setIsbn(dto.getIsbn());
        book.setAvailableCopies(dto.getAvailableCopies());
        return book;
    }

    public BookDTO mapBookToDto(Book book) {
        BookDTO dto = new BookDTO();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setAvailableCopies(book.getAvailableCopies());
        if (book.getOwner() != null) {
            dto.setOwnerId(book.getOwner().getUserId());
        }
        return dto;
    }

}
