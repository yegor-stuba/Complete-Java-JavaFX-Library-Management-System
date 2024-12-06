package com.studyshare.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
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

    public RestClient() {
        this.baseUrl = "http://localhost:8080";
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
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

        return sendRequest(request, responseType);
    }

    public <T> CompletableFuture<T> post(String path, Object body, Class<T> responseType) {
        return sendWithBody(path, body, responseType, "POST");
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
            return CompletableFuture.failedFuture(e);
        }
    }

    private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply(body -> deserialize(body, responseType));
    }

    private <T> CompletableFuture<T> sendRequest(HttpRequest request, ParameterizedTypeReference<T> responseType) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply(body -> deserializeCollection(body, responseType));
    }

    private <T> T deserialize(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response", e);
        }
    }

    private <T> T deserializeCollection(String json, ParameterizedTypeReference<T> typeReference) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructType(typeReference.getType());
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize collection response", e);
        }
    }
}