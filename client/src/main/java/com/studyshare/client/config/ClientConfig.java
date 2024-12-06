package com.studyshare.client.config;

public class ClientConfig {
    public static final String BASE_URL = "http://localhost:8080";
    public static final int REQUEST_TIMEOUT = 10000;
    public static final String API_VERSION = "/api/v1";

    public static final String BOOKS_ENDPOINT = API_VERSION + "/books";
    public static final String USERS_ENDPOINT = API_VERSION + "/users";
    public static final String AUTH_ENDPOINT = API_VERSION + "/auth";


    // Application settings
    public static final String APP_TITLE = "StudyShare Library";
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    // Timeout settings
    public static final int CONNECTION_TIMEOUT = 5000; // milliseconds
    public static final int READ_TIMEOUT = 5000; // milliseconds
}