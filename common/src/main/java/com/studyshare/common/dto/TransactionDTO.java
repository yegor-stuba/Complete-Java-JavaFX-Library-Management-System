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
    private String bookTitle;
    private TransactionType type;
    private LocalDateTime date;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private boolean active;
    private boolean completed;
    private BookDTO book;
    private UserDTO user;
}