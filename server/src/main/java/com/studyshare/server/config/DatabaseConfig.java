package com.studyshare.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.studyshare.server.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // SQLite configuration is handled in application.properties
}