package com.studyshare.client.service.interceptor;

import com.studyshare.client.service.AuthenticationService;
import java.net.http.HttpRequest;

public class AuthenticationInterceptor {
    private final AuthenticationService authenticationService;

    public AuthenticationInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public HttpRequest intercept(HttpRequest request) {
        if (authenticationService.getToken() != null) {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(request.uri())
                .header("Authorization", "Bearer " + authenticationService.getToken())
                .method(request.method(), request.bodyPublisher().orElse(HttpRequest.BodyPublishers.noBody()));

            request.headers().map().forEach((name, values) ->
                values.forEach(value -> builder.header(name, value)));

            return builder.build();
        }
        return request;
    }
}