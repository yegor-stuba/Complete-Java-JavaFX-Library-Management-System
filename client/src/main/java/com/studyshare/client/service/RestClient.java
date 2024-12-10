package com.studyshare.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.studyshare.client.service.exception.*;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.security.dto.AuthenticationResponse;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studyshare.client.config.ClientConfig;
import org.springframework.core.ParameterizedTypeReference;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

public class RestClient {
    private static final Logger log = LoggerFactory.getLogger(RestClient.class);
    private final String baseUrl = ClientConfig.BASE_URL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;
    private final CookieManager cookieManager;
    private String sessionCookie;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;


    public RestClient() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.cookieManager = new CookieManager();
        this.httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
        log.debug("Auth token set: {}", token); // Add logging
    }

    public <T> CompletableFuture<T> executeWithRetry(Supplier<CompletableFuture<T>> operation) {
        return CompletableFuture.supplyAsync(() -> {
            Exception lastException = null;
            for (int i = 0; i < MAX_RETRIES; i++) {
                try {
                    return operation.get().join();
                } catch (Exception e) {
                    lastException = e;
                    try {
                        Thread.sleep(RETRY_DELAY_MS * (i + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new CompletionException(ie);
                    }
                }
            }
            throw new CompletionException(lastException);
        });
    }
    public <T> CompletableFuture<T> getList(String path, ParameterizedTypeReference<T> responseType) {
        log.debug("GET List Request to {}", path);
        return httpClient.sendAsync(createRequestBuilder(path).GET().build(),
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> handleListResponse(response, responseType, "GET List " + path));
    }


    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        log.debug("GET Request to {}", path);
        return httpClient.sendAsync(createRequestBuilder(path).GET().build(),
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> handleResponse(response, responseType, "GET " + path));
    }




    public <T> CompletableFuture<T> put(String path, Object requestBody, Class<T> responseType) {
        String serializedBody = serializeRequest(requestBody);
        log.debug("PUT Request to {}: {}", path, serializedBody);

        HttpRequest request = createRequestBuilder(path)
                .PUT(HttpRequest.BodyPublishers.ofString(serializedBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> handleResponse(response, responseType, "PUT " + path));
    }

    public CompletableFuture<Void> delete(String path) {
        log.debug("DELETE Request to {}", path);
        return httpClient.sendAsync(createRequestBuilder(path).DELETE().build(),
                        HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    handleResponse(response, Void.class, "DELETE " + path);
                    return null;
                });
    }

    public CompletableFuture<Long> getActiveLoansCount() {
        return get("/api/transactions/count/active", Long.class);
    }


private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType, String operation) {

    log.debug("Response from {}: {} - {}", operation, response.statusCode(), response.body());

    return switch (response.statusCode()) {
        case 200, 201, 204 -> deserialize(response.body(), responseType);
        case 302 -> {
            String location = response.headers().firstValue("Location").orElse("");
            if (location.contains("error")) {
                throw new AuthenticationException("Invalid credentials");
            }
            throw new RestClientException(response.statusCode(),
                "Authentication failed - invalid credentials");
        }
        case 401 -> throw new AuthenticationException("Invalid username or password");
        case 403 -> throw new AuthorizationException("Access denied");
        case 404 -> throw new ResourceNotFoundException("Resource not found: " + operation);
        default -> {
            String errorMessage = response.body() != null ? response.body() : "Unknown error";
            log.error("Request failed: {} - Status: {}, Body: {}",
                     operation, response.statusCode(), errorMessage);
            throw new RestClientException(response.statusCode(), errorMessage);
        }
    };
}

    private <T> T handleListResponse(HttpResponse<String> response,
                                     ParameterizedTypeReference<T> responseType,
                                     String operation) {
        log.debug("Response from {}: {} - {}", operation, response.statusCode(), response.body());

        return switch (response.statusCode()) {
            case 200 -> deserializeParameterized(response.body(), responseType);
            case 401 -> throw new AuthenticationException("Session expired - please log in again");
            case 403 -> throw new AuthorizationException("Insufficient permissions to view this data");
            case 404 -> throw new ResourceNotFoundException("Data not found: " + operation);
            default -> throw new RestClientException(response.statusCode(),
                    "Failed to fetch data: " + operation + " - " + response.body());
        };
    }

    private <T> T deserialize(String response, Class<T> responseType) {
        try {
            if (response == null || response.trim().isEmpty()) {
                return null;
            }
            return objectMapper.readValue(response, responseType);
        } catch (Exception e) {
            log.error("Deserialization failed: {}", e.getMessage());
            throw new RuntimeException("Deserialization failed: " + e.getMessage());
        }
    }

    private <T> T deserializeParameterized(String response, ParameterizedTypeReference<T> responseType) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructType(responseType.getType());
            return objectMapper.readValue(response, javaType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response", e);
        }
    }

    private String serializeRequest(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize request", e);
        }
    }

    public boolean isAuthenticated() {
        return authToken != null && !authToken.isEmpty();
    }
private void updateAuthHeader(HttpRequest.Builder builder) {
    if (sessionCookie != null) {
        builder.header("Cookie", sessionCookie);
    }
}
    private HttpRequest.Builder createRequestBuilder(String path) {
        return HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .header("Cookie", getSessionCookie());
    }
    public void setSessionCookie(String cookie) {
        this.sessionCookie = cookie;
    }

    // In RestClient.java
private void storeSessionCookie(HttpResponse<?> response) {
    response.headers().firstValue("Set-Cookie")
            .ifPresent(this::setSessionCookie);
}

    private String getSessionCookie() {
        return sessionCookie != null ? sessionCookie : "";
    }

public <T> CompletableFuture<T> post(String path, Object requestBody, Class<T> responseType) {
    String serializedBody = serializeRequest(requestBody);
    log.debug("POST Request to {}: {}", path, serializedBody);

    HttpRequest request = createRequestBuilder(path)
        .POST(HttpRequest.BodyPublishers.ofString(serializedBody))
        .build();

    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(response -> handleResponse(response, responseType, "POST " + path));
}
}

