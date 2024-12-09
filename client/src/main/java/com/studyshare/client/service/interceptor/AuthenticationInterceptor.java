package com.studyshare.client.service.interceptor;

import com.studyshare.client.service.AuthenticationService;
import java.net.http.HttpRequest;

public class AuthenticationInterceptor {
    private final AuthenticationService authenticationService;

    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

 public HttpRequest intercept(HttpRequest request) {
    String token = authenticationService.getToken();
    if (token != null) {
        return HttpRequest.newBuilder()
            .uri(request.uri())
            .header("Authorization", "Bearer " + token)
            .method(request.method(), request.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()))
            .build();
    }
    return request;
}
}