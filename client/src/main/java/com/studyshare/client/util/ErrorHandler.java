package com.studyshare.client.util;

import com.studyshare.client.service.exception.RestClientException;
import jakarta.validation.ValidationException;
import javafx.application.Platform;
import java.util.concurrent.CompletionException;

public class ErrorHandler {
    public static void handle(Throwable throwable) {
        // Log full stack trace
        System.err.println("Error details:");
        throwable.printStackTrace();

        Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;

        Platform.runLater(() -> {
            if (cause instanceof RestClientException) {
                RestClientException restError = (RestClientException) cause;
                handleRestError(restError);
            } else {
                AlertUtil.showError("System Error",
                    "Type: " + cause.getClass().getSimpleName() + "\n" +
                    "Message: " + cause.getMessage());
            }
        });
    }

    private static void handleRestError(RestClientException error) {
        String message = switch (error.getStatusCode()) {
            case 401 -> "Invalid credentials. Please check username and password.";
            case 403 -> "Access denied. Insufficient permissions.";
            case 404 -> "Resource not found: " + error.getErrorBody();
            default -> "Server error: " + error.getErrorBody();
        };
        AlertUtil.showError("Server Error", message);
    }    public static void handleException(Throwable throwable) {
        if (throwable instanceof ValidationException) {
            AlertUtil.showWarning("Validation Error", throwable.getMessage());
        } else {
            AlertUtil.showError("Error", "An unexpected error occurred: " + throwable.getMessage());
        }
    }
}