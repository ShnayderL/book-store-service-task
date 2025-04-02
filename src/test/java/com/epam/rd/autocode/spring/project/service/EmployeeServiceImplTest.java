package com.epam.rd.autocode.spring.project.service;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmail("employee@company.com");
        employee.setName("Jane Smith");

        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmail("employee@company.com");
        employeeDTO.setName("Jane Smith");

        employeeService = new EmployeeServiceImpl(employeeRepository, modelMapper);
    }

    @Test
    void deleteEmployeeByEmail_ShouldCallDelete() {
        // Arrange
        when(employeeRepository.findByEmail("employee@company.com")).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployeeByEmail("employee@company.com");

        // Assert
        verify(employeeRepository).delete(employee);
    }
}