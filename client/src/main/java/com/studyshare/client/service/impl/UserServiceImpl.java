package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.UserService;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.common.security.dto.AuthenticationResponse;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserServiceImpl implements UserService {
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
    public CompletableFuture<List<UserDTO>> getAllUsers() {
        return restClient.getList("/api/users", new ParameterizedTypeReference<List<UserDTO>>() {});
    }

    @Override
    public CompletableFuture<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return restClient.put("/api/users/" + id, userDTO, UserDTO.class);
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
    public boolean isAdmin() {
        return authResponse != null && UserRole.ADMIN.equals(authResponse.getRole());
    }

    @Override
    public CompletableFuture<Long> getUserCount() {
        return restClient.get("/api/users/count", Long.class);
    }

    @Override
    public CompletableFuture<UserDTO> createUser(UserDTO userDTO) {
        return restClient.post("/api/users", userDTO, UserDTO.class);
    }
}