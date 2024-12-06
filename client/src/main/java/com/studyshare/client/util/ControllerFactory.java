package com.studyshare.client.util;

import com.studyshare.client.service.BookService;
import com.studyshare.client.service.UserService;
import com.studyshare.client.controller.*;

public class ControllerFactory {
    private final SceneManager sceneManager;
    private final UserService userService;
    private final BookService bookService;

    public ControllerFactory(SceneManager sceneManager, UserService userService, BookService bookService) {
        this.sceneManager = sceneManager;
        this.userService = userService;
        this.bookService = bookService;
    }

    public Object createController(Class<?> controllerClass) {
        if (controllerClass == LoginController.class) {
            return new LoginController(userService, sceneManager);
        } else if (controllerClass == BookManagementController.class) {
            return new BookManagementController(bookService);
        } else if (controllerClass == UserProfileController.class) {
            return new UserProfileController(userService, sceneManager);
        }
        throw new IllegalArgumentException("Unknown controller class: " + controllerClass.getName());
    }
}