package com.studyshare.client.service.impl;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.exception.AuthenticationException;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.security.dto.AuthenticationRequest;
import com.studyshare.common.security.dto.AuthenticationResponse;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;

import java.util.concurrent.CompletableFuture;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final RestClient restClient;
    private String token;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthenticationServiceImpl.class);




    public AuthenticationServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
public CompletableFuture<AuthenticationResponse> register(UserDTO userDTO) {
    return restClient.post("/api/auth/register", userDTO, AuthenticationResponse.class)
        .thenApply(response -> {
            if (response != null && response.getError() != null) {
                throw new AuthenticationException(response.getError());
            }
            this.token = response.getToken();
            return response;
        })
        .exceptionally(throwable -> {
            log.error("Registration failed: {}", throwable.getMessage());
            throw new AuthenticationException("Registration failed: " + throwable.getMessage());
        });
}


@Override
public CompletableFuture<UserDTO> login(String username, String password) {
    AuthenticationRequest request = new AuthenticationRequest(username.trim(), password);
    return restClient.post("/api/auth/login", request, UserDTO.class);
}

    @Override
    public CompletableFuture<Void> logout() {
        return restClient.post("/api/auth/logout", null, Void.class);
    }


@Override
public String getToken() {
    if (token == null) {
        throw new AuthenticationException("No valid token found - please log in");
    }
    return token;
}

private void validateSession() {
    if (token == null || !isTokenValid(token)) {
        throw new AuthenticationException("Invalid session - please log in again");
    }
}

private boolean isTokenValid(String token) {
    try {
        return restClient.get("/api/auth/validate", Boolean.class).join();
    } catch (Exception e) {
        log.warn("Token validation failed: {}", e.getMessage());
        return false;
    }
}



}

