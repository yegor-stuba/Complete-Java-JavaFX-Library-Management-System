package com.studyshare.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private Integer availableCopies;
    private Long ownerId;
    private boolean available;

    public String getStatusDisplay() {
        return available ? "Available" : "Borrowed";
    }
}