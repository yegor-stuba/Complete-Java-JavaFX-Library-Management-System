package com.studyshare.client.service.interceptor;

import com.studyshare.client.service.AuthenticationService;
import lombok.RequiredArgsConstructor;

import java.net.http.HttpRequest;
import java.util.function.BiFunction;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements BiFunction<HttpRequest.Builder, String, HttpRequest.Builder> {
    private final AuthenticationService authenticationService;

    @Override
    public HttpRequest.Builder apply(HttpRequest.Builder builder, String uri) {
        if (authenticationService.isAuthenticated()) {
            return builder.header("Authorization", "Bearer " + authenticationService.getToken());
        }
        return builder;
    }
}
