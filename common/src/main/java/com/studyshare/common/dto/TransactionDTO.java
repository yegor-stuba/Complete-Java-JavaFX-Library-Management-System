package com.studyshare.common.dto;

import com.studyshare.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private Long bookId;
    private String actionType;
    private String description;
    private LocalDateTime timestamp;
    private String username;
    private String details;
    private BookDTO book;
    private UserDTO user;
    private TransactionType type;
    private LocalDateTime dueDate;
    private String status;
    private Long transactionId;
    private LocalDateTime date;
    private boolean active;
    private LocalDateTime returnDate;

public TransactionDTO(Object o, Long bookId, TransactionType type) {
    this.bookId = bookId;
    this.type = type;
    this.timestamp = LocalDateTime.now();
}
}
