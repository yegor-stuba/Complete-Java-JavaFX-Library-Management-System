package com.studyshare.server.service.impl;

import com.studyshare.common.dto.UserDTO;
import com.studyshare.common.enums.UserRole;
import com.studyshare.server.model.User;
import com.studyshare.server.repository.UserRepository;
import com.studyshare.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        validateNewUser(userDTO);
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : UserRole.USER);
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }

        return convertToDTO(userRepository.save(user));
    }

    @Override
    @Transactional
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
    public List<UserDTO> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Long getUserCount() {
        return userRepository.count();
    }

    @Override
    public boolean validateCredentials(String username, String password) {
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

    private void validateNewUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
    }
}