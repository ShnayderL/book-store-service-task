package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.CartItemDTO;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.Client;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/client")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;

    @Autowired
    public ClientController(ClientRepository clientRepository, BookRepository bookRepository) {
        this.clientRepository = clientRepository;
        this.bookRepository = bookRepository;
        logger.info("ClientController initialized with repositories");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public String clientDashboard(Model model, HttpSession session) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Loading dashboard for client with email: {}", email);

        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            model.addAttribute("client", client);
            logger.info("Client found: {}", client.getEmail());

            List<Book> books = bookRepository.findAll();
            model.addAttribute("books", books);
            logger.debug("Loaded {} books for display", books.size());

            Map<Long, Integer> basket = (Map<Long, Integer>) session.getAttribute("basket");
            if (basket == null) {
                basket = new HashMap<>();
                session.setAttribute("basket", basket);
                logger.debug("Created new basket for session");
            }

            List<CartItemDTO> basketItems = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<Long, Integer> entry : basket.entrySet()) {
                Book book = bookRepository.findById(entry.getKey()).orElse(null);
                if (book != null) {
                    int quantity = entry.getValue();
                    BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(quantity));
                    basketItems.add(new CartItemDTO(book, quantity, subtotal));
                    total = total.add(subtotal);
                    logger.trace("Basket item: {} x {} = {}", book.getName(), quantity, subtotal);
                }
            }
            model.addAttribute("basketItems", basketItems);
            model.addAttribute("total", total);
            logger.info("Basket contains {} items with total: {}", basketItems.size(), total);

            return "client";
        } catch (Exception e) {
            logger.error("Error loading client dashboard for {}", email, e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/basket/add/{bookId}")
    public String addToBasket(@PathVariable Long bookId, HttpSession session) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Adding book {} to basket for client {}", bookId, email);

        try {
            Map<Long, Integer> basket = (Map<Long, Integer>) session.getAttribute("basket");
            if (basket == null) {
                basket = new HashMap<>();
                session.setAttribute("basket", basket);
                logger.debug("Created new basket for session");
            }

            int newQuantity = basket.getOrDefault(bookId, 0) + 1;
            basket.put(bookId, newQuantity);
            logger.info("Added book {} to basket. New quantity: {}", bookId, newQuantity);

            return "redirect:/client";
        } catch (Exception e) {
            logger.error("Error adding book {} to basket for client {}", bookId, email, e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/basket/update/{bookId}")
    public String updateQuantity(@PathVariable Long bookId, @RequestParam int quantity, HttpSession session) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Updating quantity for book {} to {} for client {}", bookId, quantity, email);

        if (quantity < 0) {
            logger.warn("Invalid quantity {} requested for book {} by client {}", quantity, bookId, email);
            return "redirect:/client";
        }

        try {
            Map<Long, Integer> basket = (Map<Long, Integer>) session.getAttribute("basket");
            if (basket != null) {
                if (quantity == 0) {
                    basket.remove(bookId);
                    logger.info("Removed book {} from basket for client {}", bookId, email);
                } else {
                    basket.put(bookId, quantity);
                    logger.info("Updated quantity for book {} to {} for client {}", bookId, quantity, email);
                }
            }
            return "redirect:/client";
        } catch (Exception e) {
            logger.error("Error updating quantity for book {} for client {}", bookId, email, e);
            throw e;
        }
    }

    @PostMapping("/basket/remove/{bookId}")
    public ResponseEntity<String> removeFromBasket(@PathVariable Long bookId, HttpSession session) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.debug("Removing book {} from basket for client {}", bookId, email);

        try {
            Map<Long, Integer> basket = (Map<Long, Integer>) session.getAttribute("basket");
            if (basket != null) {
                basket.remove(bookId);
                session.setAttribute("basket", basket);
                logger.info("Removed book {} from basket for client {}", bookId, email);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error removing book {} from basket for client {}", bookId, email, e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, String>> checkout(HttpSession session) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Processing checkout for client {}", email);

        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            Map<Long, Integer> basket = (Map<Long, Integer>) session.getAttribute("basket");
            if (basket == null || basket.isEmpty()) {
                logger.warn("Checkout attempted with empty basket by client {}", email);
                return ResponseEntity.badRequest().body(Map.of("error", "Basket is empty"));
            }

            BigDecimal total = BigDecimal.ZERO;
            for (Map.Entry<Long, Integer> entry : basket.entrySet()) {
                Book book = bookRepository.findById(entry.getKey())
                        .orElseThrow(() -> new RuntimeException("Book not found"));
                total = total.add(book.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
            }
            logger.debug("Calculated checkout total: {} for client {}", total, email);

            if (client.getBalance().compareTo(total) < 0) {
                logger.warn("Insufficient balance for client {}. Required: {}, Available: {}",
                        email, total, client.getBalance());
                return ResponseEntity.badRequest().body(Map.of("error", "Insufficient balance"));
            }

            client.setBalance(client.getBalance().subtract(total));
            clientRepository.save(client);
            logger.info("Checkout completed successfully for client {}. New balance: {}",
                    email, client.getBalance());

            session.removeAttribute("basket");
            return ResponseEntity.ok(Map.of("message", "Checkout successful"));
        } catch (Exception e) {
            logger.error("Checkout failed for client {}", email, e);
            throw e;
        }
    }

    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.warn("Account deletion requested for client {}", email);

        try {
            Client client = clientRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
            clientRepository.delete(client);
            logger.info("Account deleted for client {}", email);
            return ResponseEntity.ok(Map.of("message", "Account deleted"));
        } catch (Exception e) {
            logger.error("Account deletion failed for client {}", email, e);
            throw e;
        }
    }
}