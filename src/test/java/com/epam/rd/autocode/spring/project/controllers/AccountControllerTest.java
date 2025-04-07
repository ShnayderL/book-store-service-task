package com.epam.rd.autocode.spring.project.controllers;

import com.epam.rd.autocode.spring.project.controller.AccountController;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    private ClientRepository clientRepository;
    private EmployeeRepository employeeRepository;
    private PasswordEncoder passwordEncoder;
    private AccountController controller;

    private Model model;
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setup() {
        clientRepository = mock(ClientRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        controller = new AccountController(clientRepository, employeeRepository, passwordEncoder);

        model = mock(Model.class);
        redirectAttributes = mock(RedirectAttributes.class);
    }

    private Authentication createAuth(String email, String role) {
        return new Authentication() {
            @Override public Collection<? extends GrantedAuthority> getAuthorities() {
                return Set.of(() -> role);
            }
            @Override public Object getCredentials() { return null; }
            @Override public Object getDetails() { return null; }
            @Override public Object getPrincipal() { return null; }
            @Override public boolean isAuthenticated() { return true; }
            @Override public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {}
            @Override public String getName() { return email; }
        };
    }

    @Test
    void accountPage_shouldLoadClientData() {
        Authentication auth = createAuth("client@example.com", "ROLE_CLIENT");

        Client client = new Client(1L, "client@example.com", "pass", "Client Name", BigDecimal.TEN);
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));

        String view = controller.accountPage(model, auth);

        verify(model).addAttribute("user", client);
        verify(model).addAttribute("isClient", true);
        assertEquals("account", view);
    }

    @Test
    void accountPage_shouldLoadEmployeeData() {
        Authentication auth = createAuth("employee@example.com", "ROLE_EMPLOYEE");

        Employee employee = new Employee(2L, "employee@example.com", "pass", "Emp Name", null, null);
        when(employeeRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));

        String view = controller.accountPage(model, auth);

        verify(model).addAttribute("user", employee);
        verify(model).addAttribute("isClient", false);
        assertEquals("account", view);
    }

    @Test
    void updateProfile_shouldUpdateClientProfile() {
        Authentication auth = createAuth("client@example.com", "ROLE_CLIENT");

        Client client = new Client(1L, "client@example.com", "oldpass", "Old Name", BigDecimal.ONE);
        when(clientRepository.findByEmail("client@example.com")).thenReturn(Optional.of(client));
        when(clientRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");

        String view = controller.updateProfile("New Name", "new@example.com", "newpass", auth, redirectAttributes);

        verify(clientRepository).save(client);
        verify(redirectAttributes).addFlashAttribute("success", "Profile updated successfully!");
        assertEquals("redirect:/account", view);
        assertEquals("New Name", client.getName());
        assertEquals("new@example.com", client.getEmail());
        assertEquals("encodedpass", client.getPassword());
    }

    @Test
    void updateProfile_shouldUpdateEmployeeWithoutPassword() {
        Authentication auth = createAuth("emp@example.com", "ROLE_EMPLOYEE");

        Employee emp = new Employee(2L, "emp@example.com", "pass", "Emp", null, null);
        when(employeeRepository.findByEmail("emp@example.com")).thenReturn(Optional.of(emp));
        when(employeeRepository.findByEmail("emp2@example.com")).thenReturn(Optional.empty());

        String view = controller.updateProfile("Emp2", "emp2@example.com", "", auth, redirectAttributes);

        verify(employeeRepository).save(emp);
        verify(redirectAttributes).addFlashAttribute("success", "Profile updated successfully!");
        assertEquals("Emp2", emp.getName());
        assertEquals("emp2@example.com", emp.getEmail());
    }
}
