package com.studyshare.client.controller;

import com.studyshare.client.util.ErrorHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.concurrent.CompletableFuture;

public abstract class BaseController {
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
}