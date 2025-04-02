package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.EmployeeDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ModelMapper modelMapper) {
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        logger.info("EmployeeService initialized with dependencies");
    }

    @Override
    public List<EmployeeDTO> getAllEmployees() {
        logger.debug("Fetching all employees");
        try {
            List<EmployeeDTO> employees = employeeRepository.findAll()
                    .stream()
                    .map(employee -> {
                        logger.trace("Mapping employee entity to DTO: {}", employee.getEmail());
                        return modelMapper.map(employee, EmployeeDTO.class);
                    })
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved {} employees", employees.size());
            return employees;
        } catch (Exception e) {
            logger.error("Failed to fetch employees", e);
            throw e;
        }
    }

    @Override
    public EmployeeDTO getEmployeeByEmail(String email) {
        logger.debug("Fetching employee by email: {}", email);
        try {
            Employee employee = employeeRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Employee not found with email: {}", email);
                        return new NotFoundException("Employee not found: " + email);
                    });
            logger.info("Successfully retrieved employee: {}", email);
            return modelMapper.map(employee, EmployeeDTO.class);
        } catch (Exception e) {
            logger.error("Error retrieving employee with email: {}", email, e);
            throw e;
        }
    }

    @Override
    public EmployeeDTO updateEmployeeByEmail(String email, EmployeeDTO employeeDTO) {
        logger.debug("Updating employee with email: {}", email);
        try {
            Employee employee = employeeRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Employee to update not found: {}", email);
                        return new NotFoundException("Employee not found: " + email);
                    });

            logger.debug("Original employee data - Name: {}, Phone: {}",
                    employee.getName(), employee.getPhone());

            employee.setName(employeeDTO.getName());
            // Password updates should be logged at DEBUG level only
            logger.debug("Updating employee password (hashed)");
            employee.setPassword(employeeDTO.getPassword());
            employee.setPhone(employeeDTO.getPhone());
            employee.setBirthDate(employeeDTO.getBirthDate());

            Employee updatedEmployee = employeeRepository.save(employee);
            logger.info("Successfully updated employee: {}", email);
            return modelMapper.map(updatedEmployee, EmployeeDTO.class);
        } catch (Exception e) {
            logger.error("Failed to update employee: {}", email, e);
            throw e;
        }
    }

    @Override
    public void deleteEmployeeByEmail(String email) {
        logger.debug("Attempting to delete employee: {}", email);
        try {
            Employee employee = employeeRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Employee to delete not found: {}", email);
                        return new NotFoundException("Employee not found: " + email);
                    });
            employeeRepository.delete(employee);
            logger.info("Successfully deleted employee: {}", email);
        } catch (Exception e) {
            logger.error("Failed to delete employee: {}", email, e);
            throw e;
        }
    }

    @Override
    public EmployeeDTO addEmployee(EmployeeDTO employeeDTO) {
        logger.debug("Attempting to add new employee with email: {}", employeeDTO.getEmail());
        try {
            // Check for existing employee
            employeeRepository.findByEmail(employeeDTO.getEmail())
                    .ifPresent(existing -> {
                        logger.warn("Duplicate employee creation attempt: {}", employeeDTO.getEmail());
                        throw new AlreadyExistException("Employee already exists with email: " + employeeDTO.getEmail());
                    });

            Employee employee = modelMapper.map(employeeDTO, Employee.class);
            Employee savedEmployee = employeeRepository.save(employee);
            logger.info("Successfully created new employee: {}", employeeDTO.getEmail());
            return modelMapper.map(savedEmployee, EmployeeDTO.class);
        } catch (Exception e) {
            logger.error("Failed to create new employee: {}", employeeDTO.getEmail(), e);
            throw e;
        }
    }
}
