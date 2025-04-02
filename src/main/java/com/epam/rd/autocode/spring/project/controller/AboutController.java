package com.epam.rd.autocode.spring.project.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/about")
public class AboutController {
    private static final Logger logger = LoggerFactory.getLogger(AboutController.class);

    @GetMapping
    public String about(HttpServletRequest request) {
        // Basic access logging
        logger.info("About page accessed | IP: {} | User-Agent: {}",
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

        // Debug-level details (only enabled during troubleshooting)
        logger.debug("About page rendering with default view");

        return "about";
    }
}