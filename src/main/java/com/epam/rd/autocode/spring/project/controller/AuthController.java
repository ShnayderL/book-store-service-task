package com.epam.rd.autocode.spring.project.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Повертає Thymeleaf-шаблон login.html
    }
}
