package com.studyshare.client.service.exception;

import lombok.Getter;

@Getter
public class RestClientException extends RuntimeException {
    private final int statusCode;
    private final String errorBody;

    public RestClientException(int statusCode, String errorBody) {
        super("HTTP " + statusCode + ": " + errorBody);
        this.statusCode = statusCode;
        this.errorBody = errorBody;
    }
}
