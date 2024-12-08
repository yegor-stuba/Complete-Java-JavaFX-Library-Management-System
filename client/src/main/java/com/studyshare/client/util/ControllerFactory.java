package com.studyshare.client.util;

import com.studyshare.client.service.AuthenticationService;
import com.studyshare.client.service.BookService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.service.TransactionService;
import com.studyshare.client.controller.*;

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
        if (controllerClass == LoginController.class) {
            return new LoginController(userService, sceneManager, authService);
        } else if (controllerClass == RegisterController.class) {
            return new RegisterController(userService, sceneManager);
        } else if (controllerClass == AdminDashboardController.class) {
            return new AdminDashboardController(userService, bookService, transactionService, sceneManager);
        } else if (controllerClass == BookManagementController.class) {
            return new BookManagementController(bookService);
        } else if (controllerClass == UserProfileController.class) {
            return new UserProfileController(userService, transactionService, sceneManager);
        }
        throw new IllegalArgumentException("Unknown controller class: " + controllerClass.getName());
    }
}