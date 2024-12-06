package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.UserService;
import com.studyshare.common.dto.UserDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserServiceImpl implements UserService {
    private final RestClient restClient;
    private UserDTO currentUser;

    public UserServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public CompletableFuture<UserDTO> login(String username, String password) {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return restClient.post("/api/auth/login", loginRequest, UserDTO.class)
            .thenApply(user -> {
                this.currentUser = user;
                return user;
            });
    }

    @Override
    public CompletableFuture<UserDTO> register(UserDTO userDTO) {
        return restClient.post("/api/auth/register", userDTO, UserDTO.class);
    }

    @Override
    public CompletableFuture<UserDTO> getCurrentUser() {
        return restClient.get("/api/auth/current", UserDTO.class);
    }

    @Override
    public CompletableFuture<List<UserDTO>> getAllUsers() {
        return restClient.get("/api/users", List.class);
    }

    @Override
    public CompletableFuture<UserDTO> createUser(UserDTO userDTO) {
        return restClient.post("/api/users", userDTO, UserDTO.class);
    }

    @Override
    public CompletableFuture<UserDTO> updateUser(Long id, UserDTO userDTO) {
        return restClient.put("/api/users/" + id, userDTO, UserDTO.class);
    }

    @Override
    public CompletableFuture<Void> deleteUser(Long id) {
        return restClient.delete("/api/users/" + id, Void.class);
    }

    @Override
    public CompletableFuture<List<UserDTO>> searchUsers(String query) {
        return restClient.get("/api/users/search?query=" + query, List.class);
    }

    @Override
    public CompletableFuture<Long> getUserCount() {
        return restClient.get("/api/users/count", Long.class);
    }

    @Override
    public CompletableFuture<Void> logout() {
        return restClient.post("/api/auth/logout", null, Void.class)
            .thenRun(() -> this.currentUser = null);
    }
}