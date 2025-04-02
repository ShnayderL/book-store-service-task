package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        logger.info("OrderController initialized with OrderRepository");
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        logger.info("Client placing new order for client ID: {}",
                order.getClient() != null ? order.getClient().getId() : "unknown");

        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", savedOrder.getId());

        return savedOrder;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping
    public List<Order> getAllOrders() {
        logger.debug("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        logger.info("Retrieved {} orders", orders.size());
        return orders;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        logger.debug("Looking for order with ID: {}", id);
        return orderRepository.findById(id)
                .map(order -> {
                    logger.info("Found order ID: {}", id);
                    return ResponseEntity.ok(order);
                })
                .orElseGet(() -> {
                    logger.warn("Order not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Long id) {
        logger.info("Attempting to confirm order ID: {}", id);

        return orderRepository.findById(id)
                .map(order -> {
                    // Add your confirmation logic here
                    logger.info("Order ID: {} confirmed successfully", id);
                    return ResponseEntity.ok("Order confirmed");
                })
                .orElseGet(() -> {
                    logger.error("Failed to confirm order - not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        logger.info("Attempting to cancel order ID: {}", id);

        return orderRepository.findById(id)
                .map(order -> {
                    // Add your cancellation logic here
                    logger.warn("Order ID: {} cancelled by employee", id);
                    return ResponseEntity.ok("Order cancelled");
                })
                .orElseGet(() -> {
                    logger.error("Failed to cancel order - not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}