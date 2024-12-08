package com.studyshare.client.service.impl;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.RestClient;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.security.dto.AuthenticationRequest;
import com.studyshare.common.security.dto.AuthenticationResponse;
import java.util.concurrent.CompletableFuture;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final RestClient restClient;
    private String token;

    public AuthenticationServiceImpl(RestClient restClient) {
        this.restClient = restClient;
        this.token = null;
    }

   // In AuthenticationServiceImpl.java
public CompletableFuture<AuthenticationResponse> login(String username, String password) {
    AuthenticationRequest request = new AuthenticationRequest();
    request.setUsername(username);
    request.setPassword(password);

    return restClient.post("/api/auth/login", request, AuthenticationResponse.class)
        .thenApply(response -> {
            this.token = response.getToken();
            return response;
        })
        .exceptionally(throwable -> {
            throw new SecurityException("Authentication failed: " + throwable.getMessage());
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

