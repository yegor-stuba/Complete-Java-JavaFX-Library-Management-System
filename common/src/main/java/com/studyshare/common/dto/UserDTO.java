package com.studyshare.common.dto;

import com.studyshare.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private UserRole role;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private int activeBorrowCount;
}