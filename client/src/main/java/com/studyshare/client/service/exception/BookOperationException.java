package com.studyshare.client.service.exception;

import lombok.Getter;

@Getter
public class BookOperationException extends RuntimeException {
    private final String operation;
    private final Long bookId;

    // Constructor for specific book operations
    public BookOperationException(String operation, Long bookId, String message) {
        super(message);
        this.operation = operation;
        this.bookId = bookId;
    }

    // Constructor for general book operations
    public BookOperationException(String message) {
        super(message);
        this.operation = "GENERAL";
        this.bookId = null;
    }
}