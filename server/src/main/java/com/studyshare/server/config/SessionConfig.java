package com.studyshare.server.config;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SessionConfig {
    private static final int SESSION_TIMEOUT = 1800; // 30 minutes

    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                HttpSession session = se.getSession();
                session.setMaxInactiveInterval(SESSION_TIMEOUT);
                log.debug("Session created: {}", session.getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                log.info("Session destroyed: {}", se.getSession().getId());
            }
        };
    }
}