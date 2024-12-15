package com.studyshare.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private Integer availableCopies;
    private Integer totalCopies;
    private UserDTO borrower;

public boolean isAvailable() {
    return availableCopies != null && availableCopies > 0;
}
}