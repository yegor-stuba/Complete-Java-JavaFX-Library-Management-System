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
    private Long transactionId;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private TransactionType type;
    private LocalDateTime transactionDate;
}