package com.studyshare.server.security;

import com.studyshare.server.service.UserService;
import com.studyshare.server.service.impl.UserServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.studyshare.server.service.UserService;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserService userService; // Add this
    private final JwtTokenProvider tokenProvider;
    private static final String BEARER_PREFIX = "Bearer ";


    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserService userService) { // Update constructor
        this.tokenProvider = tokenProvider;
        this.userService = userService;
    }

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
    FilterChain chain) throws ServletException, IOException {
    try {
        String token = getJwtFromRequest(request);
        String requestPath = request.getRequestURI();
        log.debug("Processing request to: {} with token: {}", requestPath, token);

        if (token != null && token.startsWith("token-")) {
            String username = token.substring(6); // Remove "token-" prefix
            UserDetails userDetails = userService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Set authentication for user: {}", username);
        }

        chain.doFilter(request, response);
    } catch (Exception ex) {
        log.error("Authentication error: {}", ex.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
