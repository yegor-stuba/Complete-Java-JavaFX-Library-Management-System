package com.studyshare.common.security.dto;

import com.studyshare.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private String username;
    private UserRole role;
    private Long userId;
    private String message;
    private String error;
}