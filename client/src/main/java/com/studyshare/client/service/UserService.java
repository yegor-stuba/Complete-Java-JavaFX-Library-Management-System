package com.studyshare.client.service;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.security.dto.AuthenticationResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<AuthenticationResponse> login(String username, String password);
    CompletableFuture<AuthenticationResponse> register(UserDTO userDTO);
    CompletableFuture<UserDTO> getCurrentUser();
    CompletableFuture<List<UserDTO>> getAllUsers();
    CompletableFuture<UserDTO> updateUser(Long id, UserDTO userDTO);
    CompletableFuture<Void> deleteUser(Long id);
    CompletableFuture<List<UserDTO>> searchUsers(String query);
    CompletableFuture<Void> logout();
    boolean isAdmin();
}