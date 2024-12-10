package com.studyshare.server.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SessionConfig {
    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                log.info("New session created: {}", se.getSession().getId());
                se.getSession().setMaxInactiveInterval(30 * 60); // 30 minutes
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                log.info("Session destroyed: {}", se.getSession().getId());
            }
        };
    }
}