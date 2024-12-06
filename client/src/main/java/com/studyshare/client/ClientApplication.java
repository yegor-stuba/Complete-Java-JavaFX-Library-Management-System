package com.studyshare.client;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.BookService;
import com.studyshare.client.service.impl.UserServiceImpl;
import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.SceneManager;
import com.studyshare.client.util.ControllerFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    private SceneManager sceneManager;
    private RestClient restClient;
    private UserService userService;
    private BookService bookService;
    private ControllerFactory controllerFactory;

    @Override
    public void start(Stage primaryStage) {
        initializeServices();
        initializeControllerFactory(primaryStage);
        sceneManager.switchToLogin();
    }

    private void initializeServices() {
        restClient = new RestClient();
        userService = new UserServiceImpl(restClient);
        bookService = new BookServiceImpl(restClient);
    }

    private void initializeControllerFactory(Stage primaryStage) {
    // Remove the first initialization of sceneManager
    controllerFactory = new ControllerFactory(sceneManager, userService, bookService);
    sceneManager = new SceneManager(primaryStage, controllerFactory);
}

    public static void main(String[] args) {
        launch(args);
    }
}