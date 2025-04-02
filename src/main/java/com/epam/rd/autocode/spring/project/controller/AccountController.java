package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.User;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountController(ClientRepository clientRepository,
                             EmployeeRepository employeeRepository,
                             PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("AccountController initialized");
    }

    @GetMapping("/account")
    public String accountPage(Model model, Authentication authentication) {
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        logger.info("Account page requested by {} (Role: {})", email, role);

        try {
            if (role.equals("ROLE_CLIENT")) {
                Client client = clientRepository.findByEmail(email)
                        .orElseThrow(() -> {
                            logger.warn("Client not found: {}", email);
                            return new RuntimeException("Client not found");
                        });
                model.addAttribute("user", client);
                logger.debug("Client data loaded for {}", email);
            } else {
                Employee employee = employeeRepository.findByEmail(email)
                        .orElseThrow(() -> {
                            logger.warn("Employee not found: {}", email);
                            return new RuntimeException("Employee not found");
                        });
                model.addAttribute("user", employee);
                logger.debug("Employee data loaded for {}", email);
            }
            model.addAttribute("isClient", role.equals("ROLE_CLIENT"));
            return "account";
        } catch (Exception e) {
            logger.error("Error loading account page for {}: {}", email, e.getMessage());
            throw e;
        }
    }

    @PostMapping("/account/update")
    public String updateProfile(@RequestParam String name,
                                @RequestParam String email,
                                @RequestParam(required = false) String newPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        String currentEmail = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        logger.info("Profile update initiated by {} (Role: {})", currentEmail, role);
        logger.debug("Update params - Name: {}, Email: {}, Password Changed: {}",
                name, email, newPassword != null);

        try {
            if (role.equals("ROLE_CLIENT")) {
                Client client = clientRepository.findByEmail(currentEmail)
                        .orElseThrow(() -> {
                            logger.warn("Client not found during update: {}", currentEmail);
                            return new RuntimeException("Client not found");
                        });
                updateUser(client, name, email, newPassword);
                clientRepository.save(client);
                logger.info("Client profile updated successfully: {}", currentEmail);
            } else {
                Employee employee = employeeRepository.findByEmail(currentEmail)
                        .orElseThrow(() -> {
                            logger.warn("Employee not found during update: {}", currentEmail);
                            return new RuntimeException("Employee not found");
                        });
                updateUser(employee, name, email, newPassword);
                employeeRepository.save(employee);
                logger.info("Employee profile updated successfully: {}", currentEmail);
            }
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (AlreadyExistException e) {
            logger.warn("Duplicate email attempt by {}: {}", currentEmail, email);
            redirectAttributes.addFlashAttribute("error", "Email already in use");
        } catch (Exception e) {
            logger.error("Profile update failed for {}: {}", currentEmail, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error updating profile");
        }
        return "redirect:/account";
    }

    private void updateUser(User user, String name, String email, String newPassword) {
        if (!user.getEmail().equals(email)) {
            boolean emailExists = false;
            if (user instanceof Client) {
                emailExists = clientRepository.findByEmail(email).isPresent();
                logger.debug("Checking email availability for client: {}", email);
            } else if (user instanceof Employee) {
                emailExists = employeeRepository.findByEmail(email).isPresent();
                logger.debug("Checking email availability for employee: {}", email);
            }

            if (emailExists) {
                throw new AlreadyExistException("Email already in use");
            }
            user.setEmail(email);
            logger.info("Email changed from {} to {}", user.getEmail(), email);
        }
        user.setName(name);
        if (newPassword != null && !newPassword.isEmpty()) {
            logger.debug("Password change initiated for {}", user.getEmail());
            user.setPassword(passwordEncoder.encode(newPassword));
        }
    }
}