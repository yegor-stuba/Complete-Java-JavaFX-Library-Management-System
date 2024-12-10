package com.studyshare.client.util;

import com.studyshare.client.service.exception.BookOperationException;
import com.studyshare.client.service.exception.RestClientException;
import com.studyshare.client.service.exception.UserOperationException;
import jakarta.validation.ValidationException;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionException;

public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);


    public static void handle(Throwable throwable) {
        Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;

        Platform.runLater(() -> {
            if (cause instanceof RestClientException) {
                handleRestError((RestClientException) cause);
            } else if (cause instanceof ValidationException) {
                AlertUtil.showWarning("Validation Error", cause.getMessage());
            } else {
                AlertUtil.showError("System Error",
                        "Type: " + cause.getClass().getSimpleName() + "\n" +
                                "Message: " + cause.getMessage());
            }
        });
    }

    private static void handleRestError(RestClientException error) {
        String message = switch (error.getStatusCode()) {
            case 400 -> "Invalid request: " + error.getErrorBody();
            case 401 -> "Authentication required";
            case 403 -> "Access denied";
            case 404 -> "Resource not found";
            default -> "Error " + error.getStatusCode() + ": " + error.getErrorBody();
        };
        AlertUtil.showError("Server Error", message);
    }



public static void handleException(Throwable throwable) {
        if (throwable instanceof ValidationException) {
            AlertUtil.showWarning("Validation Error", throwable.getMessage());
        } else {
            AlertUtil.showError("Error", "An unexpected error occurred: " + throwable.getMessage());
        }
    }
    private static void handleSecurityError(SecurityException ex) {
        log.error("Security error: {}", ex.getMessage());
        Platform.runLater(() -> AlertUtil.showError("Security Error",
                "Access denied: " + ex.getMessage()));
    }

    private static void handleGenericError(Throwable throwable) {
        log.error("Unexpected error: ", throwable);
        Platform.runLater(() -> AlertUtil.showError("Error",
                "An unexpected error occurred: " + throwable.getMessage()));
    }
    private static String handleBookError(BookOperationException e) {
        return String.format("Book operation '%s' failed for book ID %d: %s",
                e.getOperation(), e.getBookId(), e.getMessage());
    }

    private static String handleUserError(UserOperationException e) {
        return String.format("User operation '%s' failed for user ID %d: %s",
                e.getOperation(), e.getUserId(), e.getMessage());
    }
}