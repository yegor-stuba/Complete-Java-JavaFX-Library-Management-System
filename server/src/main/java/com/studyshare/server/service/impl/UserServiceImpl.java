package com.studyshare.server.service.impl;

import com.studyshare.client.controller.AdminDashboardController;
import com.studyshare.client.service.exception.AuthenticationException;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.service.UserService;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.repository.UserRepository;
import com.studyshare.server.model.User;
import com.studyshare.server.mapper.UserMapper;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final int MIN_PASSWORD_LENGTH = 4;


    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);


    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO createUser(UserDTO userDTO) {
        validateNewUser(userDTO);
        User user = userMapper.toEntity(userDTO);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

private void validateNewUser(UserDTO userDTO) {
    log.debug("Validating new user registration for username: {}", userDTO.getUsername());

    if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
        throw new ValidationException("Username cannot be empty");
    }

    if (existsByUsername(userDTO.getUsername())) {
        log.debug("Username already exists: {}", userDTO.getUsername());
        throw new ValidationException("Username already exists");
    }

    if (existsByEmail(userDTO.getEmail())) {
        log.debug("Email already exists: {}", userDTO.getEmail());
        throw new ValidationException("Email already exists");
    }

    validatePassword(userDTO.getPassword());
}

    private void validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userMapper.toDto(userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }


    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateEntity(user, userDTO);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<UserDTO> searchUsers(String query) {
        return userMapper.toDtoList(userRepository.findByUsernameContainingIgnoreCase(query));
    }

@Override
public CompletableFuture<UserDTO> getCurrentUser() {
    return CompletableFuture.supplyAsync(() -> {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return findByUsername(auth.getName());
        }
        throw new AuthenticationException("User not authenticated");
    });
}

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        }


    @Override
    public void validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Invalid token");
        }
    }

@Override
public User getCurrentUserEntity() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    log.debug("Looking up user by username: {}", username);
    return userRepository.findByUsername(username)
        .orElseThrow(() -> {
            log.error("User not found for username: {}", username);
            return new UsernameNotFoundException("User not found: " + username);
        });
}


   @Override
public UserDTO findByUsername(String username) {
    User user = userRepository.findByUsername(username.trim())
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    UserDTO userDTO = userMapper.toDto(user);
    userDTO.setPassword(user.getPassword()); // Add this line
    return userDTO;
}
@Override
public boolean authenticate(String username, String password) {
    log.debug("Authenticating user: {}", username);
    try {
        User user = userRepository.findByUsername(username.trim())
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user.getPassword().equals(password);
    } catch (Exception e) {
        log.error("Authentication error for user {}: {}", username, e.getMessage(), e);
        return false;
    }
}


@Override
public Long getUserCount() {
    return userRepository.count();
}
    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsersForAdmin() {
        log.debug("Admin requesting all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO updateUserAsAdmin(Long id, UserDTO userDTO) {
        log.debug("Admin updating user: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userMapper.updateEntity(user, userDTO);
        return userMapper.toDto(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserAsAdmin(Long id) {
        log.debug("Admin deleting user: {}", id);
        userRepository.deleteById(id);
    }
}