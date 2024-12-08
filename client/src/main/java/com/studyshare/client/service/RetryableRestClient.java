package com.studyshare.client.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Function;
import java.util.function.Supplier;

public class RetryableRestClient {
    private static final Logger log = LoggerFactory.getLogger(RetryableRestClient.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private <T> CompletableFuture<T> withRetry(Supplier<CompletableFuture<T>> operation) {
        return withRetry(operation, MAX_RETRIES);
    }

    private <T> CompletableFuture<T> withRetry(Supplier<CompletableFuture<T>> operation, int retriesLeft) {
        return operation.get()
            .exceptionally(throwable -> {
                if (retriesLeft > 0 && isRetryable(throwable)) {
                    log.warn("Operation failed, retrying... ({} attempts left)", retriesLeft);
                    CompletableFuture<Void> delay = new CompletableFuture<>();
                    CompletableFuture.delayedExecutor(RETRY_DELAY_MS, TimeUnit.MILLISECONDS)
                        .execute(() -> delay.complete(null));
                    return delay.thenCompose(v -> withRetry(operation, retriesLeft - 1))
                        .join();
                }
                throw new CompletionException(throwable);
            });
    }

    private boolean isRetryable(Throwable throwable) {
        return throwable instanceof java.net.ConnectException ||
               throwable instanceof java.net.SocketTimeoutException;
    }
}
