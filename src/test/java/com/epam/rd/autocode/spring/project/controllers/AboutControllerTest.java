package com.epam.rd.autocode.spring.project.controllers;

import com.epam.rd.autocode.spring.project.controller.AboutController;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class AboutControllerTest {

    @Test
    void about_ReturnsAboutView() {
        // given
        AboutController controller = new AboutController();
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        String viewName = controller.about(request);

        // then
        assertEquals("about", viewName);
    }
}
