package com.studyshare.server.service;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService extends UserDetailsService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO findByUsername(String username);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<UserDTO> searchUsers(String query);
    CompletableFuture<UserDTO> getCurrentUser();
    void validateToken(String token);
    boolean authenticate(String username, String password);
    void logout();
    Long getUserCount();
    User getCurrentUserEntity();
}