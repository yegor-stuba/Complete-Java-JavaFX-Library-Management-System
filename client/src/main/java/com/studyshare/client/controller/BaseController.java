package com.studyshare.client.controller;

import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.AlertUtil;
import com.studyshare.client.util.ErrorHandler;
import com.studyshare.common.dto.BookDTO;
import com.studyshare.common.dto.TransactionDTO;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public abstract class BaseController {
    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    @FXML
    protected Label connectionStatus;

    protected <T> CompletableFuture<T> handleAsync(CompletableFuture<T> future) {
        return future.exceptionally(throwable -> {
            Platform.runLater(() -> handleError(throwable));
            return null;
        });
    }

    protected void handleError(Throwable throwable) {
        log.error("Operation failed: {}", throwable.getMessage());
        AlertUtil.showError("Error", throwable.getMessage());
    }

    public void initData(Object data) {
        if (data instanceof TransactionDTO) {
            handleTransactionData((TransactionDTO) data);
        } else if (data instanceof BookDTO) {
            handleBookData((BookDTO) data);
        }
    }

    protected void handleTransactionData(TransactionDTO transaction) {
        Platform.runLater(() -> {
            if (transaction != null) {
                logTransaction(transaction);
            }
        });
    }

    protected void handleBookData(BookDTO book) {
        Platform.runLater(() -> {
            if (book != null) {
                logBookAction(book);
            }
        });
    }

    protected void logTransaction(TransactionDTO transaction) {
        log.info("Transaction: {} - Book: {} - Type: {} - Time: {}",
            transaction.getId(),
            transaction.getBook().getTitle(),
            transaction.getType(),
            transaction.getTimestamp());
    }

    protected void logBookAction(BookDTO book) {
        log.info("Book Action: {} - Title: {} - Available: {}",
            book.getBookId(),
            book.getTitle(),
            book.isAvailable());
    }

    public void updateConnectionStatus(boolean isConnected) {
        if (connectionStatus != null) {
            Platform.runLater(() -> {
                connectionStatus.setText(isConnected ? "Connected" : "Offline");
                connectionStatus.getStyleClass().setAll("connection-status",
                    isConnected ? "connected" : "disconnected");
            });
        }
    }
}