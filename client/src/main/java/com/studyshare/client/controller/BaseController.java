package com.studyshare.client.controller;

import com.studyshare.client.util.ErrorHandler;
import java.util.concurrent.CompletableFuture;

public abstract class BaseController {
    protected <T> CompletableFuture<T> handleAsync(CompletableFuture<T> future) {
        return future.exceptionally(throwable -> {
            ErrorHandler.handle(throwable);
            return null;
        });
    }
}
