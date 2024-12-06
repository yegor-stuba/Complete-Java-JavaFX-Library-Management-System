package com.studyshare.client.service;

import com.studyshare.common.dto.UserDTO;
import java.util.concurrent.CompletableFuture;

public interface AuthenticationService {
    CompletableFuture<String> login(String username, String password);
    CompletableFuture<String> register(UserDTO userDTO);
    CompletableFuture<Boolean> validateToken(String token);
    CompletableFuture<Void> logout();
    String getToken();
    boolean isAuthenticated();
}