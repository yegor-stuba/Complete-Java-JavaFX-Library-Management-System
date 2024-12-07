package com.studyshare.client.service.exception;

public class RestClientException extends RuntimeException {
    private final int statusCode;
    private final String errorBody;

    public RestClientException(int statusCode, String errorBody) {
        this.statusCode = statusCode;
        this.errorBody = errorBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorBody() {
        return errorBody;
    }
}
