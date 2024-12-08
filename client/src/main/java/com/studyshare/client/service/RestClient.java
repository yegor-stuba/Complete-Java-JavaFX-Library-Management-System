package com.studyshare.client.service;

import com.fasterxml.jackson.databind.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.CompletionException;

import static org.hibernate.engine.spi.CollectionKey.deserialize;

public class RestClient {
    private static final Logger log = LoggerFactory.getLogger(RestClient.class);
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



    public <T> CompletableFuture<T> getList(String path, ParameterizedTypeReference<T> responseType) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(body -> deserializeParameterized(body, responseType));
    }

    private void addAuthHeader(HttpRequest.Builder builder) {
        if (authToken != null) {
            builder.header("Authorization", "Bearer " + authToken);
        }
    }

    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path));
        addAuthHeader(builder);

        return httpClient.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    log.debug("Response from {}: {} - {}", path, response.statusCode(), response.body());
                    if (response.statusCode() == 200) {
                        return deserialize(response.body(), responseType);
                    }
                    throw new RuntimeException("Server returned: " + response.statusCode());
                });
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




    public <T> CompletableFuture<T> post(String path, Object requestBody, Class<T> responseType) {
        try {
            String serializedBody = serializeRequest(requestBody);
            log.debug("POST Request to {}: {}", path, serializedBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(serializedBody))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        log.debug("Response from {}: {} - {}", path, response.statusCode(), response.body());
                        if (response.statusCode() != 200) {
                            throw new RuntimeException("Server returned: " + response.statusCode());
                        }
                        return deserialize(response.body(), responseType);
                    })
                    .exceptionally(throwable -> {
                        log.error("Request failed for {}: {}", path, throwable.getMessage());
                        throw new CompletionException(throwable);
                    });
        } catch (Exception e) {
            log.error("Failed to execute request to {}: {}", path, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
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