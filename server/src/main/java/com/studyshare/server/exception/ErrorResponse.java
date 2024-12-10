package com.studyshare.server.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private String details;

    // Add convenience constructor for simple error messages
    public ErrorResponse(String message) {
        this.code = "ERROR";
        this.message = message;
        this.details = null;
    }

    // Add convenience constructor for error code and message
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.details = null;
    }
}

