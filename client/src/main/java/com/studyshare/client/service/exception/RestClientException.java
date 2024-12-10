package com.studyshare.client.service.exception;

import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpResponse;

import static org.springframework.util.SerializationUtils.deserialize;


@Slf4j
public class RestClientException extends RuntimeException {
    private final int statusCode;
    private final String errorBody;

    public RestClientException(int statusCode, String errorBody) {
        super(String.format("HTTP %d: %s", statusCode, errorBody));
        this.statusCode = statusCode;
        this.errorBody = errorBody;
    }
    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseType, String operation) {
    log.debug("Response from {}: {} - {}", operation, response.statusCode(), response.body());

    return switch (response.statusCode()) {
        case 200, 201, 204 -> responseType.cast(deserialize(response.body().getBytes()));
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

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorBody() {
        return errorBody;
    }
}
