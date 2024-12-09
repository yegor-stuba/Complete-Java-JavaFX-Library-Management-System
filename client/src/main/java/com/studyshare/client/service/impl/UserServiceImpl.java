package com.studyshare.client.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.exception.AuthorizationException;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.common.security.dto.AuthenticationResponse;
import jakarta.validation.ValidationException;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.concurrent.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;

public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final RestClient restClient;
    private AuthenticationResponse authResponse;

    public UserServiceImpl(RestClient restClient) {
        this.restClient = restClient;
        this.authResponse = null;
    }

    @Override
    public CompletableFuture<AuthenticationResponse> login(String username, String password) {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        return restClient.post("/api/auth/login", loginRequest, AuthenticationResponse.class)
            .thenApply(response -> {
                this.authResponse = response;
                return response;
            });
    }

    @Override
    public CompletableFuture<AuthenticationResponse> register(UserDTO userDTO) {
        return restClient.post("/api/auth/register", userDTO, AuthenticationResponse.class)
            .thenApply(response -> {
                this.authResponse = response;
                return response;
            });
    }

    @Override
    public CompletableFuture<UserDTO> getCurrentUser() {
        return restClient.get("/api/auth/current", UserDTO.class);
    }

@Override
public boolean isAdmin() {
    try {
        UserDTO currentUser = getCurrentUser().join();
        return currentUser != null && UserRole.ADMIN.equals(currentUser.getRole());
    } catch (Exception e) {
        log.error("Error checking admin status", e);
        return false;
    }
}

@Override
public CompletableFuture<List<UserDTO>> getAllUsers() {
    if (!isAdmin()) {
        CompletableFuture<List<UserDTO>> future = new CompletableFuture<>();
        future.completeExceptionally(new AuthorizationException("Admin access required"));
        return future;
    }

    return restClient.getList("/api/users",
            new ParameterizedTypeReference<>() {
            });
}

    @Override
    public CompletableFuture<UserDTO> createUser(UserDTO userDTO) {
        log.debug("Creating new user: {}", userDTO.getUsername());
        return restClient.post("/api/users", userDTO, UserDTO.class)
                .thenApply(user -> {
                    log.debug("User created: {}", user.getUsername());
                    return user;
                });
    }

    @Override
    public CompletableFuture<UserDTO> updateUser(Long id, UserDTO userDTO) {
        validateUserInput(userDTO);

        return restClient.put("/api/users/" + id, userDTO, UserDTO.class)
            .thenApply(response -> {
                log.debug("User updated successfully: {}", response.getUsername());
                return response;
            })
            .exceptionally(throwable -> {
                log.error("Failed to update user {}: {}", id, throwable.getMessage());
                throw new CompletionException("Failed to update user: " + throwable.getMessage(), throwable);
            });
    }

    @Override
    public CompletableFuture<Void> deleteUser(Long id) {
        return restClient.delete("/api/users/" + id);
    }

    @Override
    public CompletableFuture<List<UserDTO>> searchUsers(String query) {
        return restClient.getList("/api/users/search?query=" + query,
            new ParameterizedTypeReference<List<UserDTO>>() {});
    }

    @Override
    public CompletableFuture<Void> logout() {
        return restClient.post("/api/auth/logout", null, Void.class)
            .thenRun(() -> this.authResponse = null);
    }


    @Override
    public CompletableFuture<Long> getUserCount() {
        return restClient.get("/api/users/count", Long.class);
    }


    private void validateUserInput(UserDTO userDTO) {
        if (userDTO.getUsername().length() < 3) {
            throw new ValidationException("Username must be at least 3 characters");
        }
        if (!userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
        if (userDTO.getPassword() != null && userDTO.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
    }
}