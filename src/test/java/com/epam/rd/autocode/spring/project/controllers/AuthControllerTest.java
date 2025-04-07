package com.epam.rd.autocode.spring.project.controllers;

import com.epam.rd.autocode.spring.project.controller.AuthController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthControllerTest {

    @Test
    void showLoginPage_ReturnsLoginView() {
        // given
        AuthController controller = new AuthController();

        // when
        String viewName = controller.showLoginPage();

        // then
        assertEquals("login", viewName);
    }
}
