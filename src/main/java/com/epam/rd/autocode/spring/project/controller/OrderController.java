package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public Order placeOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Long id) {
        // Logic to confirm order (Implementation needed)
        return ResponseEntity.ok("Order confirmed");
    }
}
