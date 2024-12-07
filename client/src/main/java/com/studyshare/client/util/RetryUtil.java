package com.studyshare.client.util;

import java.util.function.Supplier;

public class RetryUtil {
    public static <T> T withRetry(Supplier<T> operation, int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return operation.get();
            } catch (Exception e) {
                if (attempt == maxAttempts) throw e;
                try {
                    Thread.sleep(1000 * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        throw new RuntimeException("Max retry attempts reached");
    }
}
