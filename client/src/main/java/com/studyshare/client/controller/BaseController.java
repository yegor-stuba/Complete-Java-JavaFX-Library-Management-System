package com.studyshare.client.controller;

import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.ErrorHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public abstract class BaseController {
    private static final Logger log = LoggerFactory.getLogger(BookServiceImpl.class);
    @FXML
    protected Label connectionStatus;

    public void updateConnectionStatus(boolean isConnected) {
        if (connectionStatus != null) {
            connectionStatus.setText(isConnected ? "Connected" : "Offline");
            connectionStatus.getStyleClass().setAll("connection-status",
                isConnected ? "connected" : "disconnected");
        }
    }

protected <T> CompletableFuture<T> handleAsync(CompletableFuture<T> future) {
    return future.exceptionally(throwable -> {
        ErrorHandler.handle(throwable);
        return null;
    });
}

    public void initData(Object data) {
    }
}