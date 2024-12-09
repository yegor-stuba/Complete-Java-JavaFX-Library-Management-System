package com.studyshare.client.service.impl;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.exception.AuthenticationException;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.security.dto.AuthenticationRequest;
import com.studyshare.common.security.dto.AuthenticationResponse;
import java.util.concurrent.CompletableFuture;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final RestClient restClient;
    private String token;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    public AuthenticationServiceImpl(RestClient restClient) {
        this.restClient = restClient;
        this.token = null;
    }

@Override
public CompletableFuture<AuthenticationResponse> login(String username, String password) {
    AuthenticationRequest request = new AuthenticationRequest();
    request.setUsername(username);
    request.setPassword(password);
    log.debug("Attempting login for user: {}", username);

    return restClient.post("/api/auth/login", request, AuthenticationResponse.class)
        .thenApply(response -> {
            if (response == null || response.getToken() == null) {
                log.error("Received invalid authentication response");
                throw new AuthenticationException("Invalid server response");
            }
            this.token = response.getToken();
            restClient.setAuthToken(response.getToken());
            log.debug("Login successful for user: {}", username);
            return response;
        });
}

@Override
    public CompletableFuture<AuthenticationResponse> register(UserDTO userDTO) {
        return restClient.post("/api/auth/register", userDTO, AuthenticationResponse.class)
            .thenApply(response -> {
                this.token = response.getToken();
                return response;
            });
    }

    @Override
    public CompletableFuture<Boolean> validateToken(String token) {
        return restClient.get("/api/auth/validate", Boolean.class);
    }

    @Override
    public CompletableFuture<Void> logout() {
        return restClient.post("/api/auth/logout", null, Void.class)
            .thenRun(() -> this.token = null);
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public boolean isAuthenticated() {
        return token != null;
    }
}

