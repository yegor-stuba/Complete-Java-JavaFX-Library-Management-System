package com.studyshare.client.service;

import com.studyshare.common.dto.UserDTO;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService {
    CompletableFuture<UserDTO> login(String username, String password);
    CompletableFuture<UserDTO> register(UserDTO userDTO);
    CompletableFuture<UserDTO> getCurrentUser();
    CompletableFuture<List<UserDTO>> getAllUsers();
    CompletableFuture<UserDTO> createUser(UserDTO userDTO);
    CompletableFuture<UserDTO> updateUser(Long id, UserDTO userDTO);
    CompletableFuture<Void> deleteUser(Long id);
    CompletableFuture<List<UserDTO>> searchUsers(String query);
    CompletableFuture<Long> getUserCount();
    CompletableFuture<Void> logout();
}