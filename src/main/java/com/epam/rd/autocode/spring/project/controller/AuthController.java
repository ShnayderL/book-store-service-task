package com.epam.rd.autocode.spring.project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Повертає Thymeleaf-шаблон login.html
    }

    @PostMapping("/redirect-by-role")
    public ModelAndView loginRedirect() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_CLIENT")) {
                return new ModelAndView("redirect:/clients");
            } else if (authority.getAuthority().equals("ROLE_EMPLOYEE")) {
                return new ModelAndView("redirect:/employee");
            }
        }

        return new ModelAndView("redirect:/login?error");
    }
}
