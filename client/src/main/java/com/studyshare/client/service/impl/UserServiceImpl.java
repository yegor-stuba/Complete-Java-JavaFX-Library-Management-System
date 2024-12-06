package com.studyshare.client.service.impl;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.UserService;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.server.security.dto.AuthenticationResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RestClient restClient;
    private AuthenticationResponse currentUser;

    @Override
    public CompletableFuture<AuthenticationResponse> login(String username, String password) {
        UserDTO loginRequest = new UserDTO();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        return restClient.post("/api/auth/login", loginRequest, AuthenticationResponse.class)
            .thenApply(response -> {
                this.currentUser = response;
                return response;
            });
    }

    @Override
    public CompletableFuture<AuthenticationResponse> register(UserDTO userDTO) {
        return restClient.post("/api/auth/register", userDTO, AuthenticationResponse.class);
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
    public CompletableFuture<Void> logout() {
        return restClient.post("/api/auth/logout", null, Void.class)
            .thenRun(() -> this.currentUser = null);
    }

    @Override
    public boolean isAdmin() {
        return currentUser != null && UserRole.ADMIN.equals(currentUser.getRole());
    }
}