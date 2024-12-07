package com.studyshare.client.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;


public class RestClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
    private boolean isConnected = false;

    public RestClient() {
        this.baseUrl = "http://localhost:8080";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .GET()
            .build();

        return sendRequest(request, responseType);
    }

    public <T> CompletableFuture<T> get(String path, ParameterizedTypeReference<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> deserializeCollection(body, responseType));
    }

    public <T> CompletableFuture<T> post(String path, Object body, Class<T> responseType) {
        try {
            String jsonBody = body != null ? objectMapper.writeValueAsString(body) : "";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            return sendRequest(request, responseType);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(
                new RuntimeException("Failed to create request: " + e.getMessage(), e));
        }
    }

    public <T> CompletableFuture<T> put(String path, Object body, Class<T> responseType) {
        return sendWithBody(path, body, responseType, "PUT");
    }

    public CompletableFuture<Void> delete(String path) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + path))
            .DELETE()
            .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
            .thenApply(response -> null);
    }

    private <T> CompletableFuture<T> sendWithBody(String path, Object body, Class<T> responseType, String method) {
        try {
            String jsonBody = body != null ? objectMapper.writeValueAsString(body) : "";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            return sendRequest(request, responseType);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(
                new RuntimeException("Failed to create request: " + e.getMessage(), e));
        }
    }



private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                isConnected = true;
                logger.debug("Received response with status: {}", response.statusCode());
                if (response.statusCode() >= 400) {
                    handleHttpError(response);
                }
                return deserialize(response.body(), responseType);
            })
            .exceptionally(throwable -> {
                isConnected = false;
                logger.error("Request failed: {}", throwable.getMessage());
                throw new RuntimeException("Connection failed: " + throwable.getMessage());
            });
}

    private <T> T deserialize(String json, Class<T> type) {
        try {
            if (json == null || json.isEmpty()) {
                throw new RuntimeException("Empty response from server");
            }
            System.out.println("Received JSON: " + json); // Debug line
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            System.err.println("Deserialization error for JSON: " + json); // Debug line
            e.printStackTrace(); // Debug line
            throw new RuntimeException("Failed to deserialize response: " + json, e);
        }
    }


    private <T> T deserializeCollection(String json, ParameterizedTypeReference<T> typeReference) {
        try {
            if (json == null || json.isEmpty()) {
                throw new RuntimeException("Empty response from server");
            }
            JavaType type = objectMapper.getTypeFactory().constructType(typeReference.getType());
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize collection response: " + json, e);
        }
    }

    private void handleHttpError(HttpResponse<?> response) {
        switch (response.statusCode()) {
            case 400 -> throw new RuntimeException("Invalid input: " + response.body());
            case 401 -> throw new RuntimeException("Authentication required");
            case 403 -> throw new RuntimeException("Access denied");
            case 404 -> throw new RuntimeException("Resource not found");
            case 429 -> throw new RuntimeException("Too many attempts, please wait");
            case 500 -> throw new RuntimeException("Server error, please try again later");
            default -> throw new RuntimeException("Connection error: " + response.statusCode());
        }
    }
    private String getErrorMessage(Throwable throwable) {
        if (throwable.getMessage().contains("Connection refused")) {
            return "Cannot connect to server";
        }
        return throwable.getMessage();
    }
}