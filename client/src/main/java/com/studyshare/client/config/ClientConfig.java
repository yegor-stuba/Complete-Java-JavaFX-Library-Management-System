package com.studyshare.client.config;

import static org.apache.logging.log4j.util.ProviderActivator.API_VERSION;

public class ClientConfig {
    public static final String BASE_URL = "http://localhost:9080";
    public static final int REQUEST_TIMEOUT = 10000;
    public static final String API_VERSION = "/api/v1";

    public static final String FXML_PATH = "/fxml/";
    public static final String CSS_PATH = "/css/";
    public static final String USER_PROFILE_FXML = FXML_PATH + "user-profile.fxml";

    public static final String BOOKS_ENDPOINT = API_VERSION + "/books";
    public static final String USERS_ENDPOINT = API_VERSION + "/users";
    public static final String AUTH_ENDPOINT = API_VERSION + "/auth";


    // Application settings
    public static final String APP_TITLE = "Library Management System";
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;

    // Timeout settings
    public static final int CONNECTION_TIMEOUT = 5000; // milliseconds
    public static final int READ_TIMEOUT = 5000; // milliseconds
}