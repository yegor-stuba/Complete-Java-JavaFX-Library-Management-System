package com.studyshare.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyshare.client.config.ClientConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class RestClient {
    private final String baseUrl = ClientConfig.BASE_URL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RestClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofMillis(ClientConfig.CONNECTION_TIMEOUT))
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> deserialize(body, responseType));
    }

    public <T> CompletableFuture<T> post(String path, Object body, Class<T> responseType) {
        try {
            String jsonBody = body != null ? objectMapper.writeValueAsString(body) : "";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(responseBody -> deserialize(responseBody, responseType));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private <T> T deserialize(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response", e);
        }
    }
}