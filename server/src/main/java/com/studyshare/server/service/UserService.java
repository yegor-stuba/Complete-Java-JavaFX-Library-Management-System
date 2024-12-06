package com.studyshare.server.service;

import com.studyshare.common.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    UserDTO createUser(UserDTO userDTO);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO findByUsername(String username);
    List<UserDTO> searchUsers(String query);
    Long getUserCount();
    boolean validateCredentials(String username, String password);
    UserDTO getCurrentUser();
    void logout();
}