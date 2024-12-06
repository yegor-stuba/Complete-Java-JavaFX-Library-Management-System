package com.studyshare.client.config;

public class ClientConfig {
    // API endpoints
    public static final String BASE_URL = "http://localhost:8080";
    public static final String AUTH_ENDPOINT = "/api/auth";
    public static final String BOOKS_ENDPOINT = "/api/books";
    public static final String USERS_ENDPOINT = "/api/users";

    // Application settings
    public static final String APP_TITLE = "StudyShare Library";
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    // Timeout settings
    public static final int CONNECTION_TIMEOUT = 5000; // milliseconds
    public static final int READ_TIMEOUT = 5000; // milliseconds
}