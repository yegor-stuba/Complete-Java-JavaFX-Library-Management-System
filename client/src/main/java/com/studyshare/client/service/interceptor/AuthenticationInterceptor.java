package com.studyshare.client.service.interceptor;

import com.studyshare.client.service.AuthenticationService;
import java.net.http.HttpRequest;

public class AuthenticationInterceptor {
    private final AuthenticationService authenticationService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthenticationInterceptor.class);

    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

public HttpRequest intercept(HttpRequest request) {
    try {
        String token = authenticationService.getToken();
        if (token != null && !token.isEmpty()) {
            log.debug("Adding token to request: {}", token.substring(0, 10) + "...");
            return HttpRequest.newBuilder()
                .uri(request.uri())
                .headers(request.headers().map().entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream().map(value -> entry.getKey() + ": " + value))
                    .toArray(String[]::new))
                .header("Authorization", "Bearer " + token)
                .method(request.method(), request.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()))
                .build();
        }
    } catch (Exception e) {
        log.error("Failed to add authentication token: {}", e.getMessage());
    }
    return request;
}}