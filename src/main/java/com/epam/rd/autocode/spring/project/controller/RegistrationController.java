package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@Controller
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(ClientRepository clientRepository,
                                  PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        logger.info("RegistrationController initialized successfully");
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        logger.debug("Displaying registration form");
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute ClientDTO clientDTO) {
        logger.info("Registration attempt for email: {}", clientDTO.getEmail());

        // Check if email exists
        if (clientRepository.findByEmail(clientDTO.getEmail()).isPresent()) {
            logger.warn("Registration failed - email already exists: {}", clientDTO.getEmail());
            return "redirect:/register?error";
        }

        try {
            // Create and save new client
            Client client = new Client();
            client.setName(clientDTO.getName());
            client.setEmail(clientDTO.getEmail());
            client.setPassword(passwordEncoder.encode(clientDTO.getPassword()));
            client.setBalance(BigDecimal.ZERO);

            Client savedClient = clientRepository.save(client);
            logger.info("New client registered successfully. ID: {}, Name: {}, Email: {}",
                    savedClient.getId(), savedClient.getName(), savedClient.getEmail());

            return "redirect:/login?registered";
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", clientDTO.getEmail(), e);
            return "redirect:/register?error";
        }
    }
}