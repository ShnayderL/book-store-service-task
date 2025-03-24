package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}/block")
    public ResponseEntity<String> blockClient(@PathVariable Long id) {
        // Logic to block a client (Implementation needed)
        return ResponseEntity.ok("Client blocked");
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PutMapping("/{id}/unblock")
    public ResponseEntity<String> unblockClient(@PathVariable Long id) {
        // Logic to unblock a client (Implementation needed)
        return ResponseEntity.ok("Client unblocked");
    }
}
