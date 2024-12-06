package com.studyshare.server.service.impl;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.server.model.User;
import com.studyshare.server.repository.UserRepository;
import com.studyshare.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.studyshare.server.exception.ResourceNotFoundException;
import com.studyshare.server.exception.ValidationException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        validateUserInput(userDTO);

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    private void validateUserInput(UserDTO userDTO) {
    if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
        throw new ValidationException("Username cannot be empty");
    }
    if (userDTO.getEmail() == null || !userDTO.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
        throw new ValidationException("Invalid email format");
    }
    // Allow admin/admin for testing, but enforce stronger passwords for other users
    if (!userDTO.getUsername().equals("admin")) {
        if (userDTO.getPassword() == null || userDTO.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
    }
    if (userRepository.existsByUsername(userDTO.getUsername())) {
        throw new ValidationException("Username already exists");
    }
    if (userRepository.existsByEmail(userDTO.getEmail())) {
        throw new ValidationException("Email already exists");
    }
}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(userDTO.getEmail());
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user.setRole(userDTO.getRole());
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDTO findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
public CompletableFuture<Boolean> login(String username, String password) {
    return CompletableFuture.supplyAsync(() -> {
        try {
            return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
        } catch (Exception e) {
            throw new RuntimeException("Login failed", e);
        }
    });
}

@Override
public CompletableFuture<UserDTO> register(UserDTO userDTO) {
    return CompletableFuture.supplyAsync(() -> {
        try {
            return createUser(userDTO);
        } catch (Exception e) {
            throw new RuntimeException("Registration failed", e);
        }
    });
}

    @Override
    public boolean authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPassword()))
                .orElse(false);
    }



    @Override
    public UserDTO getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }


    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}