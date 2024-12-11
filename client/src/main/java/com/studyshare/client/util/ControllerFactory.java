package com.studyshare.client.util;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.BookService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.controller.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ControllerFactory {
    private final SceneManager sceneManager;
    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;
    private final AuthenticationService authService;

    public ControllerFactory(
            SceneManager sceneManager,
            UserService userService,
            BookService bookService,
            TransactionService transactionService,
            AuthenticationService authService) {
        this.sceneManager = sceneManager;
        this.userService = userService;
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.authService = authService;
    }

public Object createController(Class<?> controllerClass) {
    log.debug("Creating controller for class: {}", controllerClass.getName());
    log.debug("Available services - UserService: {}, BookService: {}, TransactionService: {}",
            userService != null, bookService != null, transactionService != null);

    log.debug("Creating controller for class: {}", controllerClass.getName());
    if (controllerClass == UserProfileController.class) {
        log.debug("Creating UserProfileController");
        return new UserProfileController(userService, bookService, transactionService, sceneManager);
    }
    if (controllerClass == LoginController.class) {
        return new LoginController(userService, sceneManager, authService);
    } else if (controllerClass == RegisterController.class) {
        return new RegisterController(userService, sceneManager);
    } else if (controllerClass == AdminDashboardController.class) {
        return new AdminDashboardController(userService, bookService, transactionService, sceneManager);
    } else if (controllerClass == BookManagementController.class) {
        return new BookManagementController(bookService, transactionService);
    }
    throw new IllegalArgumentException("Unknown controller class: " + controllerClass.getName());
}
}