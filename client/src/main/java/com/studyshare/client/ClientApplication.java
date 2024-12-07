package com.studyshare.client;

import com.studyshare.client.service.RestClient;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.BookService;
import com.studyshare.client.service.impl.TransactionServiceImpl;
import com.studyshare.client.service.impl.UserServiceImpl;
import com.studyshare.client.service.impl.BookServiceImpl;
import com.studyshare.client.util.SceneManager;
import com.studyshare.client.util.ControllerFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClientApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        RestClient restClient = new RestClient();
        UserService userService = new UserServiceImpl(restClient);
        BookService bookService = new BookServiceImpl(restClient);
        TransactionService transactionService = new TransactionServiceImpl(restClient);

        SceneManager sceneManager = new SceneManager(primaryStage);
        ControllerFactory controllerFactory = new ControllerFactory(
            sceneManager,
            userService,
            bookService,
            transactionService
        );
        sceneManager.setControllerFactory(controllerFactory);

        primaryStage.setTitle("StudyShare Library");
        sceneManager.switchToLogin();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}