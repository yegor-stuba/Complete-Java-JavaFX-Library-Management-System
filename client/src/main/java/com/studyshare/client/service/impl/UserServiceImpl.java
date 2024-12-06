package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.UserService;
import com.studyshare.common.dto.UserDTO;
import java.util.concurrent.CompletableFuture;

public class UserServiceImpl implements UserService {
    private final RestClient restClient;

    public UserServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public CompletableFuture<Boolean> login(String username, String password) {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return restClient.post("/api/auth/login", loginRequest, Boolean.class);
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
    public CompletableFuture<Void> logout() {
        return restClient.post("/api/auth/logout", null, Void.class);
    }
}