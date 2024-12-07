package com.studyshare.common.dto;

import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Long transactionId;
    private Long userId;
    private Long bookId;
    private String bookTitle;  // Added field
    private TransactionType type;
    private LocalDateTime date;
    private LocalDateTime dueDate;
    private boolean completed;  // Added field
    private BookDTO book;      // Added field for book details
}