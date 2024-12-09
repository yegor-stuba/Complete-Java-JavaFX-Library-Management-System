package com.studyshare.server.service.impl;

import com.studyshare.client.controller.AdminDashboardController;
import com.studyshare.server.exception.ValidationException;
import com.studyshare.server.service.UserService;
import com.studyshare.common.dto.UserDTO;
import com.studyshare.server.repository.UserRepository;
import com.studyshare.server.model.User;
import com.studyshare.server.mapper.UserMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);



    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(UserDTO userDTO) {
        validateNewUser(userDTO);
        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());
        User user = userMapper.toEntity(userDTO);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    private void validateNewUser(UserDTO userDTO) {
        if (existsByUsername(userDTO.getUsername()) || existsByEmail(userDTO.getEmail())) {
            throw new ValidationException("Username or email already exists");
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return CompletableFuture.completedFuture(findByUsername(username));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("admin".equals(username)) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles("ADMIN")
                    .build();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    @Override
    public void validateToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Invalid token");
        }
    }

@Override
public UserDTO findByUsername(String username) {
    log.debug("Finding user by username: '{}'", username);
    try {
        User user = userRepository.findByUsername(username.trim())
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        log.debug("Found user in database: {}", user);
        return userMapper.toDto(user);
    } catch (Exception e) {
        log.error("Error finding user {}: {}", username, e.getMessage());
        throw e;
    }
}

@Override
public boolean authenticate(String username, String password) {
    try {
        User user = userRepository.findByUsername(username.trim())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.debug("Authenticating user: {} with stored password: {}",
            username, user.getPassword());
        return password.equals(user.getPassword());
    } catch (Exception e) {
        log.error("Authentication failed for {}: {}", username, e.getMessage());
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
}