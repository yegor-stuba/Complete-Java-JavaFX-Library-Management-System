package com.studyshare.client.service;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.security.dto.AuthenticationResponse;
import java.util.concurrent.CompletableFuture;

public interface AuthenticationService {
    CompletableFuture<AuthenticationResponse> login(String username, String password);
    CompletableFuture<AuthenticationResponse> register(UserDTO userDTO);
    CompletableFuture<Boolean> validateToken(String token);
    CompletableFuture<Void> logout();
    String getToken();
    boolean isAuthenticated();
}