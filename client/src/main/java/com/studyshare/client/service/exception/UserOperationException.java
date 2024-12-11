package com.studyshare.client.service.exception;

import lombok.Getter;

@Getter
public class UserOperationException extends RuntimeException {
    private final String operation;
    private final Long userId;

    public UserOperationException(String operation, Long userId, String message) {
        super(message);
        this.operation = operation;
        this.userId = userId;
    }

    public String getOperation() {
        return operation;
    }

    public Long getUserId() {
        return userId;
    }
}
