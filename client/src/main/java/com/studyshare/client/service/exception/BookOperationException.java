package com.studyshare.client.service.exception;

import lombok.Getter;

@Getter
public class BookOperationException extends RuntimeException {
    private final String operation;
    private final Long bookId;

    public BookOperationException(String operation, Long bookId, String message) {
        super(message);
        this.operation = operation;
        this.bookId = bookId;
    }
}