package com.studyshare.client.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studyshare.client.config.ClientConfig;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ParameterizedTypeReference;
import com.fasterxml.jackson.databind.JavaType;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static org.hibernate.engine.spi.CollectionKey.deserialize;

public class RestClient {
    private final String baseUrl = ClientConfig.BASE_URL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;

    public RestClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> deserialize(body, responseType));
    }

    public <T> CompletableFuture<T> getList(String path, ParameterizedTypeReference<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> deserializeParameterized(body, responseType));
    }

    private <T> T deserialize(String response, Class<T> responseType) {
    try {
        System.out.println("Deserializing response: " + response); // Debug log
        return objectMapper.readValue(response, responseType);
    } catch (Exception e) {
        System.err.println("Deserialization failed for response: " + response);
        e.printStackTrace();
        throw new RuntimeException("Failed to deserialize response: " + e.getMessage(), e);
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



    public <T> CompletableFuture<T> post(String path, Object requestBody, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(serializeRequest(requestBody)))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(response -> deserialize(response, responseType));
    }

    public <T> CompletableFuture<T> put(String path, Object requestBody, Class<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(serializeRequest(requestBody)))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(response -> deserialize(response, responseType));
    }

    public CompletableFuture<Void> delete(String path) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .DELETE()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> null);
    }


    private String serializeRequest(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize request", e);
        }
    }


}