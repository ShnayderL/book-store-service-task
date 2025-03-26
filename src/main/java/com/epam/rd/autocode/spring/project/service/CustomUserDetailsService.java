package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import org.slf4j.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(ClientRepository clientRepository,
                                    EmployeeRepository employeeRepository,
                                    PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Searching for user with email: {}", email);

        // Шукаємо спочатку клієнта
        Optional<Client> client = clientRepository.findByEmail(email);
        if (client.isPresent()) {
            log.debug("Found client: {}", email);
            Client c = client.get();
            return User.withUsername(c.getEmail())
                    .password(c.getPassword())
                    .roles("CLIENT")
                    .build();
        }

        // Якщо клієнта не знайдено, шукаємо працівника
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        if (employee.isPresent()) {
            log.debug("Found employee: {}", email);
            Employee e = employee.get();
            return User.withUsername(e.getEmail())
                    .password(e.getPassword())
                    .roles("EMPLOYEE")
                    .build();
        }

        log.error("User not found with email: {}", email);
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
