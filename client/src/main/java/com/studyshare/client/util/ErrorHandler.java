package com.studyshare.client.util;

import com.studyshare.client.service.exception.RestClientException;
import jakarta.validation.ValidationException;
import javafx.application.Platform;
import java.util.concurrent.CompletionException;

public class ErrorHandler {
    public static void handle(Throwable throwable) {
        Throwable cause = throwable instanceof CompletionException ? throwable.getCause() : throwable;

        Platform.runLater(() -> {
            if (cause instanceof RestClientException) {
                RestClientException restError = (RestClientException) cause;
                switch (restError.getStatusCode()) {
                    case 401:
                        AlertUtil.showError("Authentication Error", "Please log in again");
                        break;
                    case 403:
                        AlertUtil.showError("Access Denied", "You don't have permission for this action");
                        break;
                    case 404:
                        AlertUtil.showError("Not Found", "The requested resource was not found");
                        break;
                    default:
                        AlertUtil.showError("Error", restError.getErrorBody());
                }
            } else {
                AlertUtil.showError("System Error", "An unexpected error occurred");
            }
        });
    }
    public static void handleException(Throwable throwable) {
        if (throwable instanceof ValidationException) {
            AlertUtil.showWarning("Validation Error", throwable.getMessage());
        } else {
            AlertUtil.showError("Error", "An unexpected error occurred: " + throwable.getMessage());
        }
    }
}
