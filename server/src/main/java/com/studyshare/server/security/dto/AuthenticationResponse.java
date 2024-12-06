package com.studyshare.server.security.dto;

import com.studyshare.common.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
    private String token;
    private String username;
    private UserRole role;
    private Long userId;
}
