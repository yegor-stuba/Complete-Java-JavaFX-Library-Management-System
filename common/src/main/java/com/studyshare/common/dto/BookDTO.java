package com.studyshare.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class BookDTO {
    private Long bookId;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Author is required")
    private String author;
    private String isbn;
    @Min(value = 0, message = "Available copies must be non-negative")
    private Integer availableCopies;
    private Long ownerId;
    private boolean available;
    private String description;

    @Builder
    public BookDTO(Long bookId, String title, String author, String isbn,
                   Integer availableCopies, Long ownerId, boolean available,
                   String description) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.availableCopies = availableCopies;
        this.ownerId = ownerId;
        this.available = available;
        this.description = description;
    }

    public String getStatusDisplay() {
        return available ? "Available" : "Borrowed";
    }
}