package com.studyshare.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyshare.client.config.ClientConfig;
import com.studyshare.client.service.exception.RestClientException;
import com.studyshare.client.service.interceptor.AuthenticationInterceptor;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class RestClient {
    private final String baseUrl = ClientConfig.BASE_URL;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final AuthenticationInterceptor authInterceptor;

    public RestClient(AuthenticationService authenticationService) {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(ClientConfig.CONNECTION_TIMEOUT))
            .build();
        this.objectMapper = new ObjectMapper();
        this.authInterceptor = new AuthenticationInterceptor(authenticationService);
    }

    public <T> CompletableFuture<T> get(String path, Class<T> responseType) {
        HttpRequest request = createRequestBuilder(path)
            .GET()
            .build();

        return sendRequest(request, responseType);
    }

    public <T> CompletableFuture<T> post(String path, Object body, Class<T> responseType) {
        String jsonBody = serializeBody(body);
        HttpRequest request = createRequestBuilder(path)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        return sendRequest(request, responseType);
    }

    public <T> CompletableFuture<T> put(String path, Object body, Class<T> responseType) {
        String jsonBody = serializeBody(body);
        HttpRequest request = createRequestBuilder(path)
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        return sendRequest(request, responseType);
    }

    public <T> CompletableFuture<T> delete(String path, Class<T> responseType) {
        HttpRequest request = createRequestBuilder(path)
            .DELETE()
            .build();

        return sendRequest(request, responseType);
    }

    private HttpRequest.Builder createRequestBuilder(String path) {
        return authInterceptor.apply(
            HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofMillis(ClientConfig.CONNECTION_TIMEOUT)),
            path
        );
    }

        private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() >= 400) {
                    throw new RestClientException(response.statusCode(), response.body());
                }
                return response.body();
            })
            .thenApply(body -> deserialize(body, responseType));
    }

    private String serializeBody(Object body) {
        try {
            return body != null ? objectMapper.writeValueAsString(body) : "";
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize request body", e);
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