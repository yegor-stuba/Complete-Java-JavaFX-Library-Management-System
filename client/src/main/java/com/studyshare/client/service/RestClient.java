package com.studyshare.client.service;

import com.fasterxml.jackson.databind.JavaType;
import com.studyshare.client.service.exception.*;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studyshare.client.config.ClientConfig;
import org.springframework.core.ParameterizedTypeReference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class RestClient {
    private static final Logger log = LoggerFactory.getLogger(RestClient.class);
    private final String baseUrl = ClientConfig.BASE_URL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String authToken;

    public RestClient() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

public void setAuthToken(String token) {
    this.authToken = token;
    log.debug("Auth token set: {}", token); // Add logging
}

    private HttpRequest.Builder createRequestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Content-Type", "application/json");

        if (authToken != null && !authToken.isEmpty()) {
            log.debug("Adding auth token to request for path {}", path);
            builder.header("Authorization", "Bearer " + authToken);
        }
        return builder;
    }

    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        log.debug("GET Request to {}", path);
        return httpClient.sendAsync(createRequestBuilder(path).GET().build(),
            HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> handleResponse(response, responseType, "GET " + path));
    }

    public <T> CompletableFuture<T> getList(String path, ParameterizedTypeReference<T> responseType) {
        log.debug("GET List Request to {}", path);
        return httpClient.sendAsync(createRequestBuilder(path).GET().build(),
            HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> handleListResponse(response, responseType, "GET List " + path));
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


private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType, String operation) {
    log.debug("Response from {}: {} - {}", operation, response.statusCode(), response.body());

    return switch (response.statusCode()) {
        case 200, 201, 204 -> deserialize(response.body(), responseType);
        case 401 -> {
            log.error("Authentication failed for {}: {}", operation, response.body());
            this.authToken = null;
            throw new AuthenticationException("Session expired - please log in again");
        }
        case 403 -> throw new AuthorizationException("Access denied");
        default -> throw new RestClientException(response.statusCode(),
            "Request failed: " + response.body());
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
}