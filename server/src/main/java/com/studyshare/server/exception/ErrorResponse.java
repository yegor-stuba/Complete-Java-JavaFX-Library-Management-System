package com.studyshare.server.exception;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
    String message;
    String details;
    int status;

    public static ErrorResponse of(String message, String details, int status) {
        return ErrorResponse.builder()
            .message(message)
            .details(details)
            .status(status)
            .build();
    }
}
