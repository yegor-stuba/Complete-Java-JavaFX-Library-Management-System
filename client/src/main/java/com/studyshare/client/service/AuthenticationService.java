package com.studyshare.client.service;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.security.dto.AuthenticationResponse;
import java.util.concurrent.CompletableFuture;

public interface AuthenticationService {

    CompletableFuture<AuthenticationResponse> register(UserDTO userDTO);
    String getToken();
    CompletableFuture<UserDTO> login(String username, String password);
    CompletableFuture<Void> logout();
}